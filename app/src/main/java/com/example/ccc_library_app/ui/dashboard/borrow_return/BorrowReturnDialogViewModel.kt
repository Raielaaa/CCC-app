package com.example.ccc_library_app.ui.dashboard.borrow_return

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.example.ccc_library_app.R
import com.example.ccc_library_app.ui.account.util.Resources
import com.example.ccc_library_app.ui.dashboard.borrow_return.date_time.BRDModelFinal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class BorrowReturnDialogViewModel @Inject constructor(
    @Named("FirebaseFireStore.Instance")
    private val firebaseFireStore: FirebaseFirestore,
    @Named("FirebaseAuth.Instance")
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    fun checkBorrowAvailability(
        borrowDeadline: String,
        bookName: String,
        hostFragment: Fragment,
        bottomSheetDialogFragment: BorrowReturnDialogFragment,
        bookAuthor: String,
        bookCode: String,
        bookGenre: String,
        bookTitle: String,
        bookStatus: String,
        vararg userInfo: String
    ) {
        //  Checks if the user reached the borrow limit
        if (bookStatus == "Unavailable") {
            Resources.displayCustomDialog(
                hostFragment.requireActivity(),
                R.layout.custom_dialog_notice,
                "Borrow notice",
                "Borrow unsuccessful. The item is either already reserved or currently unavailable."
            )
        } else {
            //  Insert borrow info to fireStore
            insertInfoToFireStore(
                borrowDeadline,
                hostFragment,
                bottomSheetDialogFragment,
                bookAuthor,
                bookCode,
                bookGenre,
                bookTitle,
                userInfo
            )
        }
    }

    private fun insertInfoToFireStore(
        borrowDeadline: String,
        hostFragment: Fragment,
        bottomSheetDialogFragment: BorrowReturnDialogFragment,
        bookAuthor: String,
        bookCode: String,
        bookGenre: String,
        bookTitle: String,
        userInfo: Array<out String>
    ) {
        val dataToBeInputted = BRDModelFinal(
            getCurrentDateTime(),
            borrowDeadline,
            bookAuthor,
            bookCode,
            bookGenre,
            bookTitle,
            userInfo[0],
            userInfo[1],
            userInfo[2],
            userInfo[3],
            userInfo[4],
            userInfo[5]
        )
        val filter = "${userInfo[0]}-${userInfo[2]}".replace(" ", "")

        Resources.displayCustomDialog(
            hostFragment.requireActivity(),
            R.layout.custom_dialog_loading
        )

        //  Checks first the user's borrow count
        firebaseFireStore.collection("ccc-library-app-borrow-data")
            .whereGreaterThanOrEqualTo(FieldPath.documentId(), filter)
            .whereLessThan(FieldPath.documentId(), filter + '\uF7FF')
            .get()
            .addOnCompleteListener { querySnapshot ->
                if (querySnapshot.isSuccessful) {
                    if (querySnapshot.result.size() >= 3) {
                        Resources.displayCustomDialog(
                            hostFragment.requireActivity(),
                            R.layout.custom_dialog_notice,
                            "Borrow notice",
                            "Borrow unsuccessful. Borrow limit reached."
                        )

                        Resources.dismissDialog()
                    } else {
                        firebaseFireStore.collection("ccc-library-app-borrow-data")
                            .document("$filter-${getCurrentDateTime()}".replace("/", "_"))
                            .set(dataToBeInputted)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    //  Increase book borrow count
                                    increaseBookBorrowCount(bookCode, hostFragment, bottomSheetDialogFragment)
                                } else {
                                    Toast.makeText(
                                        hostFragment.requireContext(),
                                        "An error occurred: Borrow task unsuccessful",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    Resources.dismissDialog()
                                }
                            }.addOnFailureListener { exception ->
                                displayErr(exception, hostFragment.requireContext())
                                Resources.dismissDialog()
                            }
                    }
                } else {
                    Toast.makeText(
                        hostFragment.requireContext(),
                        "An error occurred: Borrow count retrieval unsuccessful, please contact the developer.",
                        Toast.LENGTH_SHORT
                    ).show()
                    Resources.dismissDialog()
                }
            }.addOnFailureListener { exception ->
                displayErr(exception, hostFragment.requireContext())
                Resources.dismissDialog()
            }
    }

    private fun increaseBookBorrowCount(bookCode: String, hostFragment: Fragment, bottomSheetDialogFragment: BorrowReturnDialogFragment) {
        firebaseFireStore.collection("ccc-library-app-book-data")
            .document(bookCode)
            .get()
            .addOnCompleteListener { documentSnapshot ->
                if (documentSnapshot.isSuccessful) {
                    val currentBookCount: Int = documentSnapshot.result.get("modelBookCount").toString().toInt()

                    firebaseFireStore.collection("ccc-library-app-book-data")
                        .document(bookCode)
                        .update("modelBookCount", "${currentBookCount + 1}")
                        .addOnCompleteListener {
                            //  Update book status
                            changeBookStatus(bookCode, bottomSheetDialogFragment)
                        }.addOnFailureListener { exception ->
                            displayErr(exception, hostFragment.requireContext())
                            Resources.dismissDialog()
                        }
                } else {
                    Toast.makeText(
                        hostFragment.requireContext(),
                        "An error occurred: Book count increment failed, please contact the developer",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener { exception ->
                displayErr(exception, hostFragment.requireContext())
                Resources.dismissDialog()
            }
    }

    private fun changeBookStatus(bookCode: String, bottomSheetDialogFragment: BorrowReturnDialogFragment) {
        firebaseFireStore.collection("ccc-library-app-book-info")
            .document(bookCode)
            .update("modelStatus", "Unavailable")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Resources.dismissDialog()
                    bottomSheetDialogFragment.dismiss()

                    Resources.displayCustomDialog(
                        bottomSheetDialogFragment.requireActivity(),
                        R.layout.custom_dialog_notice,
                        "Borrow notice",
                        "Borrow successful. Kindly refresh your profile status."
                    )
                } else {
                    Toast.makeText(
                        bottomSheetDialogFragment.requireContext(),
                        "An error occurred: Cannot update book status, please contact the developer",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener { exception ->
                displayErr(exception, bottomSheetDialogFragment.requireContext())
            }
    }

    private fun getCurrentDateTime(): String {
        // Get the current date and time
        val calendar = Calendar.getInstance()
        val currentDateAndTime = calendar.time

        // Define the desired date and time format
        val dateFormat = SimpleDateFormat("dd/MM/yyyy-hh:mm a", Locale.getDefault())

        // Format the date and time

        return dateFormat.format(currentDateAndTime)
    }

    private fun displayErr(exception: Exception, context: Context) {
        Resources.dismissDialog()
        Toast.makeText(
            context,
            "An error occurred: ${exception.localizedMessage}",
            Toast.LENGTH_SHORT
        ).show()
        Log.e("MyTag", "displayErr: ", exception)
    }
}