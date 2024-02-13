package com.example.ccc_library_app.ui.dashboard.list.main

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.ccc_library_app.R
import com.example.ccc_library_app.databinding.FragmentBookListBinding
import com.example.ccc_library_app.ui.dashboard.home.main.HomeFragmentViewModel
import com.example.ccc_library_app.ui.dashboard.util.DataCache
import com.example.ccc_library_app.ui.dashboard.util.Resources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class BookListFragment : Fragment() {
    private lateinit var bookListViewModel: BookListViewModel
    private lateinit var binding: FragmentBookListBinding

    @Inject
    @Named("FirebaseAuth.Instance")
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    @Named("FirebaseFireStore.Instance")
    lateinit var firebaseFireStore: FirebaseFirestore

    //  ViewModel
    private lateinit var homeFragmentViewModel: HomeFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookListBinding.inflate(inflater, container, false)
        bookListViewModel = ViewModelProvider(this@BookListFragment)[BookListViewModel::class.java]
        homeFragmentViewModel = ViewModelProvider(this@BookListFragment)[HomeFragmentViewModel::class.java]

        initBottomNavigationBar()
        initRecyclerView()
        highlightSelectedGenre()
//        addBookImageToCloudTBD()
        initNavigationDrawer()
        initVisitButton()
        initStatusBar()
        checkIfPastDuePresent()
        initProfileImage()
        initBottomQR()

        return binding.root
    }

    private fun initBottomQR() {
        binding.ivTakeQr.setOnClickListener {
            homeFragmentViewModel.captureQR(requireActivity())
        }
    }

    private fun initProfileImage() {
        binding.apply {
            if (DataCache.userImageProfile != null) {
                ivUserImage.setImageBitmap(DataCache.userImageProfile)
            }
        }
    }

    private fun checkIfPastDuePresent() {
        binding.apply {
            Resources.checkPastDue(firebaseAuth, firebaseFireStore, cvPastDueNoticee, this@BookListFragment)

            ivRemoveNoticee.setOnClickListener {
                // Load the fade-out animation
                val fadeOutAnimation = AnimatorInflater.loadAnimator(this@BookListFragment.requireContext(), R.animator.fade_out)

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

    private fun initStatusBar() {
        Resources.changeStatusBarColorToBlack(this@BookListFragment)
    }

    private fun initVisitButton() {
        binding.apply {
            btnVisit.setOnClickListener {
                bookListViewModel.visitWebsite(requireActivity(), "https://www.getfreeebooks.com/")
            }
            cvVitalSource.setOnClickListener {
                bookListViewModel.visitWebsite(requireActivity(), "https://login.vitalsource.com/?redirect_uri=https%3A%2F%2Fccc-elibrary.vitalsource.com%2F%23%2F&brand=ccc-elibrary.vitalsource.com&context=bookshelf")
            }
            cvAccessScience.setOnClickListener {
                bookListViewModel.visitWebsite(requireActivity(), "https://www.accessscience.com/")
            }
            cvPEJ.setOnClickListener {
                bookListViewModel.visitWebsite(requireActivity(), "https://ejournals.ph/")
            }
        }
    }

    private fun initNavigationDrawer() {
        com.example.ccc_library_app.ui.account.util.Resources.navDrawer.setCheckedItem(R.id.drawer_book_list)

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

    private fun highlightSelectedGenre() {
        binding.apply {
            bookListViewModel.initSelectedGenre(
                ivAll,
                ivAcc,
                ivLit,
                ivSocial,
                ivScience,
                ivTech,
                this@BookListFragment,
                tvGenreBookList,
                requireActivity()
            )
        }
    }

    data class ImageDetails(
        val bitmap: Bitmap,
        val fileName: String
    )

    private fun addBookImageToCloudTBD() {
        val listOfBitmapImage = listOf(
            ImageDetails(convertPNGImageResourceToBitmap(R.drawable.book_1), "book_images/539874.jpg"),
            ImageDetails(convertPNGImageResourceToBitmap(R.drawable.book_2), "book_images/372518.jpg"),
            ImageDetails(convertPNGImageResourceToBitmap(R.drawable.book_3), "book_images/785463.jpg"),
            ImageDetails(convertPNGImageResourceToBitmap(R.drawable.book_4), "book_images/954726.jpg"),
            ImageDetails(convertPNGImageResourceToBitmap(R.drawable.book_5), "book_images/839247.jpg"),
            ImageDetails(convertPNGImageResourceToBitmap(R.drawable.book_6), "book_images/468912.jpg"),
            ImageDetails(convertPNGImageResourceToBitmap(R.drawable.book_7), "book_images/621357.jpg"),
            ImageDetails(convertPNGImageResourceToBitmap(R.drawable.book_8), "book_images/526741.jpg"),
            ImageDetails(convertPNGImageResourceToBitmap(R.drawable.book_9), "book_images/874592.jpg"),
            ImageDetails(convertPNGImageResourceToBitmap(R.drawable.book_10), "book_images/183490.jpg")
        )

        for (images in listOfBitmapImage) {
            bookListViewModel.addImagesToCloudTBD(images.bitmap, images.fileName)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("MyTag", "addBookImageToCloudTBD: Upload successful")
                    } else {
                        Log.d("MyTag", "addBookImageToCloudTBD: Upload failed")
                    }
                }
        }
    }

    private fun convertPNGImageResourceToBitmap(imageResource: Int) : Bitmap = BitmapFactory.decodeResource(requireContext().resources, imageResource)

    private fun initRecyclerView() {
        binding.apply {
            bookListViewModel.setUpRecyclerView(
                requireActivity(),
                rvMain,
                this@BookListFragment,
                etBookListSearch,
                tvGenreBookList
            )
        }
    }

    private fun initBottomNavigationBar() {
        binding.apply {
            bookListViewModel.apply {
                navigateHome(this@BookListFragment, ivHome)
                navigateBookmark(this@BookListFragment, ivBookmark)
                navigateSettings(this@BookListFragment, ivSettings)
            }
        }
    }
}