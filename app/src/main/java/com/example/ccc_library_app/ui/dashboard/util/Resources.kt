package com.example.ccc_library_app.ui.dashboard.util

import android.util.Log
import android.widget.ImageView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ccc_library_app.R
import com.example.ccc_library_app.ui.dashboard.list.BookListItemModel
import java.lang.reflect.Array

object Resources {
    fun navigate(hostFragment: Fragment, clickedIV: ImageView, destination: Int) {
        clickedIV.setOnClickListener {
            hostFragment.findNavController().navigate(destination)
        }
    }

    private var permanentDataForSearch: ArrayList<BookListItemModel> = ArrayList()

    fun setPermanentDataForSearch(data: ArrayList<BookListItemModel>) {
        this.permanentDataForSearch = data
    }

    fun getPermanentDataForSearch(): ArrayList<BookListItemModel> {
        return ArrayList(permanentDataForSearch)
    }

    private var drawerLayout: DrawerLayout? = null

    fun setDrawerLayoutRef(drawerLayout: DrawerLayout?) {
        this.drawerLayout = drawerLayout
    }

    fun getDrawerLayoutRef() : DrawerLayout? = this.drawerLayout
}