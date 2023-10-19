package com.example.ccc_library_app.ui.account.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.ccc_library_app.R
import com.example.ccc_library_app.databinding.FragmentHomeAccountBinding

class HomeAccountFragment : Fragment() {
    private lateinit var binding: FragmentHomeAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeAccountBinding.inflate(inflater, container, false)

        initButtonFunctions()

        return binding.root
    }

    private fun initButtonFunctions() {
        binding.apply {
            btnLogin.setOnClickListener {
                findNavController().navigate(R.id.loginFragment)
            }
        }
    }
}