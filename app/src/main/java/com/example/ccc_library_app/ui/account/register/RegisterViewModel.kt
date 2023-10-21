package com.example.ccc_library_app.ui.account.register

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class RegisterViewModel @Inject constructor(
    @Named("FirebaseAuth.Instance")
    val firebaseAuth: FirebaseAuth,
    @Named("FirebaseFireStore.Instance")
    val firebaseFireStore: FirebaseFirestore
) : ViewModel() {
    private val TAG: String = "MyTag"

    fun validateInputRegister(
        modelFirstName: String,
        modelLastName: String,
        modelProgram: String,
        modelYear: String,
        modelSection: String,
        modelUsername: String,
        modelEmail: String,
        modelPassword: String,
        modelConfirmPassword: String
    ): Boolean {
        return modelFirstName.isNotEmpty() &&
                modelLastName.isNotEmpty() &&
                modelProgram.isNotEmpty() &&
                modelYear.isNotEmpty() &&
                modelSection.isNotEmpty() &&
                modelUsername.isNotEmpty() &&
                modelEmail.isNotEmpty() &&
                modelPassword.isNotEmpty() &&
                modelConfirmPassword.isNotEmpty()
    }

    fun validatePasswordRegister(modelPassword: String, modelConfirmPassword: String): Boolean =
        modelPassword == modelConfirmPassword

    fun insertDataToFirebase(
        modelFirstName: String,
        modelLastName: String,
        modelProgram: String,
        modelYear: String,
        modelSection: String,
        modelUsername: String,
        modelEmail: String,
        modelPassword: String,
        context: Context
    ) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                firebaseAuth.createUserWithEmailAndPassword(modelEmail, modelPassword).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userID = task.result.user!!.uid

                        val userData = DataModel(
                            userID,
                            modelFirstName,
                            modelLastName,
                            modelProgram,
                            modelYear,
                            modelSection,
                            modelUsername
                        )

                        firebaseFireStore.collection("ccc-library-app-user-data")
                            .add(userData)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Register successful", Toast.LENGTH_LONG).show()
                            }.addOnFailureListener { exception ->
                                Toast.makeText(context, exception.localizedMessage, Toast.LENGTH_LONG).show()
                                Log.e(TAG, "insertDataToFirebaseAuth-FireStoreException: ${exception.message}", )
                            }
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(context, exception.localizedMessage, Toast.LENGTH_LONG).show()
                    Log.e(TAG, "insertDataToFirebaseAuth-AuthException: ${exception.message}", )
                }
            }
        } catch (exception: Exception) {
            Toast.makeText(context, exception.localizedMessage, Toast.LENGTH_LONG).show()
            Log.e(TAG, "insertDataToFirebaseAuth-root: ${exception.message}", )
        }
    }
}