package com.example.ccc_library_app.ui.dashboard.borrow_return

data class PersistentBorrowList (
    val bookCode: String,
    val bookName: String,
    val bookAuthor: String,
    val bookGenre: String,
    val borrowerName: String,
    val borrowerEmail: String
)