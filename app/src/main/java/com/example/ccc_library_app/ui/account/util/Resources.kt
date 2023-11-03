package com.example.ccc_library_app.ui.account.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ccc_library_app.R

object Resources {
    private var dialog: Dialog? = null
    private var TAG: String = "MyTag"
    private lateinit var data: List<String>

    fun setGoogleSignInData(dataFromUser: List<String>) {
        data = dataFromUser
    }

    fun getGoogleSignInData(): List<String> = data

    @SuppressLint("ObsoleteSdkInt")
    fun displayCustomDialog(
        activity: Activity,
        hostFragment: Fragment,
        layoutDialog: Int
    ) {
        try {
            if (!activity.isFinishing) {
                dialog = Dialog(activity)

                dialog?.apply {
                    setContentView(layoutDialog)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        window!!.setBackgroundDrawable(ResourcesCompat.getDrawable(activity.resources, R.drawable.custom_dialog_background, null))
                    window!!.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    setCancelable(false)
                    window!!.attributes.windowAnimations = R.style.animation
                    show()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "displayCustomDialog: ${e.printStackTrace()}", )
            // Handle the exception, if necessary
        }

        try {
            dialog?.findViewById<Button>(R.id.btnOk)?.setOnClickListener {
                dialog?.dismiss()
                hostFragment.findNavController().navigate(R.id.homeAccountFragment)
            }
            dialog?.findViewById<Button>(R.id.btnCancel)?.setOnClickListener {
                dialog?.dismiss()
                hostFragment.findNavController().navigate(R.id.homeFragment)
            }
        } catch (ignored: Exception) { }
    }

    @SuppressLint("ObsoleteSdkInt")
    fun displayCustomDialog(
        activity: Activity,
        layoutDialog: Int,
        imageBitmap: Bitmap
    ) {
        try {
            if (!activity.isFinishing) {
                dialog = Dialog(activity)

                dialog?.apply {
                    setContentView(layoutDialog)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        window!!.setBackgroundDrawable(ResourcesCompat.getDrawable(activity.resources, R.drawable.custom_dialog_background, null))
                    window!!.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    setCancelable(false)
                    window!!.attributes.windowAnimations = R.style.animation
                    show()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "displayCustomDialog: ${e.printStackTrace()}", )
            // Handle the exception, if necessary
        }

        try {
            dialog?.apply {
                findViewById<Button>(R.id.btnCancel)?.setOnClickListener {
                    dismiss()
                }
                findViewById<ImageView>(R.id.ivQR)?.setImageBitmap(imageBitmap)
            }
        } catch (e: Exception) {
//            Toast.makeText(hostFragment.requireContext(), "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "displayCustomDialog: ${e.message}")
        }
    }

    fun displayCustomDialog(
        activity: Activity,
        layoutDialog: Int
    ) {
        dialog = Dialog(activity)

        dialog?.apply {
            setContentView(layoutDialog)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                window!!.setBackgroundDrawable(ResourcesCompat.getDrawable(activity.resources, R.drawable.custom_dialog_background, null))
            window!!.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setCancelable(false)
            window!!.attributes.windowAnimations = R.style.animation
            show()
        }
        try {
            dialog?.findViewById<Button>(R.id.btnOk)?.setOnClickListener {
                dialog?.dismiss()
            }
        } catch (ignored: Exception) { }
    }

    fun dismissDialog() {
        dialog?.dismiss()
    }
}