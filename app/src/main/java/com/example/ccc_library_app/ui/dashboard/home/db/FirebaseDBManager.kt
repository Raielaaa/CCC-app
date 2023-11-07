package com.example.ccc_library_app.ui.dashboard.home.db

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ccc_library_app.di.AppModule
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Component
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class FirebaseDBManager : AppCompatActivity() {
    @Named("FirebaseFireStore.Instance")
    lateinit var firebaseFireStore: FirebaseFirestore

    private val TAG: String = "MyTag"

    fun insertDataToDB(rawValue: String?, activity: Activity) {
        firebaseFireStore.collection("ccc-library-app-user-data").get()
        Log.d(TAG, "insertDataToDB: ${rawValue.toString()}")
        if (rawValue != null) {
            val bookInfo: List<String> = rawValue.toString().split(":")
        } else {
            Toast.makeText(activity, "QR not defined. Please scan again.", Toast.LENGTH_LONG).show()
            com.example.ccc_library_app.ui.account.util.Resources.dismissDialog()
        }
    }
}