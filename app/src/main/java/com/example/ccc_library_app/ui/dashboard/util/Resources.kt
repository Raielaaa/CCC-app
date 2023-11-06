package com.example.ccc_library_app.ui.dashboard.util

import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ccc_library_app.R

object Resources {
    fun navigate(hostFragment: Fragment, clickedIV: ImageView, destination: Int) {
        clickedIV.setOnClickListener {
            hostFragment.findNavController().navigate(destination)
        }
    }
}