package com.example.ccc_library_app.ui.dashboard.settings

import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.ccc_library_app.R
import com.example.ccc_library_app.ui.dashboard.util.Resources

class SettingsViewModel : ViewModel() {
    fun navigateToSettings(hostFragment: Fragment, ivHome: ImageView) {
        Resources.navigate(hostFragment, ivHome, R.id.action_settingsFragment_to_homeFragment)
    }

    fun navigateToBookList(hostFragment: Fragment, ivBookList: ImageView) {
        Resources.navigate(hostFragment, ivBookList, R.id.action_settingsFragment_to_bookListFragmentFragment)
    }

    fun navigateToBookmark(hostFragment: Fragment, ivBookmark: ImageView) {
        Resources.navigate(hostFragment, ivBookmark, R.id.action_settingsFragment_to_bookmarkFragment)
    }
}