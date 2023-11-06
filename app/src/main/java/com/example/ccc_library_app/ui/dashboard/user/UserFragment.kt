package com.example.ccc_library_app.ui.dashboard.user

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ccc_library_app.R
import com.example.ccc_library_app.databinding.FragmentUserBinding

class UserFragment : Fragment() {
    private lateinit var userViewModel: UserViewModel
    private lateinit var binding: FragmentUserBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        userViewModel = ViewModelProvider(this@UserFragment)[UserViewModel::class.java]

        initBottomNavigationBar()

        return binding.root
    }

    private fun initBottomNavigationBar() {
        binding.apply {
            userViewModel.apply {
                navigateHome(this@UserFragment, ivHome)
                navigateBookmark(this@UserFragment, ivBookmark)
                navigateSettings(this@UserFragment, ivSettings)
            }
        }
    }
}