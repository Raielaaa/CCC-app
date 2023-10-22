package com.example.ccc_library_app.ui.account.register

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.ccc_library_app.R
import com.google.android.material.textfield.TextInputEditText
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

    fun validatePasswordStrength(
        etPassword: TextInputEditText,
        ivPassword1: ImageView,
        tvPassword1: TextView,
        ivPassword2: ImageView,
        tvPassword2: TextView,
        ivPassword3: ImageView,
        tvPassword3: TextView,
        ivPassword4: ImageView,
        tvPassword4: TextView,
        context: Context
    ) {
        etPassword.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(inputtedText: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val (hasLetter, hasNumber, hasSpecialCharacter, hasAtLeastEightCharacters) = checkString(inputtedText.toString())

                updateIconAndTextColor(ivPassword1, tvPassword1, hasLetter, context)
                updateIconAndTextColor(ivPassword2, tvPassword2, hasNumber, context)
                updateIconAndTextColor(ivPassword3, tvPassword3, hasSpecialCharacter, context)
                updateIconAndTextColor(ivPassword4, tvPassword4, hasAtLeastEightCharacters, context)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                TODO("Not yet implemented")
            }

            override fun afterTextChanged(p0: Editable?) {
//                TODO("Not yet implemented")
            }

        })
    }

    private fun updateIconAndTextColor(
        imageView: ImageView,
        textView: TextView,
        condition: Boolean,
        context: Context
    ) {
        val color = if (condition) {
            ContextCompat.getColor(context, R.color.Theme_color)
        } else {
            ContextCompat.getColor(context, R.color.defaultIconColor)
        }

        imageView.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        textView.setTextColor(color)
    }

    private fun checkString(string: String) : Quadruple<Boolean, Boolean, Boolean, Boolean> {
        val hasLetter = Regex("[a-zA-Z]").containsMatchIn(string)
        val hasNumber = Regex("\\d").containsMatchIn(string)
        val hasSpecialCharacter = Regex("""[!@#\$%^\&*()-+\=~`]""").containsMatchIn(string)
        val hasAtLeastEightCharacters = string.length > 8

        return Quadruple(hasLetter, hasNumber, hasSpecialCharacter, hasAtLeastEightCharacters)
    }
    @SuppressLint("ObsoleteSdkInt")
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            networkCapabilities != null &&
                    (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }
}