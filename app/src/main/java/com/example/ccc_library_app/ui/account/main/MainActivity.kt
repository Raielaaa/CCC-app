package com.example.ccc_library_app.ui.account.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.ccc_library_app.R
import com.example.ccc_library_app.databinding.ActivityMainBinding
import com.example.ccc_library_app.ui.account.util.Resources
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    @Named("FirebaseAuth.Instance")
    lateinit var auth: FirebaseAuth

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                    Resources.dismissDialog()
                    val user = auth.currentUser
                    Toast.makeText(this, "Signed in as ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    navController.navigate(R.id.homeFragment)
                } else {
                    Resources.dismissDialog()
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}