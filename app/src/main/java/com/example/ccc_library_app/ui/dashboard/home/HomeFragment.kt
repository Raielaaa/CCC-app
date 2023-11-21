package com.example.ccc_library_app.ui.dashboard.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.widget.ImageView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.example.ccc_library_app.R
import com.example.ccc_library_app.databinding.FragmentHomeBinding
import com.example.ccc_library_app.ui.account.main.MainActivity
import com.example.ccc_library_app.ui.dashboard.util.Resources
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.util.Timer
import java.util.TimerTask
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class HomeFragment : Fragment(), CoroutineScope {
    //  Views
    private lateinit var binding: FragmentHomeBinding

    //  ViewModel
    private lateinit var homeFragmentViewModel: HomeFragmentViewModel

    //  Slideshow
    private val imageSlideshow = ImageSlideshow()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + Job()

    //  General
    private val TAG: String = "MyTag"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        homeFragmentViewModel = ViewModelProvider(this)[HomeFragmentViewModel::class.java]

        initRecyclerView()
        initClickableViews()
        imageSlideshow.startSlideshow(binding.ivSlideshow)
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

    private fun initRecyclerViewPopular() {

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
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext[Job]?.cancel()
        imageSlideshow.stopSlideshow()
    }

    private fun initRecyclerView() {
        homeFragmentViewModel.initPopularRecyclerView(
            binding.rvPopular,
            requireActivity(),
            this@HomeFragment
        )
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