package com.example.blogapp.adapter

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.blogapp.databinding.LoyoutCommentItemBinding
import com.example.blogapp.model.Comment
import com.example.blogapp.network.Constants
import org.json.JSONObject

class CommentAdapter(private val list: ArrayList<Comment>) :
    RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    private lateinit var context: Context
    private val TAG = "CommentAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding = LoyoutCommentItemBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = list[position]
        holder.v.comment = comment
        holder.v.executePendingBindings()

        if (context.getSharedPreferences("user", Context.MODE_PRIVATE)
                .getInt("id", 0) != comment.user.id
        ) {
            holder.v.btnDeleteComment.visibility = View.GONE
        } else {
            holder.v.btnDeleteComment.visibility = View.VISIBLE
        }
        holder.v.btnDeleteComment.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.apply {
                setMessage("You will delete this comment")
                setTitle("Delete Comment")
                setPositiveButton(
                    "Delete"
                ) { _, _ -> deleteComment(comment.id, position) }
                setNegativeButton("Cancel") { _, _ -> }
            }
            builder.show()

        }
    }

    private fun deleteComment(id: Int, position: Int) {
        val volley: RequestQueue = Volley.newRequestQueue(context)
        val strRequest: StringRequest =
            object : StringRequest(Method.POST, Constants.deleteComments,
                Response.Listener { response ->
                    try {
                        val `object` = JSONObject(response)
                        if (`object`.getBoolean("success")) {
                            list.remove(list[position])
                            notifyDataSetChanged()
                            notifyItemRemoved(position)

                        }
                    } catch (e: Exception) {
                        Log.i(TAG, "deleteComment:  $id and $position   ${e.localizedMessage}")
                    }


                }, Response.ErrorListener {
                    Log.i(TAG, "deleteComment: Error  ${it.localizedMessage} ")
                }) {

                override fun getParams(): MutableMap<String, String> {
                    val map = HashMap<String, String>()
                    map["id"] = "$id"
                    return map
                }

                override fun getHeaders(): MutableMap<String, String> {
                    val map = HashMap<String, String>()
                    val sharedPreferences =
                        context.getSharedPreferences("user", Context.MODE_PRIVATE)
                    val token = sharedPreferences.getString("token", "")
                    map["Authorization"] = "Bearer $token"
                    return map
                }
            }

        strRequest.retryPolicy = DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 1f)
        volley.add(strRequest)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val v: LoyoutCommentItemBinding) : RecyclerView.ViewHolder(v.root)


}