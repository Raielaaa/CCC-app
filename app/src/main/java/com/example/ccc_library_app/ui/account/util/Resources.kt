package com.example.ccc_library_app.ui.account.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.Build
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ccc_library_app.R

object Resources {
    private var dialog: Dialog? = null

    @SuppressLint("ObsoleteSdkInt")
    fun displayCustomDialog(
        activity: Activity,
        hostFragment: Fragment,
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
                hostFragment.findNavController().navigate(R.id.homeAccountFragment)
            }
        } catch (ignored: Exception) { }
    }

    fun dismissDialog() {
        dialog?.dismiss()
    }
}