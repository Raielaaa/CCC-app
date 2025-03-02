package com.example.ccc_library_app.ui.account.main

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.example.ccc_library_app.R
import com.example.ccc_library_app.databinding.ActivityMainBinding
import com.example.ccc_library_app.ui.account.register.DataModelGoogle
import com.example.ccc_library_app.ui.account.util.Resources
import com.example.ccc_library_app.ui.dashboard.home.main.HomeFragment
import com.example.ccc_library_app.ui.dashboard.util.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

@Suppress("DEPRECATION")
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

    // For QR code
    private var imageBitmap: Bitmap? = null
    private val CAMERA_PERMISSION_CODE = 1

    //  Permission code
    private val EXACT_ALARM_PERMISSION_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("GoogleSignInSP", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        //  Disable dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_account) as NavHostFragment
        navController = navHostFragment.navController

        com.example.ccc_library_app.ui.dashboard.util.Resources.setDrawerLayoutRef(binding.drawerLayout)
        initNavDrawerClickEvents()

        //  Notification configurations
        createNotificationChannel()

        //  Checks if there's an active internet connection
        if (!checkNetworkAvailability()) {
            Resources.displayCustomDialogForNoInternet(
                this@MainActivity,
                R.layout.custom_dialog_no_internet
            )
        }

        //  Disabling drawer layout automatic opening (swiping from left edge going to right)
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestExactAlarmPermission()
        }
    }

    private fun checkNetworkAvailability(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

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

    private fun checkAndRequestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val permission = Manifest.permission.SCHEDULE_EXACT_ALARM
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // Permission not granted, request it
                ActivityCompat.requestPermissions(this, arrayOf(permission), EXACT_ALARM_PERMISSION_CODE)
            }
        }
    }

    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(Constants.channelID, Constants.notificationName, importance)
        channel.description = Constants.notificationDescription
        val notificationManager: NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

//    private fun getDisplayNameFromFirebase(callback: (String?) -> Unit) {
//        try {
//            val uID = auth.currentUser!!.uid
//
//            fireStore.collection("ccc-library-app-user-data").document(uID)
//                .get()
//                .addOnSuccessListener { documentSnapshot ->
//                    val displayNameFromFirebase = documentSnapshot.getString("modelUsername")
//                    callback(displayNameFromFirebase)
//                }
//                .addOnFailureListener { exception ->
//                    // Handle the failure (e.g., log an error)
//                    callback(null)
//                }
//        } catch (ignored: Exception) { }
//    }
//
//    private fun getEmailFromAuth(): String = auth.currentUser!!.email.toString()

    private fun initNavDrawerClickEvents() {
        Resources.navDrawer = binding.navDrawer

        binding.apply {
            navDrawer.setCheckedItem(R.id.drawer_home)
            navDrawer.setNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.drawer_home -> {
                        navigate(R.id.homeFragment)
                        navDrawer.setCheckedItem(R.id.drawer_home)
                    }
                    R.id.drawer_book_list -> {
                        navigate(R.id.bookListFragment)
                        navDrawer.setCheckedItem(R.id.drawer_book_list)
                    }
                    R.id.drawer_bookmark -> {
                        navigate(R.id.bookmarkFragment)
                        navDrawer.setCheckedItem(R.id.drawer_bookmark)
                    }
                    R.id.drawer_settings -> {
                        navigate(R.id.settingsFragment)
                        navDrawer.setCheckedItem(R.id.drawer_settings)
                    }
                }
                this.drawerLayout.closeDrawer(GravityCompat.START)
                true
            }
        }
    }

    private fun navigate(fragment: Int) {
        binding.apply {
            drawerLayout.closeDrawer(GravityCompat.START)
            CoroutineScope(Dispatchers.IO).launch {
                delay(500)
                withContext(Dispatchers.Main) {
                    navController.navigate(fragment, null, getCustomNavOptions(R.anim.fade_in, R.anim.fade_out))
                }
            }
        }
    }

    private fun getCustomNavOptions(enterAnim: Int, exitAnim: Int): NavOptions {
        return NavOptions.Builder()
            .setEnterAnim(enterAnim)
            .setExitAnim(exitAnim)
            .build()
    }

    // Use this method to navigate to a destination
    private fun navigateTo(destinationId: Int, args: Bundle? = null) {
        navController.navigate(destinationId, args)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            Resources.displayCustomDialog(
                activity = this@MainActivity,
                layoutDialog = R.layout.custom_dialog_loading
            )

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Resources.dismissDialog()
                Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        if (requestCode == CAMERA_PERMISSION_CODE) {
            val extras: Bundle? = data?.extras
            try {
                imageBitmap = extras?.get("data") as Bitmap

                displayBorrowReturnDialog(imageBitmap!!)
            } catch (exception: Exception) {
                Toast.makeText(this@MainActivity, "An error occurred: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
                return
            }
        }
    }

    private fun displayBorrowReturnDialog(imageBitmap: Bitmap) {
        Resources.scanBitmapQR(
            imageBitmap,
            this@MainActivity,
            fireStore,
            auth
        )
//        Resources.displayCustomDialogForQr(
//            HomeFragment(),
//            this@MainActivity,
//            R.layout.custom_dialog_qr,
//            imageBitmap!!,
//            fireStore,
//            auth
//        )
    }

    private fun saveBitmapToInternalStorage(bitmap: Bitmap): String? {
        val contextWrapper = ContextWrapper(this@MainActivity)
        val directory = contextWrapper.getDir("imageDir", MODE_PRIVATE)
        val file = File(directory, "ImageQRScan")

        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            Log.e(TAG, "saveBitmapToInternalStorage: ${e.printStackTrace()}")
        } finally {
            try {
                fos?.close()
            } catch (e: IOException) {
                Log.e(TAG, "saveBitmapToInternalStorage: ${e.printStackTrace()}")
            }
        }
        return file.absolutePath
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

        fireStore.collection("ccc-library-app-user-data").document(userID)
            .set(userData)
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