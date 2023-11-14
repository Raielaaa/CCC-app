package com.example.ccc_library_app.ui.dashboard.home

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.ccc_library_app.R
import com.example.ccc_library_app.ui.dashboard.home.popular.FirebaseDataModel
import com.example.ccc_library_app.ui.dashboard.home.popular.PopularAdapter
import com.example.ccc_library_app.ui.dashboard.home.popular.PopularModel
import com.example.ccc_library_app.ui.dashboard.util.Resources
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    @Named("FirebaseFireStore.Instance")
    val firebaseFireStore: FirebaseFirestore,
    @Named("FirebaseStorage.Instance")
    val firebaseStorage: StorageReference
) : ViewModel() {
    private var TAG: String = "MyTag"

    private lateinit var bookListPopularTemp: ArrayList<FirebaseDataModel>
    private var bookListPopularFinal: ArrayList<PopularModel> = ArrayList()

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

    fun initPopularRecyclerView(
        recyclerView: RecyclerView,
        activity: Activity
    ) {
        try {
            com.example.ccc_library_app.ui.account.util.Resources.displayCustomDialog(
                activity,
                R.layout.custom_dialog_loading
            )

            bookListPopularTemp = ArrayList()

            firebaseFireStore.collection("ccc-library-app-book-info")
                .get()
                .addOnSuccessListener { bookInfoListFromDB ->
                    if (bookInfoListFromDB.isEmpty) {
                        Toast.makeText(
                            activity,
                            "Popular books unavailable",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        for (bookInfo in bookInfoListFromDB) {
                            bookListPopularTemp.add(
                                FirebaseDataModel(
                                    bookInfo.get("modelBookTitle").toString(),
                                    bookInfo.get("modelBookImage").toString()
                                )
                            )
                        }

                        var itemsProcessed = 0
                        for (bookData in bookListPopularTemp) {
                            displayPopularRV(bookData, recyclerView, activity, bookData.modelBookImage) {
                                itemsProcessed++
                                if (itemsProcessed == bookListPopularTemp.size) {
                                    displayInfoToRecyclerView(recyclerView, activity, bookListPopularFinal)
                                }
                            }
                        }
                    }
                }
        } catch (exception: Exception) {
            Toast.makeText(
                activity,
                "An error occurred: ${exception.localizedMessage}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun displayInfoToRecyclerView(recyclerView: RecyclerView, activity: Activity, bookListPopularFinal: ArrayList<PopularModel>) {
        val adapter = PopularAdapter()
        recyclerView.adapter = adapter
        adapter.setList(bookListPopularFinal)

        com.example.ccc_library_app.ui.account.util.Resources.dismissDialog()
    }

    private fun displayPopularRV(
        data: FirebaseDataModel,
        recyclerView: RecyclerView,
        activity: Activity,
        bookCode: String,
        completion: (Boolean) -> Unit
    ) {
        getImage(data.modelBookImage, activity) { bitmapImage ->
            if (bitmapImage == null) {
                showToastMessage(activity, "An error occurred displaying the books")
                completion(false) // Signal failure
            } else {
                bookListPopularFinal.add(
                    PopularModel(
                        bitmapToUri(activity, bitmapImage, bookCode)!!,
                        data.modelBookTitle
                    )
                )
                completion(true)
            }
        }
    }

    private fun getImage(filePath: String, activity: Activity, callback: (Bitmap?) -> Unit) {
        Log.d(TAG, "getImage: $filePath")
        firebaseStorage.child(filePath).getBytes(1_048_576L)
            .addOnSuccessListener { data ->
                // Convert the byte array to a Bitmap
                val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                callback(bitmap)
            }.addOnFailureListener { exception ->
                // Indicate failure by passing null to the callback
                callback(null)

                // Handle the failure, e.g., show an error message or log the exception
                Log.e("MyTag", "Error downloading image: ${exception.message}", exception)
                showToastMessage(
                    activity,
                    "Error downloading image: ${exception.localizedMessage}"
                )
            }
    }

    private fun bitmapToUri(activity: Activity, bitmap: Bitmap, bookNumber: String): Uri? {
        // Get the directory for storing the image
        val imagesDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val formattedFileName = bookNumber.split("/")
        val imageFile = File(imagesDir, "${formattedFileName[1]}.png")

        try {
            // Create a FileOutputStream to write the bitmap to the file
            val fos = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.close()

            // Create a Uri from the file
            return Uri.fromFile(imageFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    private fun showToastMessage(activity: Activity, message: String) {
        Toast.makeText(
            activity,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}