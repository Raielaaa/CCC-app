package com.example.ccc_library_app.ui.dashboard.borrow_return

import android.app.Activity
import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.example.ccc_library_app.R
import com.example.ccc_library_app.ui.account.util.Resources
import com.example.ccc_library_app.ui.dashboard.borrow_return.date_time.BRDModelFinal
import com.example.ccc_library_app.ui.dashboard.util.Constants
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
                        Resources.dismissDialog()
                        bottomSheetDialogFragment.dismiss()
                        Resources.displayCustomDialog(
                            hostFragment.requireActivity(),
                            R.layout.custom_dialog_notice,
                            "Borrow notice",
                            "Borrow unsuccessful. Borrow limit reached."
                        )
                    } else {
                        firebaseFireStore.collection("ccc-library-app-borrow-data")
                            .document("$filter-${getCurrentDateTime()}".replace("/", "_"))
                            .set(dataToBeInputted)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    //  Insert borrow data to DB's permanent record list
                                    firebaseFireStore.collection("ccc-library-app-borrow-data-history")
                                        .document("$filter-${getCurrentDateTime()}".replace("/", "_"))
                                        .set(dataToBeInputted)
                                        .addOnSuccessListener {
                                            Log.d("MyTag", "insertInfoToFireStore: $filter-${getCurrentDateTime()}\".replace(\"/\", \"_\") was successfully inserted in the history list records")
                                        }.addOnFailureListener { exception ->
                                            Log.e("MyTag", "insertInfoToFireStore: ${exception.message} - Failed to insert borrow data to history list records")
                                        }

                                    //  Increase book borrow count
                                    increaseBookBorrowCount(
                                        bookCode,
                                        hostFragment,
                                        bottomSheetDialogFragment,
                                        bookTitle,
                                        borrowDeadline
                                    )
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

    private fun increaseBookBorrowCount(
        bookCode: String,
        hostFragment: Fragment,
        bottomSheetDialogFragment: BorrowReturnDialogFragment,
        bookName: String,
        borrowDeadline: String
    ) {
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
                            changeBookStatus(bookCode, bottomSheetDialogFragment, bookName, borrowDeadline)
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

    private fun changeBookStatus(
        bookCode: String,
        bottomSheetDialogFragment: BorrowReturnDialogFragment,
        bookName: String,
        borrowDeadline: String
    ) {
        firebaseFireStore.collection("ccc-library-app-book-info")
            .document(bookCode)
            .update("modelStatus", "Unavailable")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //  Requesting permission before Setting up notification
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        checkAndRequestExactAlarmPermission(bottomSheetDialogFragment, bookName, borrowDeadline)
                    }

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

    private fun checkAndRequestExactAlarmPermission(
        bottomSheetDialogFragment: BorrowReturnDialogFragment,
        bookName: String,
        borrowDeadline: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val permission = android.Manifest.permission.SCHEDULE_EXACT_ALARM
            if (ContextCompat.checkSelfPermission(bottomSheetDialogFragment.requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                setUpNotification(bottomSheetDialogFragment.requireActivity(), bookName, borrowDeadline)
            } else {
                Toast.makeText(
                    bottomSheetDialogFragment.requireContext(),
                    "Cannot set up notification",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            setUpNotification(bottomSheetDialogFragment.requireActivity(), bookName, borrowDeadline)
        }
    }

    private fun setUpNotification(activity: Activity, bookName: String, borrowDeadline: String) {
        val intent = Intent(activity.applicationContext, Notification::class.java)
        val title = "Return Reminder: $bookName"
        val message = "Friendly reminder to return $bookName within 12 hours. If already returned, please ignore this notification."
        intent.putExtra(Constants.titleExtra, title)
        intent.putExtra(Constants.messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            activity.applicationContext,
            Constants.notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTimeForNotification(borrowDeadline)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }

    //  Functions for notification
    private fun getTimeForNotification(modelBorrowDeadlineDateTime: String): Long {
        Log.d("MyTag", "getTimeForNotification: $modelBorrowDeadlineDateTime")
        val borrowDeadlineDateTime = modelBorrowDeadlineDateTime.split("-")

        val borrowDeadlineDateTimeFinal =
            "${borrowDeadlineDateTime[0]} ${borrowDeadlineDateTime[1].replace("\\s*:\\s*".toRegex(), ":")}"

        Log.d("MyTag", "getTimeForNotification: ${getTwelveHoursBeforeDeadline(borrowDeadlineDateTimeFinal)}")
        // Calculate 12 hours before the deadline
        return getTwelveHoursBeforeDeadline(borrowDeadlineDateTimeFinal)
    }

    private fun getTwelveHoursBeforeDeadline(deadlineDateTime: String): Long {
        val dateFormat = SimpleDateFormat("d/M/yyyy hh:mm a", Locale.getDefault())
        val deadlineCalendar = Calendar.getInstance()

        // Parse the deadline date-time string to Calendar
        deadlineCalendar.time = dateFormat.parse(deadlineDateTime)!!

        // Subtract 12 hours from the deadline
        deadlineCalendar.add(Calendar.HOUR_OF_DAY, - 12)

        // Return the result as a long value representing time in milliseconds
        return deadlineCalendar.timeInMillis
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

    fun checkReturnAuthenticity(
        borrowReturnDialogFragment: BorrowReturnDialogFragment,
        userName: String,
        userSection: String,
        bookCode: String,
        title: String,
        author: String,
        genre: String,
        email: String
    ) {
        Resources.displayCustomDialog(
            borrowReturnDialogFragment.requireActivity(),
            R.layout.custom_dialog_loading
        )
        val filter = "${userName.replace(" ", "")}-$userSection"

        firebaseFireStore.collection("ccc-library-app-borrow-data")
            .whereGreaterThanOrEqualTo(FieldPath.documentId(), filter)
            .whereLessThan(FieldPath.documentId(), filter + '\uF7FF')
            .whereEqualTo("modelBookCode", bookCode)
            .get()
            .addOnCompleteListener { querySnapshot ->
                if (querySnapshot.isSuccessful) {
                    if (querySnapshot.result.documents.isEmpty()) {
                        Resources.dismissDialog()
                        borrowReturnDialogFragment.dismiss()
                        Resources.displayCustomDialog(
                            borrowReturnDialogFragment.requireActivity(),
                            R.layout.custom_dialog_notice,
                            "Return Unsuccessful",
                            "The scanned item is not currently listed in your active borrowing records."
                        )
                    } else {
                        //  Add user's returned info to persistent borrow info list on DB
                        val dataToBeInserted = PersistentBorrowList(
                            bookCode,
                            title,
                            author,
                            genre,
                            filter,
                            email
                        )

                        firebaseFireStore.collection("ccc-library-app-return-info")
                            .document(filter)
                            .set(dataToBeInserted)
                            .addOnSuccessListener {
                                Log.d("MyTag", "checkReturnAuthenticity: Returned item successfully inserted in the persistent return info list on DB")
                            }.addOnFailureListener { exception ->
                                Log.e("MyTag", "checkReturnAuthenticity: ${exception.message} - Error on inserting current returned data to persistent return info list on DB")
                            }


                        //  Delete users info from borrow info list
                        for (data in querySnapshot.result.documents) {
                            data.reference.delete()
                        }

                        //  Update book status
                        firebaseFireStore.collection("ccc-library-app-book-info")
                            .document(bookCode)
                            .update("modelStatus", "Available")
                            .addOnSuccessListener {
                                Resources.dismissDialog()
                                borrowReturnDialogFragment.dismiss()
                                Resources.displayCustomDialog(
                                    borrowReturnDialogFragment.requireActivity(),
                                    R.layout.custom_dialog_notice,
                                    "Return Successful",
                                    "Process successful. Book has been returned successfully, kindly refresh the page."
                                )
                            }.addOnFailureListener { exception ->
                                displayErr(exception, borrowReturnDialogFragment.requireContext())
                            }
                    }
                } else {
                    Toast.makeText(
                        borrowReturnDialogFragment.requireContext(),
                        "An error occurred: Can't find borrow info",
                        Toast.LENGTH_SHORT
                    ).show()
                    Resources.dismissDialog()
                }
            }.addOnFailureListener { exception ->
                displayErr(exception, borrowReturnDialogFragment.requireContext())
            }
    }
}