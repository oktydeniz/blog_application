package com.example.blogapp.views.mainapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.blogapp.R
import com.example.blogapp.databinding.FragmentNewPostBinding
import com.example.blogapp.network.Constants
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.lang.Exception

class NewPostFragment : Fragment() {
    private var _binding: FragmentNewPostBinding? = null
    private val binding get() = _binding!!
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var bitmap: Bitmap? = null
    private var volley: RequestQueue? = null
    private var sharedPreferences: SharedPreferences? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPostBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)
        registerLauncher()
        actions()
    }

    private fun actions() {
        binding.postImageNew.setOnClickListener {
            selectImage(it)
        }
        binding.sendNewPost.setOnClickListener {
            sendPost()
        }
    }

    private fun registerLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { r ->
                if (r.resultCode == Activity.RESULT_OK) {
                    val intentForResult = r.data
                    intentForResult?.let {
                        val imgData = it.data
                        imgData?.let { img ->
                            try {
                                if (Build.VERSION.SDK_INT >= 28) {
                                    val source = ImageDecoder.createSource(
                                        requireActivity().contentResolver,
                                        img
                                    )
                                    bitmap = ImageDecoder.decodeBitmap(source)
                                    binding.postImageNew.setImageBitmap(bitmap)
                                } else {
                                    bitmap = MediaStore.Images.Media.getBitmap(
                                        requireActivity().contentResolver,
                                        img
                                    )
                                    binding.postImageNew.setImageBitmap(bitmap)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                    }
                }
            }
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) {
                    val intentToGallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                } else {
                    Toast.makeText(requireContext(), "Permission needed!", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun sendPost() {
        val desc = binding.editTextTextNewPost.text.toString()
        var str = ""
        if (bitmap != null) {
            val bAOS = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, bAOS)
            val byteArray = bAOS.toByteArray()
            str = Base64.encodeToString(byteArray, Base64.DEFAULT)
        }
        sendRequest(desc, str)
    }

    private fun sendRequest(desc: String, str: String) {
        volley = Volley.newRequestQueue(requireContext())
        val request: StringRequest = object : StringRequest(
            Method.POST,
            Constants.createPost,
            Response.Listener { response ->
                try {
                    val responseObject = JSONObject(response)
                    if (responseObject.getBoolean("success")) {
                        Toast.makeText(requireContext(), "Posted !", Toast.LENGTH_SHORT)
                            .show()
                        bitmap = null
                        binding.postImageNew.setImageResource(R.drawable.ic_baseline_image_search_24)
                        binding.editTextTextNewPost.setText("")
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "ups! Some thing went wrong. Try Again",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener {

            }) {
            override fun getParams(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["desc"] = desc
                map["photo"] = str
                return map
            }

            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                val token = sharedPreferences?.getString("token", "")
                map["Authorization"] = "Bearer $token"
                return map
            }
        }
        request.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 1f
        )
        volley?.add(request)

    }

    private fun selectImage(v: View) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                Snackbar.make(v, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Give Permission") {
                        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    }.show()
            } else {
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            val intentToGallery =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }
}