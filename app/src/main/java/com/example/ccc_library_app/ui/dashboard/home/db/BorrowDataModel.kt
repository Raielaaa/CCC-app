package com.example.ccc_library_app.ui.dashboard.home.db

data class BorrowDataModel(
    val userID: String,
    val modelBookCode: String,
    val modelBookName: String,
    val modelBookAuthor: String,
    val modelBookGenre: String,
    val modelBorrower: String,
    val modelProgram: String,
    val modelSection: String,
    val modelBorrowDate: String,
    val modelDeadline: String,
    val modelBorrowStatus: String
)
