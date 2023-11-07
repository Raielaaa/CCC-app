package com.example.ccc_library_app.ui.dashboard.home

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.ccc_library_app.R
import com.example.ccc_library_app.ui.dashboard.util.Resources

class HomeFragmentViewModel : ViewModel() {
    private var TAG: String = "MyTag"

    fun captureQR(activity: Activity) {
        com.example.ccc_library_app.ui.account.util.Resources.dismissDialog()
        //  Prompting the user for camera permission. Camera permission is used so that we can use
        //  the camera of the phone of the user
        cameraPermissions(activity)
    }

    private fun takeImage(activity: Activity) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            activity.startActivityForResult(intent, 1)
        } catch (e: Exception) {
            Toast.makeText(activity, e.localizedMessage, Toast.LENGTH_SHORT).show()
            Log.d(TAG, "takeImage: ${e.message}")
        }
    }

    private fun cameraPermissions(activity: Activity) {
        val cameraPermission = android.Manifest.permission.CAMERA
        val permissionGranted = PackageManager.PERMISSION_GRANTED

        if (ContextCompat.checkSelfPermission(activity, cameraPermission) != permissionGranted) {
            // Request camera permission if it's not granted.
            ActivityCompat.requestPermissions(activity, arrayOf(cameraPermission), 111)
        } else {
            // Camera permission is already granted, proceed to capturing the image.
            takeImage(activity)
        }
    }

    fun navigateToBookList(hostFragment: Fragment, ivBookList: ImageView) {
        Resources.navigate(hostFragment, ivBookList, R.id.action_homeFragment_to_bookListFragment)
    }

    fun navigateToBookmark(hostFragment: Fragment, ivBookmark: ImageView) {
        Resources.navigate(hostFragment, ivBookmark, R.id.action_homeFragment_to_bookmarkFragment)
    }

    fun navigateToSettings(hostFragment: Fragment, ivSettings: ImageView) {
        Resources.navigate(hostFragment, ivSettings, R.id.action_homeFragment_to_settingsFragment)
    }
}