package com.example.ccc_library_app.ui.dashboard.bookmark

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ccc_library_app.R
import com.example.ccc_library_app.databinding.FragmentBookmarkBinding
import com.example.ccc_library_app.ui.dashboard.home.main.HomeFragmentViewModel
import com.example.ccc_library_app.ui.dashboard.util.Constants
import com.example.ccc_library_app.ui.dashboard.util.DataCache
import com.example.ccc_library_app.ui.dashboard.util.Resources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class BookmarkFragment : Fragment() {
    private lateinit var binding: FragmentBookmarkBinding
    private lateinit var bookMarkViewModel: BookmarkViewModel

    @Inject
    @Named("FirebaseAuth.Instance")
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    @Named("FirebaseFireStore.Instance")
    lateinit var firebaseFireStore: FirebaseFirestore

    //  Image chooser
    private lateinit var pickMediaLauncher: ActivityResultLauncher<PickVisualMediaRequest>

    private lateinit var homeFragmentViewModel: HomeFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        bookMarkViewModel = ViewModelProvider(this@BookmarkFragment)[BookmarkViewModel::class.java]
        homeFragmentViewModel = ViewModelProvider(this@BookmarkFragment)[HomeFragmentViewModel::class.java]

        initBottomNavigationBar()
        initNavigationDrawer()
        initStatusBar()
        checkIfPastDuePresent()
        initProfileImage()
        initRV()
        initFAB()
        refreshApp()

        return binding.root
    }

    private fun refreshApp() {
        binding.swipeDownRefreshBookmark.setOnRefreshListener {
            findNavController().navigate(R.id.bookmarkFragment)
        }
    }

    private fun initFAB() {
        binding.fabBookmarkCamera.setOnClickListener {
            homeFragmentViewModel.captureQR(requireActivity())
        }
    }

    private fun initRV() {
        bookMarkViewModel.initRV(
            binding,
            this@BookmarkFragment,
            binding.rvBookmarkMain,
            firebaseAuth,
            firebaseFireStore,
            FirebaseStorage.getInstance(),
            binding.ivNoData
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Move the registerForActivityResult call here
        pickMediaLauncher = registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            if (uri != null) {
                val storage = FirebaseStorage.getInstance().reference
                val auth = FirebaseAuth.getInstance()

                Resources.imageChooserDisplay(
                    this@BookmarkFragment,
                    binding.ivProfileImage,
                    storage,
                    auth,
                    uri
                )
            } else {
                Toast.makeText(
                    requireContext(),
                    "No media selected",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun initProfileImage() {
        binding.apply {
            if (DataCache.userImageProfile != null) {
                ivProfileImage.setImageBitmap(DataCache.userImageProfile)
            }

            ivProfileImage.setOnClickListener {
                // Call the registered launcher here
                pickMediaLauncher.launch(PickVisualMediaRequest())
            }
        }
    }

    private fun initStatusBar() {
        Resources.changeStatusBarColorToBlack(this@BookmarkFragment)
    }

    private fun initNavigationDrawer() {
        com.example.ccc_library_app.ui.account.util.Resources.navDrawer.setCheckedItem(R.id.drawer_bookmark)

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

    private fun checkIfPastDuePresent() {
        binding.apply {
            Resources.checkPastDue(firebaseAuth, firebaseFireStore, cvPastDueNoticee, this@BookmarkFragment)

            ivRemoveNoticee.setOnClickListener {
                // Load the fade-out animation
                val fadeOutAnimation = AnimatorInflater.loadAnimator(this@BookmarkFragment.requireContext(), R.animator.fade_out)

                // Create an AnimatorSet
                val animatorSet = AnimatorSet()

                // Set the target view for the animation
                fadeOutAnimation.setTarget(cvPastDueNoticee)

                // Add the fade-out animation to the AnimatorSet
                animatorSet.play(fadeOutAnimation)

                // Add an AnimatorListenerAdapter to handle visibility change after the fade
                animatorSet.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        // Set visibility to INVISIBLE after the fade-out animation
                        cvPastDueNoticee.visibility = View.GONE
                    }
                })

                // Start the AnimatorSet
                animatorSet.start()
            }
        }
    }

    private fun initBottomNavigationBar() {
        binding.apply {
            bookMarkViewModel.navigateHome(this@BookmarkFragment, ivHome)
            bookMarkViewModel.navigateBookList(this@BookmarkFragment, ivBookList)
            bookMarkViewModel.navigateSettings(this@BookmarkFragment, ivSettings)

            ivTakeQr.setOnClickListener {
                homeFragmentViewModel.captureQR(requireActivity())
            }
        }
    }
}