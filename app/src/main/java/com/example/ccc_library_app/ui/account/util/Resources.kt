package com.example.ccc_library_app.ui.account.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ccc_library_app.R
import com.example.ccc_library_app.ui.account.main.MainActivity
import com.example.ccc_library_app.ui.dashboard.borrow_return.BorrowReturnDialogFragment
import com.example.ccc_library_app.ui.dashboard.home.main.HomeFragment
import com.example.ccc_library_app.ui.dashboard.home.db.FirebaseDBManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

object Resources {
    private var dialog: Dialog? = null
    private var TAG: String = "MyTag"
    private lateinit var data: List<String>
    lateinit var navDrawer: NavigationView

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

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE, Barcode.FORMAT_AZTEC)
        .enableAllPotentialBarcodes()
        .build()

    fun scanBitmapQR(imageBitmap: Bitmap, activity: MainActivity, fireStore: FirebaseFirestore, auth: FirebaseAuth) {
        //  Create an InputImage object from the bitmap
        val image = InputImage.fromBitmap(imageBitmap, 0)
        //  Create a BarcodeScanner client with options
        val scanner = BarcodeScanning.getClient(options)

        //  Process the image for barcodes
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                //  If no barcode is found, show a toast message and exit function
                if (barcodes.toString() != "[]") {
                    //  Loop through each barcode found in the image
                    for (barcode in barcodes) {
                        if (barcode.rawValue.toString().isEmpty()) {
                            dismissDialog()
                            displayCustomDialog(
                                activity,
                                R.layout.custom_dialog_notice,
                                "QR-scan notice",
                                "Nothing to scan. Please try again."
                            )
                        } else {
                            //  If the barcode is of type text, extract the book name and author name
                            when (barcode.valueType) {
                                Barcode.TYPE_TEXT -> {
                                    dismissDialog()

                                    BorrowReturnDialogFragment(
                                        barcode.rawValue!!,
                                        fireStore,
                                        auth,
                                        FirebaseStorage.getInstance(),
                                        imageBitmap
                                    ).show(activity.supportFragmentManager, "BorrowReturn_Dialog")
//                                    FirebaseDBManager().insertDataToDB(
//                                        barcode.rawValue,
//                                        activity,
//                                        fireStore,
//                                        auth
//                                    )
                                }
                            }
                        }
                    }
                } else {
                    //  If no barcode is found
                    dismissDialog()
                    displayCustomDialog(
                        activity,
                        R.layout.custom_dialog_notice,
                        "QR-scan notice",
                        "Nothing to scan. Please try again."
                    )
                }
            }
    }

    @SuppressLint("ObsoleteSdkInt")
    fun displayCustomDialog(
        activity: Activity,
        layoutDialog: Int,
        title: String,
        content: String,
        minWidthPercentage: Float = 0.75f
    ) {
        try {
            if (!activity.isFinishing) {
                dialog = Dialog(activity)

                dialog?.apply {
                    setContentView(layoutDialog)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        window!!.setBackgroundDrawable(ResourcesCompat.getDrawable(
                            activity.resources,
                            R.drawable.custom_dialog_background,
                            null))
                    }
                    window!!.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    setCancelable(false)
                    window!!.attributes.windowAnimations = R.style.animation

                    // Calculate the minWidth in pixels based on the percentage of the screen width
                    val screenWidth = getScreenWidth(activity)
                    val minWidth = (screenWidth * minWidthPercentage).toInt()

                    dialog?.apply {
                        findViewById<ConstraintLayout>(R.id.clMain)?.minWidth = minWidth
                        findViewById<ConstraintLayout>(R.id.clMain)?.setOnClickListener {
                            dialog?.dismiss()
                        }
                        findViewById<TextView>(R.id.tvDialogOk)?.setOnClickListener {
                            dialog?.dismiss()
                        }
                        findViewById<TextView>(R.id.tvDialogTitle)?.text = title
                        findViewById<TextView>(R.id.tvDialogContent)?.text = content
                    }
                    show()
                }
            }
        } catch (err: Exception) {
            Log.e(TAG, "displayCustomDialog: ${err.message}")
            Toast.makeText(
                activity,
                "Error: ${err.localizedMessage}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getScreenWidth(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()
        val windowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
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