package com.example.ccc_library_app.ui.dashboard.borrow_return

data class BorrowReturnDialogModel (
    val bookName: String,
    val userBorrower: String,
    val bookAuthor: String,
    val bookGenre: String,
    val bookStatus: String,
    val bookCode: String
)