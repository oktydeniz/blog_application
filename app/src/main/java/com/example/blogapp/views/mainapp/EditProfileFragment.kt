package com.example.blogapp.views.mainapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
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
import com.example.blogapp.databinding.FragmentEditProfileBinding
import com.example.blogapp.network.Constants
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.lang.Exception

class EditProfileFragment : Fragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var bitmap: Bitmap? = null
    private var volley: RequestQueue? = null
    private var userShared: SharedPreferences? = null
    private val TAG = "EditProfileFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerLauncher()
        userShared = requireActivity().getSharedPreferences("user", Activity.MODE_PRIVATE)
        binding.updateProfilePhoto.setOnClickListener {
            selectImage(it)
        }
        binding.userInfoUpdateBtn.setOnClickListener {
            saveUpdates()
        }
    }

    private fun registerLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intentForResult = result.data
                    intentForResult?.let {
                        val nImg = it.data
                        nImg?.let { img ->
                            try {
                                bitmap = if (Build.VERSION.SDK_INT >= 28) {
                                    val source = ImageDecoder.createSource(
                                        requireActivity().contentResolver,
                                        img
                                    )
                                    ImageDecoder.decodeBitmap(source)

                                } else {
                                    MediaStore.Images.Media.getBitmap(
                                        requireActivity().contentResolver,
                                        img
                                    )
                                }
                                binding.newProfileImage.setImageBitmap(bitmap)

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

    private fun saveUpdates() {
        val name = binding.userInfoNameUpdateInput.text.toString()
        val lastName = binding.userInfoLastNameUpdateInput.text.toString()
        var src = ""
        if (bitmap != null) {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            src = Base64.encodeToString(byteArray, Base64.DEFAULT)
        }
        sendRequest(name, lastName, src)
    }

    private fun sendRequest(name: String, lastName: String, src: String) {
        volley = Volley.newRequestQueue(requireContext())
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            Constants.saveUserInfoURL,
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean("success")) {
                        val editor: SharedPreferences.Editor = userShared!!.edit()
                        editor.putString("name", name)
                        editor.putString("lastName", lastName)
                        editor.putString("photo",jsonObject.getString("photo"))
                        editor.apply()
                        requireActivity().finish()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener {
                Log.e(TAG, "problem occurred, volley error: " + it.message)
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["name"] = name
                map["lastName"] = lastName
                map["photo"] = src
                return map

            }

            override fun getHeaders(): MutableMap<String, String> {
                val token = userShared?.getString("token", "")
                val map = HashMap<String, String>()
                map["Authorization"] = "Bearer $token"
                return map
            }
        }

        stringRequest.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 1f
        )
        volley?.add(stringRequest)
    }

    private fun selectImage(it: View) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                Snackbar.make(it, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Give Permission") {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

                    }.show()

            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            val intentToGallery =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}