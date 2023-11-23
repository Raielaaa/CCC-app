package com.example.ccc_library_app.ui.dashboard.home.featured

import android.net.Uri

data class CompleteFeaturedBookModel(
    val image: Uri,
    val featuredTitle: String,
    val featuredDescription: String,
    val count: String
)