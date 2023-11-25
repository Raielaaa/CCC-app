package com.example.ccc_library_app.ui.dashboard.home.inventory

import android.net.Uri

data class InventoryItemsDataModel(
    val bookImage: Uri,
    val bookTitle: String,
    val bookAuthor: String,
    val bookGenre: String
)
