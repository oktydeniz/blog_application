package com.example.blogapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.blogapp.databinding.LayoutAccountPostItemBinding
import com.example.blogapp.model.Post

class ProfilePostAdapter(private var list: ArrayList<Post>) :
    RecyclerView.Adapter<ProfilePostAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutAccountPostItemBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = list[position]
        holder.binding.photo = post.photo
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(var binding: LayoutAccountPostItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}