package com.example.ccc_library_app.ui.dashboard.bookmark

import android.util.Log
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.ccc_library_app.R
import com.example.ccc_library_app.ui.dashboard.util.Resources
import dagger.hilt.android.lifecycle.HiltViewModel

class BookmarkViewModel : ViewModel() {
    fun navigateHome(hostFragment: Fragment, ivHome: ImageView) {
        Resources.navigate(hostFragment, ivHome, R.id.action_bookmarkFragment_to_homeFragment)
    }

    fun navigateBookList(hostFragment: Fragment, ivBookList: ImageView) {
        Resources.navigate(hostFragment, ivBookList, R.id.action_bookmarkFragment_to_bookListFragment)
    }

    fun navigateSettings(hostFragment: Fragment, ivSettings: ImageView) {
        Resources.navigate(hostFragment, ivSettings, R.id.action_bookmarkFragment_to_settingsFragment)
    }
}