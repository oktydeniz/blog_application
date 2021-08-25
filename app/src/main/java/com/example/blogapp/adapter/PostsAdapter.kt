package com.example.blogapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.blogapp.databinding.PostItemLayoutBinding
import com.example.blogapp.model.Post

class PostsAdapter(
    private val list: ArrayList<Post>
) : RecyclerView.Adapter<PostsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PostItemLayoutBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = list[position]
        holder.bind(post)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder constructor(private val binding: PostItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Post) {
            binding.post = item
            binding.executePendingBindings()
        }
    }
}