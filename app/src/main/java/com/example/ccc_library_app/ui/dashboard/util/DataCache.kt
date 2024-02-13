package com.example.ccc_library_app.ui.dashboard.util

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object DataCache {
    var userImageProfile: Bitmap? = null
    val booksFullInfo = ArrayList<CompleteBookInfoModel>()

    var bookmarkPastDueCounter: Int = 0
    var bookmarkBorrowCount: Int = 0

    fun bitmapToUri(bitmap: Bitmap, activity: Activity): Uri {
        val filename = "labass-app-profile-pic.jpg" // Set a fixed filename
        val file = File(activity.externalCacheDir, filename)
        try {
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return Uri.fromFile(file)
    }
}