package com.example.blogapp.views.mainapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.blogapp.R
import com.example.blogapp.adapter.ProfilePostAdapter
import com.example.blogapp.databinding.FragmentUserProfileBinding
import com.example.blogapp.viewmodel.UserProfileViewModel

class UserProfileFragment : Fragment() {
    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ProfilePostAdapter
    private lateinit var viewModel: UserProfileViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(UserProfileViewModel::class.java)
        viewModel.getUserData()
        init()
    }

    private fun init() {
        viewModel.user.observe(viewLifecycleOwner, {
            binding.user = it
        })
        viewModel.textPost.observe(viewLifecycleOwner, {
            if (it) binding.networkStateText.visibility =
                View.GONE else binding.networkStateText.visibility = View.VISIBLE
        })
        viewModel.posts.observe(viewLifecycleOwner, {
            binding.postCount = it.size
            adapter = ProfilePostAdapter(it)
            binding.allPostsRecyclerView.adapter = adapter
        })
        binding.editProfileButton.setOnClickListener {
            val nav = UserProfileFragmentDirections.actionUserProfileFragmentToEditProfileFragment()
            Navigation.findNavController(it).navigate(nav)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.log_out_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logOutIcon) {
            val alert: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            alert.apply {
                setTitle("Log Out !")
                setMessage("Do You Want to Log Out?")
                setIcon(R.drawable.ic_baseline_login_24)
                setPositiveButton("Ok") { _, _ ->
                    viewModel.logOut(requireContext(), requireActivity())
                }
                setNegativeButton("Cancel") { _, _ -> }
            }
            alert.show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (!hidden) {
            init()
        }
        super.onHiddenChanged(hidden)
    }

    override fun onResume() {
        super.onResume()
        init()
    }
}