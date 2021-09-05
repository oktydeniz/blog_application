package com.example.blogapp.views.mainapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.blogapp.adapter.CommentAdapter
import com.example.blogapp.adapter.PostsAdapter
import com.example.blogapp.databinding.FragmentCommentBinding
import com.example.blogapp.model.Comment
import com.example.blogapp.model.Post
import com.example.blogapp.viewmodel.CommentViewModel

class CommentFragment : Fragment() {
    private var _binding: FragmentCommentBinding? = null
    private val binding get() = _binding!!
    private val args: CommentFragmentArgs by navArgs()
    private var post: Int? = null
    private lateinit var viewModel: CommentViewModel
    private var arrayList = ArrayList<Comment>()
    private  val TAG = "CommentFragment"
    private lateinit var adapter: CommentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.let {
            post = it.postId
            viewModel = ViewModelProvider(this).get(CommentViewModel::class.java)
            viewModel.getComment(post!!)
            Log.i(TAG, "onViewCreated:  $post")
            observeData()
        }


    }

    private fun observeData() {
        viewModel.commentLiveData.observe(viewLifecycleOwner, {
            it.let {
                binding.recyclerComments.visibility = View.VISIBLE
                arrayList.addAll(it)
                setAdapter(it)
            }
        })
        viewModel.isSuccess.observe(viewLifecycleOwner, {
            it.let {
                if (it) {
                    binding.commentWrongText.visibility = View.GONE
                } else {
                    binding.commentWrongText.visibility = View.VISIBLE

                }
            }
        })
        viewModel.isLoading.observe(viewLifecycleOwner, {
            it.let {
                if (it) {
                    binding.progressComment.visibility = View.VISIBLE
                } else {
                    binding.progressComment.visibility = View.GONE
                }
            }
        })
    }

    private fun setAdapter(arrayList: ArrayList<Comment>) {
        adapter = CommentAdapter(arrayList)
        binding.recyclerComments.adapter = adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}