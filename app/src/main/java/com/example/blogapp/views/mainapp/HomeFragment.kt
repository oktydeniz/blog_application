package com.example.blogapp.views.mainapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.blogapp.adapter.PostsAdapter
import com.example.blogapp.databinding.FragmentHomeBinding
import com.example.blogapp.viewmodel.HomeViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: PostsAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.getData()
        observeData()

    }

    private fun observeData() {
        viewModel.postMLDModel.observe(viewLifecycleOwner, {
            it.let {
                binding.allPostsRecyclerView.visibility = View.VISIBLE
                adapter = PostsAdapter(it)
                binding.allPostsRecyclerView.adapter = adapter
            }

        })
        viewModel.isLoading.observe(viewLifecycleOwner, {
            it.let {
                if (it) {
                    binding.progressBarHome.visibility = View.VISIBLE
                } else {
                    binding.progressBarHome.visibility = View.GONE
                }
            }
        })
        viewModel.isSuccess.observe(viewLifecycleOwner, {
            it.let {
                if (it) {
                    binding.errorTextHome.visibility = View.INVISIBLE
                } else {
                    binding.errorTextHome.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}