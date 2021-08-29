package com.example.blogapp.views.mainapp

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.blogapp.R
import com.example.blogapp.adapter.PostsAdapter
import com.example.blogapp.databinding.FragmentHomeBinding
import com.example.blogapp.model.Post
import com.example.blogapp.viewmodel.HomeViewModel
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private var arrayList = ArrayList<Post>()
    private var displayList = ArrayList<Post>()
    private var mainList = ArrayList<Post>()
    private lateinit var adapter: PostsAdapter
    private  val TAG = "HomeFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

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
                arrayList.addAll(it)
                setAdapter(it)
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

    private fun setAdapter(model: ArrayList<Post>) {
        adapter = PostsAdapter(model)
        binding.allPostsRecyclerView.adapter = adapter
        binding.allPostsRecyclerView.adapter!!.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        val menuItem = menu.findItem(R.id.search_src_text)
        if (menuItem != null) {
            val searchView = menuItem.actionView as SearchView
            val editText =
                searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
            editText.hint = "Search..."

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    if (newText.isNotEmpty()) {
                        displayList.clear()
                        val search = newText.lowercase(Locale.getDefault())
                        arrayList.forEach {
                            if (it.desc.lowercase(Locale.getDefault()).contains(search)) {
                                displayList.add(it)
                                Log.i(TAG, "onQueryTextChange:  $it")
                            }
                        }

                    } else {
                        displayList.clear()
                        displayList.addAll(arrayList)
                    }
                    return true
                }
            })
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
