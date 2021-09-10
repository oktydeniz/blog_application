package com.example.blogapp.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.blogapp.model.Comment
import com.example.blogapp.model.User
import com.example.blogapp.network.Constants
import com.example.blogapp.util.NetworkState
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class CommentViewModel(application: Application) : BaseViewModel(application) {
    val commentLiveData = MutableLiveData<ArrayList<Comment>>()
    var commentList = ArrayList<Comment>()
    val isSuccess = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()
    private val sharedPreferences =
        application.getSharedPreferences("user", Context.MODE_PRIVATE).getString("token", "")
    private var volleyRequest: RequestQueue = Volley.newRequestQueue(application)

    fun getComment(id: Int) {
        if (NetworkState.isNetworkAvailable(getApplication())) {
            isLoading.value = true
            isSuccess.value = true
            getData(id)
        } else {
            isSuccess.value = false
            isLoading.value = false
        }
    }

    private fun getData(id: Int) {
        commentList.clear()
        launch {
            val strRequest: StringRequest = object : StringRequest(
                Method.POST,
                Constants.comments,
                Response.Listener { res ->
                    try {
                        val responseObject = JSONObject(res)
                        if (responseObject.getBoolean("success")) {
                            val arrayJson = JSONArray(responseObject.getString("comments"))
                            for (i in 0 until arrayJson.length()) {
                                val commentObject = arrayJson.getJSONObject(i)
                                val userObject = commentObject.getJSONObject("user")

                                val user = User(
                                    userObject.getInt("id"),
                                    (userObject.getString("name") + " " + userObject.getString("lastName")),
                                    userObject.getString("photo")
                                )

                                val comment = Comment(
                                    commentObject.getInt("id"),
                                    commentObject.getString("comment"),
                                    commentObject.getString("created_at"),
                                    user
                                )
                                commentList.add(comment)

                            }
                            commentLiveData.value = commentList
                        }
                        isSuccess.value = true
                        isLoading.value = false
                    } catch (e: Exception) {
                    }

                }, Response.ErrorListener {
                    isSuccess.value = false
                    isLoading.value = false
                }
            ) {

                override fun getHeaders(): MutableMap<String, String> {
                    val map = HashMap<String, String>()
                    map["Authorization"] = "Bearer $sharedPreferences"

                    return map
                }

                override fun getParams(): MutableMap<String, String> {
                    val map = HashMap<String, String>()
                    map["id"] = "$id"

                    return map
                }

            }

            strRequest.retryPolicy = DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 1f
            )
            volleyRequest.add(strRequest)
        }
    }

    fun sendComment(postId: Int, comment: String) {
        launch {
            val stringRequest: StringRequest =
                object : StringRequest(
                    Method.POST,
                    Constants.createComments,
                    Response.Listener { response ->
                        try {
                            val jsonObject = JSONObject(response)
                            if (jsonObject.getBoolean("success")) {
                                val newComment = jsonObject.getJSONObject("comment")
                                val user = newComment.getJSONObject("user")
                                val userNew = User(
                                    user.getInt("id"),
                                    (user.getString("name") + " " + user.getString("lastName")),
                                    user.getString("photo")
                                )

                                val commentNew = Comment(
                                    newComment.getInt("id"),
                                    newComment.getString("comment"),
                                    newComment.getString("created_at"),
                                    userNew
                                )
                                commentList.add(commentNew)
                                commentLiveData.value = commentList
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()

                        }
                    },
                    Response.ErrorListener {

                    }) {
                    override fun getParams(): MutableMap<String, String> {
                        val map = HashMap<String, String>()
                        map["post_id"] = "$postId"
                        map["comment"] = comment
                        return map

                    }

                    override fun getHeaders(): MutableMap<String, String> {
                        val map = HashMap<String, String>()
                        map["Authorization"] = "Bearer $sharedPreferences"
                        return map
                    }
                }

            stringRequest.retryPolicy = DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 1f
            )
            volleyRequest.add(stringRequest)
        }

    }
}