package com.example.blogapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.blogapp.databinding.LoyoutCommentItemBinding
import com.example.blogapp.model.Comment

class CommentAdapter(private val list: ArrayList<Comment>) :
    RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    private lateinit var context: Context

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
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val v: LoyoutCommentItemBinding) : RecyclerView.ViewHolder(v.root)


}