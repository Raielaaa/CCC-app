package com.example.ccc_library_app.ui.dashboard.home.db

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ccc_library_app.ui.account.util.Resources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FirebaseDBManager : AppCompatActivity() {
    //  General
    private val TAG: String = "MyTag"

    //  Date and Time
    private val currentTime = Calendar.getInstance()
    private val sdf = SimpleDateFormat("MM/dd/yy hh:mm a", Locale.getDefault())

    fun insertDataToDB(
        rawValue: String?,
        activity: Activity,
        fireStore: FirebaseFirestore,
        auth: FirebaseAuth
    ) {
        if (rawValue != null) {
            val bookInfo: List<String> = rawValue.toString().split(":")
            val userID = auth.currentUser!!.uid

            organizeData(
                userID,
                bookInfo,
                activity,
                fireStore
            )
        } else {
            Toast.makeText(
                activity,
                "QR not defined. Please scan again.",
                Toast.LENGTH_LONG
            ).show()
            Resources.dismissDialog()
        }
    }

    private fun organizeData(
        userID: String,
        bookInfo: List<String>,
        activity: Activity,
        fireStore: FirebaseFirestore
    ) {
        fireStore.collection("ccc-library-app-user-data")
            .document(userID)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val userData = UserData(
                        userFirstName = document.data!!["modelFirstName"].toString(),
                        userLastName = document.data!!["modelLastName"].toString(),
                        userProgram = document.data!!["modelProgram"].toString(),
                        userSection = document.data!!["modelSection"].toString(),
                        userYear = document.data!!["modelYear"].toString()
                    )

                    initBorrowDataModel(
                        userID,
                        userData,
                        bookInfo,
                        activity,
                        fireStore
                    )
                } else {
                    Toast.makeText(
                        activity,
                        "No such document",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun initBorrowDataModel(
        userID: String,
        userData: UserData,
        bookInfo: List<String>,
        activity: Activity,
        fireStore: FirebaseFirestore
    ) {
        val borrowInfo = BorrowDataModel(
            userID,
            bookInfo[3], //  Book code
            bookInfo[0], //  Book name
            bookInfo[1], //  Book author
            bookInfo[2], //  Book genre
            "${userData.userFirstName} ${userData.userLastName}",
            userData.userProgram,
            userData.userSection,
            userData.userYear,
            getCurrentBorrowDate(),
            getBorrowDeadlineDate(),
            "On borrow"
        )

        insertBorrowDataToFireStore(
            borrowInfo,
            userID,
            activity,
            fireStore,
            bookInfo[3]
        )
    }

    private fun insertBorrowDataToFireStore(
        borrowInfo: BorrowDataModel,
        userID: String,
        activity: Activity,
        fireStore: FirebaseFirestore,
        bookCode: String
    ) {
        fireStore.collection("ccc-library-app-borrow-data")
            .document(userID)
            .set(borrowInfo)
            .addOnSuccessListener {
                Toast.makeText(
                    activity,
                    "Book successfully registered.",
                    Toast.LENGTH_LONG
                ).show()

                //  Update ccc-library-app-book-info modelBookStatus
                updateBookInfoStatus(
                    bookCode,
                    fireStore,
                    activity
                )

                setBookBorrowStatus(
                    borrowInfo,
                    activity,
                    fireStore
                )
            }.addOnFailureListener { exception ->
                Toast.makeText(activity, exception.localizedMessage, Toast.LENGTH_LONG).show()
                Log.e(TAG, "insertDataToDB: ${exception.message}")
            }
    }

    private fun updateBookInfoStatus(
        bookCode: String,
        fireStore: FirebaseFirestore,
        activity: Activity
    ) {
        fireStore.collection("ccc-library-app-book-info")
            .document(bookCode)
            .update("modelStatus", "Unavailable")
            .addOnSuccessListener {
                Toast.makeText(
                    activity,
                    "Book availability status updated",
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnFailureListener {
                Toast.makeText(
                    activity,
                    "Book availability status update failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun setBookBorrowStatus(
        borrowInfo: BorrowDataModel,
        activity: Activity,
        fireStore: FirebaseFirestore
    ) {
        fireStore.collection("ccc-library-app-book-data")
            .document(borrowInfo.modelBookCode)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    var currentBookBorrowCount = 0

                    try {
                        currentBookBorrowCount = document.data!!["modelBookCount"].toString().toInt()
                    } catch (ignored: Exception) {
                        val bookInfo = BookDataModel(
                            borrowInfo.modelBookCode,
                            borrowInfo.modelBookName,
                            borrowInfo.modelBookAuthor,
                            borrowInfo.modelBookGenre,
                            "1"
                        )

                        fireStore.collection("ccc-library-app-book-data")
                            .document(borrowInfo.modelBookCode)
                            .set(bookInfo)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    activity,
                                    "Book status successfully updated",
                                    Toast.LENGTH_LONG
                                ).show()
                                Resources.dismissDialog()
                            }
                    }

                    updateBookBorrowStatus(
                        currentBookBorrowCount,
                        borrowInfo.modelBookCode,
                        activity,
                        fireStore
                    )
                }
            }
    }

    private fun updateBookBorrowStatus(
        currentBookBorrowCount: Int,
        bookCode: String,
        activity: Activity,
        fireStore: FirebaseFirestore
    ) {
        fireStore.collection("ccc-library-app-book-data")
            .document(bookCode)
            .update("modelBookCount", "${currentBookBorrowCount + 1}")
            .addOnSuccessListener {
                Toast.makeText(
                    activity,
                    "Book status successfully updated",
                    Toast.LENGTH_LONG
                ).show()
                Resources.dismissDialog()
            }
    }

    private fun getBorrowDeadlineDate(): String {
        currentTime.add(
            Calendar.DAY_OF_MONTH,
            if (currentTime.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) 4 else 3
        )

        return sdf.format(currentTime.time)
    }

    private fun getCurrentBorrowDate() = sdf.format(currentTime.time)

    data class UserData(
        val userFirstName: String,
        val userLastName: String,
        val userProgram: String,
        val userSection: String,
        val userYear: String
    )
}