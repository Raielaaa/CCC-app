package com.example.ccc_library_app.ui.dashboard.borrow_return.date_time

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.ccc_library_app.R
import com.example.ccc_library_app.databinding.FragmentSetDateBinding

class SetDateFragment(
    private val dateTimeSelectedListener: DateTimeSelectedListener
) : DialogFragment() {
    private lateinit var binding: FragmentSetDateBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSetDateBinding.inflate(inflater, container, false)

        return binding.root
    }
}