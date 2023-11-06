package com.example.ccc_library_app.ui.dashboard.bookmark

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.ccc_library_app.R
import com.example.ccc_library_app.databinding.FragmentBookmarkBinding

class BookmarkFragment : Fragment() {
    private lateinit var binding: FragmentBookmarkBinding
    private lateinit var bookMarkViewModel: BookmarkViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        bookMarkViewModel = ViewModelProvider(this@BookmarkFragment)[BookmarkViewModel::class.java]

        initBottomNavigationBar()

        return binding.root
    }

    private fun initBottomNavigationBar() {
        binding.apply {
            bookMarkViewModel.navigateHome(this@BookmarkFragment, ivHome)
            bookMarkViewModel.navigateUser(this@BookmarkFragment, ivUser)
            bookMarkViewModel.navigateSettings(this@BookmarkFragment, ivSettings)
        }
    }
}