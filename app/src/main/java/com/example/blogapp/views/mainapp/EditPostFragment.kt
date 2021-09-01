package com.example.blogapp.views.mainapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.blogapp.R
import com.example.blogapp.databinding.FragmentEditPostBinding
import com.example.blogapp.model.Post
import com.example.blogapp.network.Constants
import org.json.JSONObject

class EditPostFragment : Fragment() {

    private val args: EditPostFragmentArgs by navArgs()
    private var _binding: FragmentEditPostBinding? = null
    private val binding get() = _binding!!
    private var post: Post? = null
    private val TAG = "EditPostFragment"
    private lateinit var sharedPreferences: SharedPreferences
    private var volley: RequestQueue? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.let {
            post = it.editPost
        }
        init()
    }

    private fun init() {
        sharedPreferences = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)

        post?.let {
            binding.post = it
        }
        binding.updateBtn.setOnClickListener {
            updatePost()
        }
    }

    private fun updatePost() {
        val desc = binding.newPostUpdate.text.toString()
        volley = Volley.newRequestQueue(requireContext())
        val request: StringRequest = object : StringRequest(
            Method.POST,
            Constants.updatePost,
            Response.Listener { response ->

                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean("success")) {
                        Toast.makeText(requireContext(), "Post Updated", Toast.LENGTH_SHORT)
                            .show()
                        findNavController().navigate(R.id.action_editPostFragment_to_homeFragment)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener {
                Log.e(TAG, "problem occurred, volley error: " + it.message)

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                val token = sharedPreferences.getString("token", "")
                map["Authorization"] = "Bearer $token"
                Log.i(TAG, "getHeaders: Bearer $token")
                return map
            }

            override fun getParams(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["id"] = "${post?.id}"
                map["desc"] = desc
                return map
            }
        }
        request.retryPolicy = DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 1f)
        volley?.add(request)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPostBinding.inflate(inflater, container, false)
        return binding.root
    }
}