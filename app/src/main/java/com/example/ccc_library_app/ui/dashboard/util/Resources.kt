package com.example.ccc_library_app.ui.dashboard.util

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ccc_library_app.R
import com.example.ccc_library_app.ui.account.util.Resources
import com.example.ccc_library_app.ui.dashboard.list.BookListItemModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.lang.reflect.Array

object Resources {
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
}