package com.example.blogapp.views.mainapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.blogapp.R
import com.example.blogapp.databinding.FragmentEditPostBinding
import com.example.blogapp.model.Post

class EditPostFragment : Fragment() {

    private val args: EditPostFragmentArgs by navArgs()
    private var _binding: FragmentEditPostBinding? = null
    private val binding get() = _binding!!
    private var post: Post? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.let {
            post = it.editPost
        }
        init()
    }

    private fun init() {
        post?.let {
            binding.editPostArea.text = post?.desc
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPostBinding.inflate(inflater, container, false)
        return binding.root
    }
}