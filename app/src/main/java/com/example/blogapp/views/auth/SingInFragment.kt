package com.example.blogapp.views.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.blogapp.AppActivity
import com.example.blogapp.databinding.FragmentSingInBinding
import com.example.blogapp.network.Constants
import org.json.JSONObject


class SingInFragment : Fragment() {
    private var _binding: FragmentSingInBinding? = null
    private val binding get() = _binding!!
    private var volleyRequestQueue: RequestQueue? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSingInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.singInBtn.setOnClickListener {
            init()
        }
        binding.dontHaveAccountText.setOnClickListener {
            val directions = SingInFragmentDirections.actionSingInFragmentToSingUpFragment()
            Navigation.findNavController(it).navigate(directions)
        }
    }

    private fun init() {

        volleyRequestQueue = Volley.newRequestQueue(context)

        val strReq: StringRequest = object : StringRequest(
            Method.POST, Constants.loginURL,
            Response.Listener { response ->


                try {
                    val responseObj = JSONObject(response)
                    if (responseObj.getBoolean("success")) {

                        val jsonObject = responseObj.getJSONObject("user")
                        val preferences =
                            requireActivity().applicationContext.getSharedPreferences(
                                "user",
                                Context.MODE_PRIVATE
                            )
                        val editor = preferences.edit()
                        editor.putString("token", responseObj.getString("token"))
                        editor.putString("name", jsonObject.getString("name"))
                        editor.putInt("id", jsonObject.getInt("id"))
                        editor.putString("lastName", jsonObject.getString("lastName"))
                        editor.putString("photo", jsonObject.getString("photo"))
                        editor.putBoolean("isLoggedIn", true)
                        editor.apply()

                        Toast.makeText(
                            requireContext(),
                            "Login Success Welcome ${jsonObject.getString("name")}",
                            Toast.LENGTH_SHORT
                        )
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

            override fun getParams(): Map<String, String> {
                val map: HashMap<String, String> = HashMap()
                map["email"] = binding.singInEmailInput.text.toString().trim()
                map["password"] = binding.singInPasswordInput.text.toString().trim()
                return map
            }
        }
        strReq.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 1f
        )
        volleyRequestQueue?.add(strReq)

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }
}