package com.example.ccc_library_app.ui.dashboard.home

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.ccc_library_app.R
import com.example.ccc_library_app.ui.account.util.Resources

class HomeFragmentViewModel : ViewModel() {
    private var TAG: String = "MyTag"

    fun captureQR(activity: Activity) {
        Resources.dismissDialog()
        //  Prompting the user for camera permission. Camera permission is used so that we can use
        //  the camera of the phone of the user
        cameraPermissions(activity)

        //  After getting the permission (assuming that the user clicked the "Allow" button on the prompt). A function
        //  will automatically be implemented, this function is used for launching the phone camera of the user to
        //  capture an image.
        takeImage(activity)
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
        val permissions = arrayOf(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.SYSTEM_ALERT_WINDOW,
            android.Manifest.permission.CAMERA
        )
        //  When you request a permission from the user using "requestPermissions()" method, you can specify a request code as the second argument.
        //  The purpose of the request code is to identify the permission request when the result of the request is returned
        //  to the activity or fragment that initiated the request.
        //  Note that the permission code can be any number, but it's a good practice to generate a unique request code for each permission request.
        val cameraPermissionCode = 111
        activity.requestPermissions(permissions, cameraPermissionCode)
    }

    fun navigateToUser(hostFragment: Fragment, ivUser: ImageView) {
        ivUser.setOnClickListener {
            hostFragment.findNavController().navigate(R.id.action_homeFragment_to_userFragment)
        }
    }

    fun navigateToBookmark(hostFragment: Fragment, ivBookmark: ImageView) {
        ivBookmark.setOnClickListener {
            hostFragment.findNavController().navigate(R.id.action_homeFragment_to_bookmarkFragment)
        }
    }

    fun navigateToSettings(hostFragment: Fragment, ivSettings: ImageView) {
        ivSettings.setOnClickListener {
            hostFragment.findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }
    }
}