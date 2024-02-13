package com.example.ccc_library_app.ui.dashboard.home.main

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.UnderlineSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.text.color
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ccc_library_app.R
import com.example.ccc_library_app.databinding.FragmentHomeBinding
import com.example.ccc_library_app.ui.dashboard.home.popular.FirebaseDataModel
import com.example.ccc_library_app.ui.dashboard.home.popular.PopularAdapter
import com.example.ccc_library_app.ui.dashboard.util.DataCache
import com.example.ccc_library_app.ui.dashboard.util.Resources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class HomeFragment : Fragment(), CoroutineScope {
    @Inject
    @Named("FirebaseAuth.Instance")
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    @Named("FirebaseFireStore.Instance")
    lateinit var firebaseFireStore: FirebaseFirestore

    @Inject
    @Named("FirebaseStorage.Instance")
    lateinit var firebaseStorage: StorageReference

    //  Views
    private lateinit var binding: FragmentHomeBinding

    //  ViewModel
    private lateinit var homeFragmentViewModel: HomeFragmentViewModel

    //  Slideshow
    private val imageSlideshow = ImageSlideshow()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + Job()

    //  General
    private val TAG: String = "MyTag"

    //  Image chooser
    private lateinit var pickMediaLauncher: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        homeFragmentViewModel = ViewModelProvider(this)[HomeFragmentViewModel::class.java]

        initializeViews()
        initNavDrawer()
        initOnBackPress()
        initStatusBar()

        return binding.root
    }

    private fun initStatusBar() {
        Resources.changeStatusBarColorToWhite(this@HomeFragment)
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
                    this@HomeFragment,
                    binding.ivUserImage,
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

    private fun initOnBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Toast.makeText(
                    requireContext(),
                    "Back press on Homepage unavailable",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun initNavDrawer() {
        com.example.ccc_library_app.ui.account.util.Resources.navDrawer.setCheckedItem(R.id.drawer_home)
    }

    private fun initializeViews() {
        initAnnouncement()
        checkIfPastDuePresent()
        refreshApp()
        initRecyclerView()
        initClickableViews()
        imageSlideshow.startSlideshow(binding.ivSlideshow)
        initBottomNavigationBar()
        initNavigationDrawer()
        initFeaturedBookDisplay()
        initSeeMoreDesign()
        initBookTally()
        initSeeAllBottomDialog()
        initProfileImage()
        disableBackPress()
        initExpandButtons()
    }

    private fun initExpandButtons() {
        binding.apply {
            ivBorrowedStatusExpand.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_bookmarkFragment)
            }
            ivInventoryExpand.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_bookListFragment)
            }
            ivInventoryExpands.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_bookListFragment)
            }
        }
    }

    private fun initAnnouncement() {
        firebaseFireStore.collection("ccc-library-app-announcement")
            .document("lircannouncement")
            .get()
            .addOnSuccessListener { documentSnapshot ->
                binding.tvAnnouncements.text = documentSnapshot.get("modelAnnouncement").toString()
            }.addOnFailureListener { exception ->
                Log.e(TAG, "initAnnouncement: ${exception.message}")
                Toast.makeText(
                    requireContext(),
                    "Failed to load announcement: ${exception.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun checkIfPastDuePresent() {
        binding.apply {
            Resources.checkPastDue(firebaseAuth, firebaseFireStore, cvPastDueNotice, this@HomeFragment)

            ivRemoveNotice.setOnClickListener {
                // Load the fade-out animation
                val fadeOutAnimation = AnimatorInflater.loadAnimator(this@HomeFragment.requireContext(), R.animator.fade_out)

                // Create an AnimatorSet
                val animatorSet = AnimatorSet()

                // Set the target view for the animation
                fadeOutAnimation.setTarget(cvPastDueNotice)

                // Add the fade-out animation to the AnimatorSet
                animatorSet.play(fadeOutAnimation)

                // Add an AnimatorListenerAdapter to handle visibility change after the fade
                animatorSet.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        // Set visibility to INVISIBLE after the fade-out animation
                        cvPastDueNotice.visibility = View.GONE
                    }
                })

                // Start the AnimatorSet
                animatorSet.start()
            }
        }
    }

    private fun disableBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Toast.makeText(
                    requireContext(),
                    "Back press on Homepage unavailable",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun refreshApp() {
        binding.apply {
            swipeDownRefresh.setOnRefreshListener {
                homeFragmentViewModel.refreshPersistentData(
                    this@HomeFragment,
                    swipeDownRefresh
                )
            }
        }
    }

    private fun initRecyclerView() {
        homeFragmentViewModel.initPopularRecyclerView(
            binding.rvPopular,
            requireActivity(),
            this@HomeFragment
        )
    }

    private fun initProfileImage() {
        binding.apply {
            if (DataCache.userImageProfile != null) {
                ivUserImage.setImageBitmap(DataCache.userImageProfile)
            } else {
                firebaseStorage.child("user_image/${firebaseAuth.currentUser?.uid}")
                    .getBytes(Long.MAX_VALUE)
                    .addOnSuccessListener { bytes ->
                        // Convert the byte array to a Bitmap and set it in the ImageView
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                        DataCache.userImageProfile = bitmap
                        binding.ivUserImage.setImageBitmap(bitmap)
                    }.addOnFailureListener { exception ->
                        // Handle failures
                        exception.printStackTrace()
                    }
            }
        }
    }


    private fun initSeeAllBottomDialog() {
        binding.apply {
            homeFragmentViewModel.initSeeAllBottomDialog(
                tvCurrentSeeAll,
                ivCurrentSeeAll,
                tvBorrowSeeAll,
                ivBorrowSeeAll,
                this@HomeFragment,
                requireActivity()
            )
        }
    }

    private fun initBookTally() {
        if (DataCache.booksFullInfo.isEmpty()) {
            binding.apply {
                homeFragmentViewModel.displayTally(
                    tvInventoryCurrent,
                    tvInventoryBorrowed,
                    requireContext()
                )
            }
        } else {
            binding.apply {
                var inventoryCurrent = 0
                var inventoryBorrowed = 0

                for (data in DataCache.booksFullInfo) {
                    if (data.modelBookStatus == "Available") inventoryCurrent++
                    else inventoryBorrowed++
                }

                tvInventoryBorrowed.text = inventoryBorrowed.toString()
                tvInventoryCurrent.text = inventoryCurrent.toString()
            }
        }
    }

    private fun initSeeMoreDesign() {
        binding.apply {
            val spannableString = SpannableString(tvSeeMore.text.toString())
            spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, 0)
            tvSeeMore.text = spannableString

            val clickMeSpannableString = SpannableStringBuilder()
                .append("Click ")
                .color(ContextCompat.getColor(requireContext(), R.color.Theme_color)) { append("ME")}
            tvClickMe.text = clickMeSpannableString
        }
    }

    private fun initFeaturedBookDisplay() {
        binding.apply {
            homeFragmentViewModel.initFeaturedBook(
                ivFeaturedImage,
                tvFeaturedTitle,
                tvFeaturedDescription,
                requireActivity()
            )
        }
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
            homeFragmentViewModel.navigateToBookList(this@HomeFragment, ivBookList)
            homeFragmentViewModel.navigateToBookmark(this@HomeFragment, ivBookmark)
            homeFragmentViewModel.navigateToSettings(this@HomeFragment, ivSettings)
        }
    }

    private fun initClickableViews() {
        binding.apply {
            cvCaptureQR.setOnClickListener {
                homeFragmentViewModel.captureQR(requireActivity())
            }
            ivTakeQr.setOnClickListener {
                homeFragmentViewModel.captureQR(requireActivity())
            }
            tvSeeMore.setOnClickListener {
                homeFragmentViewModel.initFeaturedClickedFunction(
                    this@HomeFragment,
                    tvFeaturedTitle.text.toString()
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext[Job]?.cancel()
        imageSlideshow.stopSlideshow()
    }
}

class ImageSlideshow {
    private val images = intArrayOf(R.drawable.slideshow_pic1, R.drawable.slideshow_pic2, R.drawable.slideshow_pic3, R.drawable.slideshow_pic4)
    private var currentImageIndex = 0
    private var timer: Timer? = null
    private lateinit var imageView: ImageView

    fun startSlideshow(imageView: ImageView) {
        this.imageView = imageView
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                changeImageWithAnimation(imageView)
            }
        }, 0, 5000) // Change image every 5 seconds
    }

    fun stopSlideshow() {
        timer?.cancel()
        timer?.purge()
    }

    private fun changeImageWithAnimation(imageView: ImageView) {
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.duration = 1000

        val animationSet = AnimationSet(true)
        animationSet.addAnimation(fadeIn)

        imageView.startAnimation(animationSet)

        currentImageIndex = (currentImageIndex + 1) % images.size
        // Preload the next image in the background
        val nextImageIndex = (currentImageIndex + 1) % images.size
        val nextImage = images[nextImageIndex]
        imageView.post {
            imageView.setImageResource(nextImage)
        }
    }
}