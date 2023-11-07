package com.example.ccc_library_app.ui.dashboard.list

import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.example.ccc_library_app.R
import com.example.ccc_library_app.ui.dashboard.util.Resources

class BookListViewModel : ViewModel() {
    fun navigateHome(hostFragment: Fragment, ivHome: ImageView) {
        Resources.navigate(hostFragment, ivHome, R.id.action_bookListFragment_to_homeFragment
        )
    }

    fun navigateBookmark(hostFragment: Fragment, ivBookmark: ImageView) {
        Resources.navigate(hostFragment, ivBookmark, R.id.action_bookListFragment_to_bookmarkFragment
        )
    }

    fun navigateSettings(hostFragment: Fragment, ivSettings: ImageView) {
        Resources.navigate(hostFragment, ivSettings, R.id.action_bookListFragment_to_settingsFragment
        )
    }
}