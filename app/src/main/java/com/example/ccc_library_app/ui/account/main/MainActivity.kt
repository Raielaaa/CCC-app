package com.example.ccc_library_app.ui.account.main

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.ccc_library_app.R
import com.example.ccc_library_app.databinding.ActivityMainBinding
import com.example.ccc_library_app.ui.account.register.DataModelGoogle
import com.example.ccc_library_app.ui.account.util.Resources
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    @Named("FirebaseAuth.Instance")
    lateinit var auth: FirebaseAuth

    @Inject
    @Named("FirebaseFireStore.Instance")
    lateinit var fireStore: FirebaseFirestore

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: Editor
    private val TAG = "MyTag"
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("GoogleSignInSP", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_account) as NavHostFragment
        navController = navHostFragment.navController
    }

    // Use this method to navigate to a destination
    fun navigateTo(destinationId: Int, args: Bundle? = null) {
        navController.navigate(destinationId, args)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Resources.displayCustomDialog(
            activity = this@MainActivity,
            layoutDialog = R.layout.custom_dialog_loading
        )
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Resources.dismissDialog()
                Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userID = task.result.user!!.uid
                    insertGoogleDataToFIreStore(
                        Resources.getGoogleSignInData(),
                        userID
                    )
                    
                    val user = auth.currentUser
                    Toast.makeText(this, "Signed in as ${user?.displayName}", Toast.LENGTH_SHORT).show()
                } else {
                    Resources.dismissDialog()
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun insertGoogleDataToFIreStore(googleSignInData: List<String>, userID: String) {

        val userData = DataModelGoogle(
            googleSignInData[0],
            googleSignInData[1],
            googleSignInData[2],
            googleSignInData[3],
            googleSignInData[4],
            googleSignInData[5],
            userID
        )

        fireStore.collection("ccc-library-app-user-data")
            .add(userData)
            .addOnSuccessListener {
                Resources.dismissDialog()
                navController.navigate(R.id.homeFragment)
                Toast.makeText(this@MainActivity, "Google sign-in successful", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { exception ->
                Resources.dismissDialog()
                Toast.makeText(this@MainActivity, exception.localizedMessage, Toast.LENGTH_LONG).show()
                Log.e(TAG, "insertDataToFirebaseAuth-FireStoreException: ${exception.message}", )
            }
    }
}