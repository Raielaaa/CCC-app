package com.example.ccc_library_app.ui.dashboard.user

import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.ccc_library_app.R
import com.example.ccc_library_app.ui.dashboard.util.Resources

class UserViewModel : ViewModel() {
    fun navigateHome(hostFragment: Fragment, ivHome: ImageView) {
        Resources.navigate(hostFragment, ivHome, R.id.action_userFragment_to_homeFragment
        )
    }

    fun navigateBookmark(hostFragment: Fragment, ivBookmark: ImageView) {
        Resources.navigate(hostFragment, ivBookmark, R.id.action_userFragment_to_bookmarkFragment
        )
    }

    fun navigateSettings(hostFragment: Fragment, ivSettings: ImageView) {
        Resources.navigate(hostFragment, ivSettings, R.id.action_userFragment_to_settingsFragment
        )
    }
}