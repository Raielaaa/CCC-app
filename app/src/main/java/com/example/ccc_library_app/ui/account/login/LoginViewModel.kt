package com.example.ccc_library_app.ui.account.login

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.ccc_library_app.R
import com.example.ccc_library_app.ui.account.util.Resources
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

@Suppress("DEPRECATION")
@HiltViewModel
class LoginViewModel @Inject constructor(
    @Named("FirebaseAuth.Instance")
    val firebaseAuth: FirebaseAuth
): ViewModel() {
    private val TAG: String = "MyTag"

    fun validateCredentials(
        email: String,
        password: String,
        context: Context,
        fragment: Fragment,
        etEmail: TextInputEditText,
        etPassword: TextInputEditText,
        txtInputLayoutEmail: TextInputLayout,
        txtInputLayoutPW: TextInputLayout,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                Resources.displayCustomDialog(
                    activity = fragment.requireActivity(),
                    hostFragment = fragment,
                    layoutDialog = R.layout.custom_dialog_loading
                )
            }
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            context,
                            "Login successful",
                            Toast.LENGTH_LONG
                        ).show()
                        Resources.dismissDialog()
                        fragment.findNavController().navigate(R.id.homeFragment)
                    } else {
                        etEmail.apply {
                            setText("")
                            requestFocus()
                        }
                        etPassword.setText("")
                        txtInputLayoutEmail.boxStrokeColor = fragment.resources.getColor(R.color.required)
                        txtInputLayoutPW.boxStrokeColor = fragment.resources.getColor(R.color.required)

                        Toast.makeText(
                            context,
                            "Login failed: ${task.exception.toString().substring(66, task.exception.toString().length)}",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e(TAG, "validateCredentials: ${task.exception.toString()}")
                    }
                }
        }
    }
}