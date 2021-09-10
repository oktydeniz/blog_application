package com.example.blogapp.views.auth

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.blogapp.R
import com.example.blogapp.databinding.FragmentSingUpBinding
import com.example.blogapp.network.Constants
import org.json.JSONObject

class SingUpFragment : Fragment() {

    private var _binding: FragmentSingUpBinding? = null
    private val binding get() = _binding!!
    private var volleyRequestQueue: RequestQueue? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSingUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.singUpBtn.setOnClickListener {
            init()
        }
    }

    private fun init() {
        volleyRequestQueue = Volley.newRequestQueue(context)
        val strRequest: StringRequest = object : StringRequest(
            Method.POST,
            Constants.registerURL,
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
                        editor.putString("token", responseObj.getString("token"))

                        editor.putBoolean("isLoggedIn", true)
                        editor.apply()
                        Toast.makeText(requireContext(), "Register Success", Toast.LENGTH_SHORT)
                            .show()
                        val controller = findNavController()
                        controller.navigate(R.id.action_singUpFragment_to_userInfoFragment)
                    }
                } catch (e: Exception) {

                    e.printStackTrace()
                }
            },
            Response.ErrorListener {

            }) {
            override fun getParams(): Map<String, String> {
                val map: HashMap<String, String> = HashMap()
                map["email"] = binding.singUpMailInput.text.toString().trim()
                map["password"] = binding.singUpPasswordInputMain.text.toString().trim()
                map["password_confirmation"] =
                    binding.singUpPasswordInputConfirmed.text.toString().trim()

                return map
            }
        }
        strRequest.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 1f
        )
        volleyRequestQueue?.add(strRequest)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}