package com.example.ccc_library_app.ui.dashboard.bookmark

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.ccc_library_app.R
import com.example.ccc_library_app.databinding.FragmentBookmarkBinding
import com.example.ccc_library_app.ui.dashboard.bookmark.rv.BookmarkAdapter
import com.example.ccc_library_app.ui.dashboard.util.Constants
import com.example.ccc_library_app.ui.dashboard.util.DataCache
import com.example.ccc_library_app.ui.dashboard.util.Resources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel

class BookmarkViewModel : ViewModel() {
    fun navigateHome(hostFragment: Fragment, ivHome: ImageView) {
        Resources.navigate(hostFragment, ivHome, R.id.action_bookmarkFragment_to_homeFragment)
    }

    fun navigateBookList(hostFragment: Fragment, ivBookList: ImageView) {
        Resources.navigate(hostFragment, ivBookList, R.id.action_bookmarkFragment_to_bookListFragment)
    }

    fun navigateSettings(hostFragment: Fragment, ivSettings: ImageView) {
        Resources.navigate(hostFragment, ivSettings, R.id.action_bookmarkFragment_to_settingsFragment)
    }

    fun initRV(
        binding: FragmentBookmarkBinding,
        bookmarkFragment: Fragment,
        rvBookmarkMain: RecyclerView,
        firebaseAuth: FirebaseAuth,
        firebaseFireStore: FirebaseFirestore,
        firebaseStorage: FirebaseStorage,
        ivNoData: ImageView
    ) {
        DataCache.bookmarkPastDueCounter = 0
        DataCache.bookmarkBorrowCount = 0
        com.example.ccc_library_app.ui.account.util.Resources.displayCustomDialog(
            bookmarkFragment.requireActivity(),
            R.layout.custom_dialog_loading
        )

        firebaseFireStore.collection("ccc-library-app-user-data")
            .document(firebaseAuth.uid.toString())
            .get()
            .addOnCompleteListener { documentSnapshot ->
                if (documentSnapshot.isSuccessful) {
                    val firstName = documentSnapshot.result.get("modelFirstName")
                    val lastName = documentSnapshot.result.get("modelLastName")
                    val program = documentSnapshot.result.get("modelSection")

                    val filter = "$firstName$lastName".replace(" ", "") + "-$program"
                    Log.d(Constants.TAG, "initRV: $filter")

                    firebaseFireStore.collection("ccc-library-app-borrow-data")
                        .whereGreaterThanOrEqualTo(FieldPath.documentId(), filter)
                        .whereLessThan(FieldPath.documentId(), filter + '\uF7FF')
                        .get()
                        .addOnCompleteListener { querySnapshot ->
                            if (!querySnapshot.result.isEmpty) {
                                val listOfBorrows = ArrayList<BookmarkModel>()

                                for (data in querySnapshot.result.documents) {
                                    listOfBorrows.add(
                                        BookmarkModel(
                                            data.get("modelBookTitle").toString(),
                                            data.get("modelBorrowDate").toString(),
                                            data.get("modelBorrowDeadline").toString(),
                                            data.get("modelBookGenre").toString(),
                                            data.get("modelBookCode").toString()
                                        )
                                    )

                                    DataCache.bookmarkBorrowCount++
                                }

                                binding.apply {
                                    val bookmarkAdapter = BookmarkAdapter(
                                        firebaseStorage,
                                        bookmarkFragment.requireContext(),
                                        tvTopUsername,
                                        tvTopBorrowCount,
                                        tvTopStatus,
                                        cvTopStatus,
                                        ivTopLogout,
                                        bookmarkFragment,
                                        firebaseFireStore,
                                        firebaseAuth
                                    )

                                    bookmarkAdapter.setList(listOfBorrows)
                                    rvBookmarkMain.adapter = bookmarkAdapter
                                }

                                com.example.ccc_library_app.ui.account.util.Resources.dismissDialog()
                            } else {
                                ivNoData.visibility = View.VISIBLE
                                com.example.ccc_library_app.ui.account.util.Resources.dismissDialog()
                            }
                        }.addOnFailureListener { exception ->
                            displayErr(exception, bookmarkFragment)
                        }
                } else {
                    Toast.makeText(
                        bookmarkFragment.requireContext(),
                        "Error on getting data. Please check your connection",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener { exception ->
                displayErr(exception, bookmarkFragment)
            }
    }

    private fun displayErr(exception: Exception, bookmarkFragment: Fragment) {
        Log.e(Constants.TAG, "displayErr: ${exception.message}")
        Toast.makeText(
            bookmarkFragment.requireContext(),
            "An error occurred: ${exception.localizedMessage}",
            Toast.LENGTH_SHORT
        ).show()
    }
}