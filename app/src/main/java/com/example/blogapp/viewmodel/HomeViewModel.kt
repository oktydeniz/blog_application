package com.example.blogapp.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.blogapp.model.Post
import com.example.blogapp.model.User
import com.example.blogapp.network.Constants
import com.example.blogapp.util.NetworkState
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class HomeViewModel(application: Application) : BaseViewModel(application) {
    val postMLDModel = MutableLiveData<ArrayList<Post>>()
    private val postArrayList = ArrayList<Post>()
    val isSuccess = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()
    private val sharedPreferences = application.getSharedPreferences("user", Context.MODE_PRIVATE)
    private var volleyRequestQueue: RequestQueue = Volley.newRequestQueue(application)


    fun getData() {
        if (NetworkState.isNetworkAvailable(getApplication())) {
            isSuccess.value = true
            isLoading.value = true
            getDataAPI()
        } else {
            isSuccess.value = false
            isLoading.value = false
        }
    }

    private fun getDataAPI() {
        postArrayList.clear()
        launch {
            val strRequestQueue: StringRequest = object : StringRequest(
                Method.GET, Constants.posts,
                Response.Listener { response ->
                    try {
                        val responseObj = JSONObject(response)
                        if (responseObj.getBoolean("success")) {
                            val arrayJson = JSONArray(responseObj.getString("posts"))
                            for (i in 0 until arrayJson.length()) {
                                val postObject = arrayJson.getJSONObject(i)
                                val userObject = postObject.getJSONObject("user")

                                val user = User(
                                    userObject.getInt("id"),
                                    (userObject.getString("name") + " " + userObject.getString("lastName")),
                                    userObject.getString("photo")
                                )
                                val post = Post(
                                    postObject.getInt("id"),
                                    postObject.getInt("likecounts"),
                                    postObject.getInt("commentcounts"),
                                    postObject.getString("created_at"),
                                    postObject.getString("desc"),
                                    postObject.getString("photo"),
                                    user,
                                    postObject.getBoolean("selflike"),
                                )
                                postArrayList.add(post)
                            }
                            postMLDModel.value = postArrayList
                        }
                        isSuccess.value = true
                        isLoading.value = false
                    } catch (e: Exception) {
                        Log.e(
                            "HomeViewModel",
                            "problem occurred From Try, volley error: " + e.message
                        )

                    }
                }, Response.ErrorListener {
                    Log.e("HomeViewModel", "problem occurred, volley error: " + it.message)
                    isSuccess.value = false
                    isLoading.value = false
                }
            ) {

                override fun getHeaders(): MutableMap<String, String> {
                    val map = HashMap<String, String>()
                    val token = sharedPreferences.getString("token", "")
                    map["Authorization"] = "Bearer $token"
                    return map
                }
            }

            strRequestQueue.retryPolicy = DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 1f
            )
            volleyRequestQueue.add(strRequestQueue)
        }

    }
}