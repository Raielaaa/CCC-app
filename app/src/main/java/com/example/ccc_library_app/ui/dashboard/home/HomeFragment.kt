package com.example.ccc_library_app.ui.dashboard.home

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ccc_library_app.R
import com.example.ccc_library_app.databinding.FragmentHomeBinding
import com.example.ccc_library_app.ui.dashboard.home.popular.PopularAdapter
import com.example.ccc_library_app.ui.dashboard.home.popular.PopularModel

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: PopularAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        initRecyclerView()

        return binding.root
    }

    private fun initRecyclerView() {
        adapter = PopularAdapter()

        binding.apply {
            adapter.setList(
                listOf(
                    PopularModel(Uri.parse("android.resource://com.example.ccc_library_app/drawable/main_popular_pic_1"), "Data Science"),
                    PopularModel(Uri.parse("android.resource://com.example.ccc_library_app/drawable/main_popular_pic_1"), "Max. Impact"),
                    PopularModel(Uri.parse("android.resource://com.example.ccc_library_app/drawable/main_popular_pic_1"), "Techno-crimes"),
                    PopularModel(Uri.parse("android.resource://com.example.ccc_library_app/drawable/main_popular_pic_1"), "Data Science 2"),
                )
            )

            rvPopular.adapter = adapter
        }
    }
}