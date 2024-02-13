package com.example.ccc_library_app.ui.dashboard.util

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ccc_library_app.R
import com.example.ccc_library_app.ui.dashboard.home.main.BorrowStatusModel
import com.example.ccc_library_app.ui.dashboard.list.BookListItemModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Resources {
    fun changeStatusBarColorToBlack(hostFragment: Fragment) {
        // Check if the Android version is Lollipop or higher
        // Get the activity's window
        val window: Window = hostFragment.requireActivity().window

        // Set the status bar text color to black
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    fun changeStatusBarColorToWhite(hostFragment: Fragment) {
        // Check if the Android version is Lollipop or higher
        // Get the activity's window
        val window: Window = hostFragment.requireActivity().window

        // Set the status bar text color to black
        window.decorView.systemUiVisibility = 0
    }

    fun imageChooserDisplay(
        hostFragment: Fragment,
        ivUserImage: ImageView,
        storage: StorageReference,
        auth: FirebaseAuth,
        uri: Uri
    ) {
        com.example.ccc_library_app.ui.account.util.Resources.displayCustomDialog(
            hostFragment.requireActivity(),
            R.layout.custom_dialog_loading
        )

        val imagePath = "user_image/${auth.currentUser?.uid}"

        //  Resizing the image before uploading
        val resizedBitmap = com.example.ccc_library_app.ui.dashboard.util.Resources.resizeImage(
            uri,
            hostFragment.requireActivity().contentResolver
        )

        //  Convert the resized bitmap toa byte array
        val baos = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        //  Insert image to firebase storage
        storage.child(imagePath)
            .putBytes(data)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                return@continueWithTask storage.child(imagePath).downloadUrl
            }
            .addOnCompleteListener { taskChild ->
                if (taskChild.isSuccessful) {
                    storage.child(imagePath)
                        .getBytes(Long.MAX_VALUE)
                        .addOnSuccessListener { bytes ->
                            //  Convert the byte array to a Bitmap and set it in the ImageView
                            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                            DataCache.userImageProfile = bitmap
                            ivUserImage.setImageBitmap(bitmap)
                            com.example.ccc_library_app.ui.account.util.Resources.dismissDialog()
                        }.addOnFailureListener { exception ->
                            //  Handle failures
                            Log.e("MyTag", "imageChooserDisplay: ${exception.message}", )
                            Toast.makeText(
                                hostFragment.requireContext(),
                                "An error occurred: ${exception.localizedMessage}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }
    }

    private fun resizeImage(uri: Uri, contentResolver: ContentResolver): Bitmap {
        val originalBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))

        // Get the absolute path from the Uri
        val path = getRealPathFromURI(uri, contentResolver)

        // Get the image orientation
        val exif = ExifInterface(path)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)

        // Rotate the bitmap based on the orientation
        val rotatedBitmap = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(originalBitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(originalBitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(originalBitmap, 270f)
            else -> originalBitmap
        }

        // Calculate the new dimensions to keep the aspect ratio
        val maxWidth = 300f
        val maxHeight = 300f
        val scale = Math.min(maxWidth / rotatedBitmap.width, maxHeight / rotatedBitmap.height)

        val newWidth = (rotatedBitmap.width * scale).toInt()
        val newHeight = (rotatedBitmap.height * scale).toInt()

        return Bitmap.createScaledBitmap(rotatedBitmap, newWidth, newHeight, true)
    }

    private fun getRealPathFromURI(uri: Uri, contentResolver: ContentResolver): String {
        var path = ""
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        if (cursor != null) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            if (cursor.moveToFirst()) {
                path = cursor.getString(columnIndex)
            }
            cursor.close()
        }
        return path
    }


    private fun rotateImage(bitmap: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun navigate(hostFragment: Fragment, clickedIV: ImageView, destination: Int) {
        clickedIV.setOnClickListener {
            hostFragment.findNavController().navigate(destination)
        }
    }

    private var permanentDataForSearch: ArrayList<BookListItemModel> = ArrayList()

    fun setPermanentDataForSearch(data: ArrayList<BookListItemModel>) {
        this.permanentDataForSearch = data
    }

    fun getPermanentDataForSearch(): ArrayList<BookListItemModel> {
        return ArrayList(permanentDataForSearch)
    }

    private var drawerLayout: DrawerLayout? = null

    fun setDrawerLayoutRef(drawerLayout: DrawerLayout?) {
        this.drawerLayout = drawerLayout
    }

    fun getDrawerLayoutRef() : DrawerLayout? = this.drawerLayout

    private var userFilter: String = ""
    fun checkPastDue(
        firebaseAuth: FirebaseAuth,
        firebaseFireStore: FirebaseFirestore,
        cvPastDueNotice: CardView,
        hostFragment: Fragment
    ) {
        if (userFilter.isEmpty()) {
            val userID = firebaseAuth.currentUser?.uid

            firebaseFireStore.collection("ccc-library-app-user-data")
                .document(userID.toString())
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val userFirstName = documentSnapshot.get("modelFirstName").toString()
                    val userLastName = documentSnapshot.get("modelLastName").toString()
                    val userSection = documentSnapshot.get("modelSection").toString()

                    val filter = "${userFirstName.replace(" ", "")}${userLastName.replace(" ", "")}-$userSection"
                    userFilter = filter

                    firebaseFireStore.collection("ccc-library-app-borrow-data")
                        .whereGreaterThanOrEqualTo(FieldPath.documentId(), userFilter)
                        .whereLessThan(FieldPath.documentId(), userFilter + '\uF7FF')
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (querySnapshot.documents.isNotEmpty()) {
                                val itemDeadlines: ArrayList<String> = ArrayList()

                                for (document in querySnapshot.documents) {
                                    itemDeadlines.add(document.get("modelBorrowDeadline").toString())

                                    DataCache.totalOnBorrow.add(
                                        BorrowStatusModel(
                                            document.get("modelBookTitle").toString(),
                                            document.get("modelBookGenre").toString(),
                                            document.get("modelBookCode").toString()
                                        )
                                    )
                                }

                                for (itemDeadline in itemDeadlines) {
                                    if (getBorrowTimeDifference(itemDeadline).toLong() <= 0) {
                                        cvPastDueNotice.visibility = View.VISIBLE
                                        cvPastDueNotice.startAnimation(AnimationUtils.loadAnimation(hostFragment.requireActivity(), R.anim.rotate))

                                        CoroutineScope(Dispatchers.Main).launch {
                                            delay(2000)
                                            cvPastDueNotice.clearAnimation()
                                        }
                                    }
                                }
                            }
                        }
                }
        } else {
            firebaseFireStore.collection("ccc-library-app-borrow-data")
                .whereGreaterThanOrEqualTo(FieldPath.documentId(), userFilter)
                .whereLessThan(FieldPath.documentId(), userFilter + '\uF7FF')
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.documents.isNotEmpty()) {
                        val itemDeadlines: ArrayList<String> = ArrayList()

                        for (document in querySnapshot.documents) {
                            itemDeadlines.add(document.get("modelBorrowDeadline").toString())
                        }

                        for (itemDeadline in itemDeadlines) {
                            if (getBorrowTimeDifference(itemDeadline).toLong() <= 0) {
                                cvPastDueNotice.visibility = View.VISIBLE
                                cvPastDueNotice.startAnimation(AnimationUtils.loadAnimation(hostFragment.requireActivity(), R.anim.rotate))

                                CoroutineScope(Dispatchers.Main).launch {
                                    delay(2000)
                                    cvPastDueNotice.clearAnimation()
                                }
                            }
                        }
                    }
                }.addOnFailureListener { exception ->
                    Log.e(Constants.TAG, "checkPastDue-not-empty: ${exception.message}", )
                }
        }
    }

    private fun getBorrowTimeDifference(modelBorrowDeadlineDateTime: String): String {
        val borrowDeadlineDateTime = modelBorrowDeadlineDateTime.split("-")

        val borrowDeadlineDateTimeFinal = "${borrowDeadlineDateTime[0]} ${borrowDeadlineDateTime[1].replace("\\s*:\\s*".toRegex(), ":")}"
        val currentDateTime = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()).format(
            Date()
        )

        return calculateDateTimeDifferenceInMinutes(currentDateTime, borrowDeadlineDateTimeFinal).toString()
    }

    private fun calculateDateTimeDifferenceInMinutes(dateTime1: String, dateTime2: String): Long {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())

        // Parse date-time strings to Date objects
        val parsedDateTime1 = dateFormat.parse(dateTime1)
        val parsedDateTime2 = dateFormat.parse(dateTime2)

        // Calculate time difference in milliseconds
        val timeDifference = parsedDateTime2!!.time - parsedDateTime1!!.time

        // Convert milliseconds to minutes
        return timeDifference / (60 * 1000)
    }
}