package com.example.blogapp.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.blogapp.MainActivity
import com.example.blogapp.model.Post
import com.example.blogapp.model.User
import com.example.blogapp.network.Constants
import com.example.blogapp.util.NetworkState
import kotlinx.coroutines.launch
import org.json.JSONObject

class UserProfileViewModel(application: Application) : BaseViewModel(application) {

    val user = MutableLiveData<User>()
    var textPost = MutableLiveData<Boolean>()
    var posts = MutableLiveData<ArrayList<Post>>()
    var postsArray = ArrayList<Post>()
    private val sharedPreferences =
        application.getSharedPreferences("user", Context.MODE_PRIVATE)
    private var volleyRequestQueue: RequestQueue = Volley.newRequestQueue(application)

    fun getUserData() {
        if (NetworkState.isNetworkAvailable(getApplication())) {
            getDataLocal()
            getDataUrl()
            textPost.value = true
        } else {
            textPost.value = false
        }
    }

    private fun getDataUrl() {
        postsArray.clear()
        launch {
            val stringRequest: StringRequest = object : StringRequest(
                Method.GET,
                Constants.showMyPosts,
                Response.Listener { response ->
                    try {
                        val `object` = JSONObject(response)
                        if (`object`.getBoolean("success")) {
                            val arrayJson = `object`.getJSONArray("posts")
                            val userJson = `object`.getJSONObject("user")
                            val user = User(
                                userJson.getInt("id"),
                                userJson.getString("name") + " " + userJson.getString("lastName"),
                                userJson.getString("photo")
                            )
                            for (i in 0 until arrayJson.length()) {
                                val jsonPost = arrayJson.getJSONObject(i)
                                val post = Post(
                                    jsonPost.getInt("id"),
                                    photo = jsonPost.getString("photo"),
                                    desc = jsonPost.getString("desc"),
                                    user = user,
                                    selfLike = false,
                                    comments = 0,
                                    likes = 0,
                                    date = jsonPost.getString("created_at")
                                )
                                postsArray.add(post)
                            }
                            posts.value = postsArray
                        }
                    } catch (e: Exception) {

                    }
                }, Response.ErrorListener {

                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val map = HashMap<String, String>()
                    val token = sharedPreferences.getString("token", "")
                    map["Authorization"] = "Bearer $token"
                    return map
                }

            }
            stringRequest.retryPolicy = DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 1f
            )
            volleyRequestQueue.add(stringRequest)
        }

    }

    private fun getDataLocal() {
        user.value = sharedPreferences.getString("photo", "")?.let {
            User(
                sharedPreferences.getInt("id", 0),
                sharedPreferences.getString("name", "") + " " + sharedPreferences.getString(
                    "lastName",
                    ""
                ),
                it
            )
        }
    }

    fun logOut(c:Context,a:Activity) {
        launch {
            val request: StringRequest = object : StringRequest(
                Method.POST,
                Constants.logOut,
                Response.Listener { res ->
                    try {
                        val responseJSONObject = JSONObject(res)
                        if (responseJSONObject.getBoolean("success")) {
                            val editor: SharedPreferences.Editor = sharedPreferences.edit()
                            editor.clear()
                            editor.apply()
                            Toast.makeText(getApplication(), "Logged out", Toast.LENGTH_SHORT)
                                .show()
                            val intent = Intent(c, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            a.startActivity(intent)
                            a.finish()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()

                    }
                },
                Response.ErrorListener {

                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val map = HashMap<String, String>()
                    val token = sharedPreferences.getString("token", "")
                    map["Authorization"] = "Bearer $token"
                    return map
                }

            }
            request.retryPolicy = DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 1f
            )
            volleyRequestQueue.add(request)
        }
    }

}