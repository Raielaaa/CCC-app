package com.example.ccc_library_app.ui.dashboard.settings

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named


@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var settingsViewModel: SettingsViewModel

    @Inject
    @Named("FirebaseAuth.Instance")
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    @Named("FirebaseFireStore.Instance")
    lateinit var firebaseFireStore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        settingsViewModel = ViewModelProvider(this@SettingsFragment)[SettingsViewModel::class.java]

        initBottomNavigationBar()
        initNavigationDrawer()
        initStatusBar()
        checkIfPastDuePresent()

        return binding.root
    }

    private fun checkIfPastDuePresent() {
        binding.apply {
            Resources.checkPastDue(firebaseAuth, firebaseFireStore, cvPastDueNoticeeee, this@SettingsFragment)

            cvPastDueNoticeee.setOnClickListener {
                // Load the fade-out animation
                val fadeOutAnimation = AnimatorInflater.loadAnimator(this@SettingsFragment.requireContext(), R.animator.fade_out)

                // Create an AnimatorSet
                val animatorSet = AnimatorSet()

                // Set the target view for the animation
                fadeOutAnimation.setTarget(cvPastDueNoticeee)

                // Add the fade-out animation to the AnimatorSet
                animatorSet.play(fadeOutAnimation)

                // Add an AnimatorListenerAdapter to handle visibility change after the fade
                animatorSet.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        // Set visibility to INVISIBLE after the fade-out animation
                        cvPastDueNoticeeee.visibility = View.GONE
                    }
                })

                // Start the AnimatorSet
                animatorSet.start()
            }
        }
    }

    private fun initStatusBar() {
        Resources.changeStatusBarColorToBlack(this@SettingsFragment)
    }

    private fun initNavigationDrawer() {
        com.example.ccc_library_app.ui.account.util.Resources.navDrawer.setCheckedItem(R.id.drawer_settings)

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