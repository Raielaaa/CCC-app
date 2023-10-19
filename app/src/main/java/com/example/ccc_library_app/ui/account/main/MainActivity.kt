package com.example.ccc_library_app.ui.account.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.ccc_library_app.R
import com.example.ccc_library_app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_account) as NavHostFragment
        navController = navHostFragment.navController
    }

    // Use this method to navigate to a destination
    fun navigateTo(destinationId: Int, args: Bundle? = null) {
        navController.navigate(destinationId, args)
    }
}