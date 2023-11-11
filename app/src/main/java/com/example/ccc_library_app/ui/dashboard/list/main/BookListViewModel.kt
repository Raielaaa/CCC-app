package com.example.ccc_library_app.ui.dashboard.list.main

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ccc_library_app.R
import com.example.ccc_library_app.ui.account.util.Resources
import com.example.ccc_library_app.ui.dashboard.list.BookListAdapter
import com.example.ccc_library_app.ui.dashboard.list.BookListInfoModel
import com.example.ccc_library_app.ui.dashboard.list.BookListItemDecoration
import com.example.ccc_library_app.ui.dashboard.list.BookListItemModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class BookListViewModel @Inject constructor(
    @Named("FirebaseFireStore.Instance")
    val firebaseFireStore: FirebaseFirestore,
    @Named("FirebaseStorage.Instance")
    val firebaseStorage: StorageReference
) : ViewModel() {
    private val TAG: String = "MyTag"
    private var listOfBookListItemModel = ArrayList<BookListItemModel>()

    fun navigateHome(hostFragment: Fragment, ivHome: ImageView) {
        com.example.ccc_library_app.ui.dashboard.util.Resources.navigate(hostFragment, ivHome, R.id.action_bookListFragment_to_homeFragment
        )
    }

    fun navigateBookmark(hostFragment: Fragment, ivBookmark: ImageView) {
        com.example.ccc_library_app.ui.dashboard.util.Resources.navigate(hostFragment, ivBookmark, R.id.action_bookListFragment_to_bookmarkFragment
        )
    }

    fun navigateSettings(hostFragment: Fragment, ivSettings: ImageView) {
        com.example.ccc_library_app.ui.dashboard.util.Resources.navigate(hostFragment, ivSettings, R.id.action_bookListFragment_to_settingsFragment
        )
    }

    fun setUpRecyclerView(activity: Activity, rvMain: RecyclerView) {
        Resources.displayCustomDialog(activity, R.layout.custom_dialog_loading)

        try {
            firebaseFireStore.collection("ccc-library-app-book-info")
                .get()
                .addOnSuccessListener { bookInfoListFromDB ->
                    if (bookInfoListFromDB.isEmpty) {
                        displayToastMessage("Books unavailable.", activity)
                    } else {
                        val bookInfoListLocal = ArrayList<BookListInfoModel>()

                        for (bookInfo in bookInfoListFromDB.documents) {
                            bookInfoListLocal.add(
                                BookListInfoModel(
                                    bookInfo.get("modelBookCode").toString(),
                                    bookInfo.get("modelBookTitle").toString(),
                                    bookInfo.get("modelBookAuthor").toString(),
                                    bookInfo.get("modelBookGenre").toString(),
                                    bookInfo.get("modelBookPublisher").toString(),
                                    bookInfo.get("modelBookPublicationDate").toString(),
                                    bookInfo.get("modelBookImage").toString(),
                                    bookInfo.get("modelBookDescription").toString()
                                )
                            )
                        }

                        var itemsProcessed = 0

                        for (data in bookInfoListLocal) {
                            addDataToBookListItemModel(data, activity, data.modelBookImage) {
                                itemsProcessed++
                                if (itemsProcessed == bookInfoListLocal.size) {
                                    displayInfoToRecyclerView(rvMain, activity)
                                }
                            }
                        }
                    }
                }.addOnFailureListener {
                    displayToastMessage("An error occurred: ${it.localizedMessage}", activity)
                }
        } catch (exception: Exception) {
            displayToastMessage("Critical error occurred: ${exception.localizedMessage}", activity)
        }
    }

    private fun addDataToBookListItemModel(
        data: BookListInfoModel,
        activity: Activity,
        bookNumber: String,
        completion: (Boolean) -> Unit
    ) {
        getImage(data.modelBookImage, activity) { bitmapImage ->
            if (bitmapImage != null) {
                listOfBookListItemModel.add(
                    BookListItemModel(
                        bitmapToUri(activity, bitmapImage, bookNumber)!!,
                        data.modelBookTitle,
                        "Genre: ${data.modelBookGenre}"
                    )
                )
                completion(true) // Signal successful addition of image
            } else {
                completion(false) // Signal failure
            }
        }
    }


    private fun displayInfoToRecyclerView(rvMain: RecyclerView, activity: Activity) {
        rvMain.apply {
            layoutManager = GridLayoutManager(activity, 2)
            adapter = BookListAdapter(listOfBookListItemModel)
            addItemDecoration(
                BookListItemDecoration(
                2,
                50,
                true
            )
            )
        }
        
        Resources.dismissDialog()
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

    private fun displayToastMessage(toastMessage: String, activity: Activity) {
        Toast.makeText(
            activity,
            toastMessage,
            Toast.LENGTH_LONG
        ).show()
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

    private fun showToastMessage(activity: Activity, message: String) {
        Toast.makeText(
            activity,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    fun addImagesToCloudTBD(
        bitmap: Bitmap,
        fileName: String
    ) : UploadTask {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        return firebaseStorage.child(fileName).putBytes(data)
    }
}