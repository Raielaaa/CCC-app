package com.example.ccc_library_app.ui.dashboard.list

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ccc_library_app.databinding.FragmentBookListBinding

class BookListFragment : Fragment() {
    private lateinit var bookListViewModel: BookListViewModel
    private lateinit var binding: FragmentBookListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookListBinding.inflate(inflater, container, false)
        bookListViewModel = ViewModelProvider(this@BookListFragment)[BookListViewModel::class.java]

        initBottomNavigationBar()

        return binding.root
    }

    private fun initBottomNavigationBar() {
        binding.apply {
            bookListViewModel.apply {
                navigateHome(this@BookListFragment, ivHome)
                navigateBookmark(this@BookListFragment, ivBookmark)
                navigateSettings(this@BookListFragment, ivSettings)
            }
        }
    }
}