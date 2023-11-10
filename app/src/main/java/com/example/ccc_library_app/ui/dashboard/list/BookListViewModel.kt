package com.example.ccc_library_app.ui.dashboard.list

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.example.ccc_library_app.R
import com.example.ccc_library_app.ui.account.util.Resources
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.ByteArrayOutputStream
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

    fun setUpRecyclerView(
        activity: Activity
    ) {
        Resources.displayCustomDialog(
            activity,
            R.layout.custom_dialog_loading
        )

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
                                    bookInfo.get("modelBookDescription").toString()
                                )
                            )
                        }
                    }
                }.addOnFailureListener {
                    displayToastMessage("An error occurred: ${it.localizedMessage}", activity)
                }
        } catch (exception: Exception) {
            displayToastMessage("Critical error occurred: ${exception.localizedMessage}", activity)
        } finally {
            Resources.dismissDialog()
        }
    }

    private fun displayToastMessage(toastMessage: String, activity: Activity) {
        Toast.makeText(
            activity,
            toastMessage,
            Toast.LENGTH_LONG
        ).show()
    }

    fun showImage(ivPicPlaceholder: ImageView) {
        firebaseStorage.child("book_images/183490.jpg").getBytes(1_048_576L).addOnSuccessListener { data ->
            // Convert the byte array to a Bitmap
            val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
            if (bitmap != null) {
                // Set the Bitmap to the ImageView
                ivPicPlaceholder.setImageBitmap(bitmap)
            } else {
                // Handle the case where the conversion to Bitmap failed
                // You can show an error message or set a default image.
            }
        }.addOnFailureListener { exception ->
            // Handle the failure, e.g., show an error message or log the exception
            Log.e("MyTag", "Error downloading image: ${exception.message}", exception)
        }
    }

//    fun addImagesToCloudTBD(
//        bitmap: Bitmap,
//        fileName: String
//    ) : UploadTask {
//        val baos = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//        val data = baos.toByteArray()
//
//        return firebaseStorage.child(fileName).putBytes(data)
//    }
}