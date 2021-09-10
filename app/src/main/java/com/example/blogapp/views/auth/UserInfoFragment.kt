package com.example.blogapp.views.auth

import android.Manifest
import android.app.Activity.MODE_PRIVATE
import android.app.Activity.RESULT_OK
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
import com.example.blogapp.AppActivity
import com.example.blogapp.databinding.FragmentUserInfoBinding
import com.example.blogapp.network.Constants
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import kotlin.collections.HashMap

class UserInfoFragment : Fragment() {
    private var _binding: FragmentUserInfoBinding? = null
    private val binding get() = _binding!!
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var selectedBitmap: Bitmap? = null
    private var volley: RequestQueue? = null
    private var userShared: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserInfoBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerLauncher()
        userShared = requireActivity().getSharedPreferences("user", MODE_PRIVATE)

        binding.selectProfilePhoto.setOnClickListener {
            selectImage(it)
        }
        binding.userInfoSaveBtn.setOnClickListener {
            saveInfo()
        }
    }


    private fun selectImage(v: View) {
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
                //rationale
                Snackbar.make(v, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Give Permission") {
                        //request permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

                    }.show()

            } else {
                //request permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

            }
        } else {
            val intentToGallery =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)

        }
    }

    private fun saveInfo() {
        val name = binding.userInfoNameInput.text.toString()
        val lastName = binding.userInfoLastNameInput.text.toString()
        var str = ""
        if (selectedBitmap != null) {
            val byteArrayOutputStream = ByteArrayOutputStream()
            selectedBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val bytArray = byteArrayOutputStream.toByteArray()
            str = Base64.encodeToString(bytArray, Base64.DEFAULT)

        }
        sendRequest(name, lastName, str)
    }

    private fun sendRequest(name: String, lastName: String, str: String) {
        volley = Volley.newRequestQueue(requireContext())
        val strRequest: StringRequest = object : StringRequest(
            Method.POST,
            Constants.saveUserInfoURL,
            Response.Listener { response ->
                try {
                    val responseObj = JSONObject(response)
                    if (responseObj.getBoolean("success")) {
                        val preferences =
                            requireActivity().applicationContext.getSharedPreferences(
                                "user",
                                Context.MODE_PRIVATE
                            )
                        val editor = preferences.edit()
                        editor.putString("photo", responseObj.getString("photo"))
                        editor.apply()
                        Toast.makeText(requireContext(), "Welcome $name ", Toast.LENGTH_SHORT)
                            .show()
                        startActivity(Intent(requireContext(), AppActivity::class.java))
                        requireActivity().finish()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {

            }) {
            override fun getParams(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["name"] = name
                map["lastName"] = lastName
                map["photo"] = str
                return map

            }

            override fun getHeaders(): MutableMap<String, String> {
                val token = userShared?.getString("token", "")
                val map = HashMap<String, String>()
                map["Authorization"] = "Bearer $token"
                return map
            }

        }
        strRequest.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 1f
        )
        volley?.add(strRequest)


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }

    private fun registerLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intentForResult = result.data
                    intentForResult?.let { it ->
                        val imgData = it.data
                        imgData?.let { img ->
                            try {
                                if (Build.VERSION.SDK_INT >= 28) {
                                    val source = ImageDecoder.createSource(
                                        requireActivity().contentResolver,
                                        img
                                    )
                                    selectedBitmap = ImageDecoder.decodeBitmap(source)
                                    binding.profileImage.setImageBitmap(selectedBitmap)
                                } else {
                                    selectedBitmap = MediaStore.Images.Media.getBitmap(
                                        requireActivity().contentResolver,
                                        img
                                    )
                                    binding.profileImage.setImageBitmap(selectedBitmap)
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


}