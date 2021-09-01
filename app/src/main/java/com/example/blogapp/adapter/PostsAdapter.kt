package com.example.blogapp.adapter

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.blogapp.R
import com.example.blogapp.databinding.PostItemLayoutBinding
import com.example.blogapp.model.Post
import com.example.blogapp.network.Constants
import com.example.blogapp.views.mainapp.HomeFragmentDirections
import org.json.JSONObject
import java.lang.Exception
import kotlin.collections.ArrayList

class PostsAdapter(
    private val list: ArrayList<Post>
) : RecyclerView.Adapter<PostsAdapter.ViewHolder>() {
    var c: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        c = parent.context
        val binding = PostItemLayoutBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = list[position]
        holder.v.post = post
        val id = c?.getSharedPreferences("user", Context.MODE_PRIVATE)?.getInt("id", 0)
        if (id == post.user.id) {
            holder.v.btnPostOption.visibility = View.VISIBLE
        } else {
            holder.v.btnPostOption.visibility = View.GONE
        }
        holder.v.btnPostOption.setOnClickListener { view ->
            val menu = PopupMenu(c, holder.v.btnPostOption)
            menu.inflate(R.menu.pop_up_options)
            menu.setOnMenuItemClickListener {
                if (it.itemId == R.id.optionDelete) {
                    deletePost(post.id, position)
                }
                if (it.itemId == R.id.optionEdit) {
                    val direction =
                        HomeFragmentDirections.actionHomeFragmentToEditPostFragment(post)
                    Navigation.findNavController(view).navigate(direction)
                    Toast.makeText(c, post.user.userName, Toast.LENGTH_SHORT).show()
                }
                return@setOnMenuItemClickListener false
            }
            menu.show()
        }
    }

    private fun deletePost(id: Int, position: Int) {
        val builder = AlertDialog.Builder(c)
        builder.setTitle("Confirm")
        builder.setMessage("Delete Post ? ")
        builder.setPositiveButton(
            "Delete"
        ) { _, _ ->
            val volley = Volley.newRequestQueue(c)
            val request: StringRequest = object : StringRequest(
                Method.POST,
                Constants.deletePost,
                Response.Listener { response ->
                    try {
                        val jsonObject = JSONObject(response)
                        if (jsonObject.getBoolean("success")) {
                            Toast.makeText(c, "Post Deleted", Toast.LENGTH_SHORT)
                                .show()

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, Response.ErrorListener {
                    Log.e("Adapter Log ", "problem occurred, volley error: " + it.message)

                }

            ) {

                override fun getParams(): MutableMap<String, String> {
                    val map = HashMap<String, String>()
                    map["id"] = "$id"
                    Log.i("Adapter Log ", "getParams: $id ")
                    return map
                }

                override fun getHeaders(): MutableMap<String, String> {
                    val map = HashMap<String, String>()
                    val token =
                        c?.getSharedPreferences("user", Context.MODE_PRIVATE)
                            ?.getString("token", "")
                    map["Authorization"] = "Bearer $token"
                    Log.i("Adapter Log ", "getParams: $token ")
                    return map
                }

            }
            request.retryPolicy = DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 1f)
            volley?.add(request)
        }
        builder.setNegativeButton(
            "Cancel",
        ) { _, _ ->

        }
        builder.show()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(var v: PostItemLayoutBinding) : RecyclerView.ViewHolder(v.root)
}