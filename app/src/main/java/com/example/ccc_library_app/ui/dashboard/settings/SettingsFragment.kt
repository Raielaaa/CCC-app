package com.example.ccc_library_app.ui.dashboard.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.example.ccc_library_app.R
import com.example.ccc_library_app.databinding.FragmentSettingsBinding
import com.example.ccc_library_app.ui.dashboard.util.Resources


class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        settingsViewModel = ViewModelProvider(this@SettingsFragment)[SettingsViewModel::class.java]

        initBottomNavigationBar()
        initNavigationDrawer()

        return binding.root
    }

    private fun initNavigationDrawer() {
        val drawerLayout: DrawerLayout? = Resources.getDrawerLayoutRef()

        binding.ivNavDrawer.setOnClickListener {
            // Toggle the drawer (open if closed, close if open)
            if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    private fun initBottomNavigationBar() {
        binding.apply {
            settingsViewModel.apply {
                navigateToSettings(this@SettingsFragment, ivHome)
                navigateToBookList(this@SettingsFragment, ivBookList)
                navigateToBookmark(this@SettingsFragment, ivBookmark)
            }
        }
    }
}