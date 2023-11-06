package com.example.ccc_library_app.ui.dashboard.bookmark

import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.ccc_library_app.R
import com.example.ccc_library_app.ui.dashboard.util.Resources

class BookmarkViewModel : ViewModel() {
    fun navigateHome(hostFragment: Fragment, ivHome: ImageView) {
        Resources.navigate(hostFragment, ivHome, R.id.action_bookmarkFragment_to_homeFragment)
    }

    fun navigateUser(hostFragment: Fragment, ivUser: ImageView) {
        Resources.navigate(hostFragment, ivUser, R.id.action_bookmarkFragment_to_userFragment)
    }

    fun navigateSettings(hostFragment: Fragment, ivSettings: ImageView) {
        Resources.navigate(hostFragment, ivSettings, R.id.action_bookmarkFragment_to_settingsFragment)
    }
}