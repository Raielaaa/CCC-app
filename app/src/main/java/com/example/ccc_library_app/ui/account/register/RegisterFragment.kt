package com.example.ccc_library_app.ui.account.register

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ccc_library_app.R
import com.example.ccc_library_app.databinding.FragmentRegisterBinding
import com.google.android.material.textfield.TextInputLayout


class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container,false)

        initClickedViewsTheme()

        return binding.root
    }

    private fun initClickedViewsTheme() {
        binding.apply {

        }
    }
}