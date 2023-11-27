package com.example.ccc_library_app.ui.dashboard.bookmark

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.example.ccc_library_app.R
import com.example.ccc_library_app.databinding.FragmentBookmarkBinding
import com.example.ccc_library_app.ui.dashboard.util.Resources

class BookmarkFragment : Fragment() {
    private lateinit var binding: FragmentBookmarkBinding
    private lateinit var bookMarkViewModel: BookmarkViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        bookMarkViewModel = ViewModelProvider(this@BookmarkFragment)[BookmarkViewModel::class.java]

        initBottomNavigationBar()
        initNavigationDrawer()

        return binding.root
    }

    private fun initNavigationDrawer() {
        com.example.ccc_library_app.ui.account.util.Resources.navDrawer.setCheckedItem(R.id.drawer_bookmark)

        val drawerLayout: DrawerLayout? = Resources.getDrawerLayoutRef()

        binding.ivNavDrawer.setOnClickListener {
            // Toggle the drawer (open if closed, close if open)
            if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    private fun initBottomNavigationBar() {
        binding.apply {
            bookMarkViewModel.navigateHome(this@BookmarkFragment, ivHome)
            bookMarkViewModel.navigateBookList(this@BookmarkFragment, ivBookList)
            bookMarkViewModel.navigateSettings(this@BookmarkFragment, ivSettings)
        }
    }
}