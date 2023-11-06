package com.example.ccc_library_app.ui.dashboard.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.example.ccc_library_app.R
import com.example.ccc_library_app.databinding.FragmentSettingsBinding


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

        return binding.root
    }

    private fun initBottomNavigationBar() {
        binding.apply {
            settingsViewModel.apply {
                navigateToSettings(this@SettingsFragment, ivHome)
                navigateToUser(this@SettingsFragment, ivUser)
                navigateToBookmark(this@SettingsFragment, ivBookmark)
            }
        }
    }
}