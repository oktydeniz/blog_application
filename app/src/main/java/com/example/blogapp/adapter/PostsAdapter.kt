package com.example.blogapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.blogapp.R
import com.example.blogapp.databinding.PostItemLayoutBinding
import com.example.blogapp.model.Post
import com.example.blogapp.views.mainapp.HomeFragmentDirections
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
                    Toast.makeText(c, post.desc, Toast.LENGTH_SHORT).show()
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

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(var v: PostItemLayoutBinding) : RecyclerView.ViewHolder(v.root)
}