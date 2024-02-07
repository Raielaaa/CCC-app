package com.example.ccc_library_app.ui.dashboard.list.selected

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.example.ccc_library_app.R
import com.example.ccc_library_app.ui.account.util.Resources
import com.example.ccc_library_app.ui.dashboard.list.BookListInfoModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ClickedBookViewModel @Inject constructor(
    @Named("FirebaseFireStore.Instance")
    val firebaseFireStore: FirebaseFirestore,
    @Named("FirebaseStorage.Instance")
    val firebaseStorage: StorageReference
) : ViewModel() {
    private val TAG: String = "MyTag"

    private fun displayToastMessage(exception: Exception, activity: Activity) {
        Toast.makeText(
            activity,
            exception.localizedMessage,
            Toast.LENGTH_LONG
        ).show()

        Log.e(TAG, "displayToastMessage: ${exception.message}", )
    }

    fun displayBookData(
        tvBookTitleBookList: TextView,
        tvAuthor: TextView,
        tvGenre: TextView,
        tvPublisher: TextView,
        tvPublicationDate: TextView,
        tvSynopsis: TextView,
        bookTitle: String,
        activity: Activity,
        ivMainBG: ImageView,
        hostFragment: Fragment
    ) {
        try {
            firebaseFireStore.collection("ccc-library-app-book-info")
                .get()
                .addOnSuccessListener { document ->
                    for (data in document.documents) {
                        if (data.get("modelBookTitle") == bookTitle) {
                            val completeBookInfo = BookListInfoModel(
                                data.get("modelBookCode").toString(),
                                data.get("modelBookTitle").toString(),
                                data.get("modelBookAuthor").toString(),
                                data.get("modelBookGenre").toString(),
                                data.get("modelBookPublisher").toString(),
                                data.get("modelBookPublicationDate").toString(),
                                data.get("modelBookImage").toString(),
                                data.get("modelBookDescription").toString()
                            )

                            tvBookTitleBookList.text = completeBookInfo.modelBookTitle
                            tvAuthor.text = "Author: " + completeBookInfo.modelBookAuthor
                            tvGenre.text = "Genre: " + completeBookInfo.modelBookGenre
                            tvPublisher.text = "Publisher: " + completeBookInfo.modelBookPublisher
                            tvPublicationDate.text = "Publication date: " + completeBookInfo.modelBookPublicationDate
                            tvSynopsis.text = completeBookInfo.modelBookDescription
//                            getImage(
//                                completeBookInfo.modelBookImage,
//                                activity,
//                                ivMainBG
//                            )

                            val storage = FirebaseStorage.getInstance()
                            val gsReference = storage.getReferenceFromUrl("gs://ccc-library-system.appspot.com/${completeBookInfo.modelBookImage}")

                            Glide.with(hostFragment.requireContext())
                                .load(gsReference)
                                .placeholder(R.drawable.placeholder_image)
                                .error(R.drawable.error_image)
                                .into(ivMainBG)

                            return@addOnSuccessListener
                        }
                    }
                }.addOnFailureListener { exception ->
                    displayToastMessage(exception, activity)
                }
        } catch (exception: Exception) {
            displayToastMessage(exception, activity)
            Resources.dismissDialog()
        }
    }

//    private fun getImage(
//        filePath: String,
//        activity: Activity,
//        ivMainBG: ImageView
//    ) {
//        firebaseStorage.child(filePath).getBytes(1_048_576L)
//            .addOnSuccessListener { data ->
//                // Convert the byte array to a Bitmap
//                val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
//                ivMainBG.setImageBitmap(bitmap)
//
//                Resources.dismissDialog()
//            }.addOnFailureListener { exception ->
//                // Handle the failure, e.g., show an error message or log the exception
//                Log.e("MyTag", "Error downloading image: ${exception.message}", exception)
//                showToastMessage(
//                    activity,
//                    "Error downloading image: ${exception.localizedMessage}"
//                )
//            }
//    }
    private fun showToastMessage(activity: Activity, message: String) {
        Toast.makeText(
            activity,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}