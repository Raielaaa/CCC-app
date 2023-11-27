package com.example.ccc_library_app.ui.dashboard.home

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.ccc_library_app.R
import com.example.ccc_library_app.ui.dashboard.home.featured.CompleteFeaturedBookModel
import com.example.ccc_library_app.ui.dashboard.home.featured.FeaturedBookModel
import com.example.ccc_library_app.ui.dashboard.home.inventory.InventoryItemsDataModel
import com.example.ccc_library_app.ui.dashboard.home.inventory.InventorySeeAllBottomSheetFragment
import com.example.ccc_library_app.ui.dashboard.home.popular.FirebaseDataModel
import com.example.ccc_library_app.ui.dashboard.home.popular.PopularAdapter
import com.example.ccc_library_app.ui.dashboard.home.popular.PopularModel
import com.example.ccc_library_app.ui.dashboard.list.BookListItemModel
import com.example.ccc_library_app.ui.dashboard.util.Resources
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    @Named("FirebaseFireStore.Instance")
    val firebaseFireStore: FirebaseFirestore,
    @Named("FirebaseStorage.Instance")
    val firebaseStorage: StorageReference
) : ViewModel() {
    private var TAG: String = "MyTag"

    private lateinit var bookListPopularPermanent: ArrayList<BookListItemModel>
    private lateinit var bookListPopularTemp: ArrayList<FirebaseDataModel>
    private var bookListPopularFinal: ArrayList<PopularModel> = ArrayList()

    fun captureQR(activity: Activity) {
        com.example.ccc_library_app.ui.account.util.Resources.dismissDialog()
        //  Prompting the user for camera permission. Camera permission is used so that we can use
        //  the camera of the phone of the user
        cameraPermissions(activity)
    }

    private fun takeImage(activity: Activity) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            activity.startActivityForResult(intent, 1)
        } catch (e: Exception) {
            Toast.makeText(activity, e.localizedMessage, Toast.LENGTH_SHORT).show()
            Log.d(TAG, "takeImage: ${e.message}")
        }
    }

    private fun cameraPermissions(activity: Activity) {
        val cameraPermission = android.Manifest.permission.CAMERA
        val permissionGranted = PackageManager.PERMISSION_GRANTED

        if (ContextCompat.checkSelfPermission(activity, cameraPermission) != permissionGranted) {
            // Request camera permission if it's not granted.
            ActivityCompat.requestPermissions(activity, arrayOf(cameraPermission), 111)
        } else {
            // Camera permission is already granted, proceed to capturing the image.
            takeImage(activity)
        }
    }

    fun navigateToBookList(hostFragment: Fragment, ivBookList: ImageView) {
        Resources.navigate(hostFragment, ivBookList, R.id.action_homeFragment_to_bookListFragment)
    }

    fun navigateToBookmark(hostFragment: Fragment, ivBookmark: ImageView) {
        Resources.navigate(hostFragment, ivBookmark, R.id.action_homeFragment_to_bookmarkFragment)
    }

    fun navigateToSettings(hostFragment: Fragment, ivSettings: ImageView) {
        Resources.navigate(hostFragment, ivSettings, R.id.action_homeFragment_to_settingsFragment)
    }

    fun initPopularRecyclerView(
        recyclerView: RecyclerView,
        activity: Activity,
        hostFragment: Fragment
    ) {
        try {
            com.example.ccc_library_app.ui.account.util.Resources.displayCustomDialog(
                activity,
                R.layout.custom_dialog_loading
            )

            bookListPopularTemp = ArrayList()
            bookListPopularPermanent = ArrayList()

            firebaseFireStore.collection("ccc-library-app-book-info")
                .get()
                .addOnSuccessListener { bookInfoListFromDB ->
                    if (bookInfoListFromDB.isEmpty) {
                        Toast.makeText(
                            activity,
                            "Popular books unavailable",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        val genreHolder: ArrayList<String> = ArrayList()

                        for (bookInfo in bookInfoListFromDB) {
                            bookListPopularTemp.add(
                                FirebaseDataModel(
                                    bookInfo.get("modelBookTitle").toString(),
                                    bookInfo.get("modelBookImage").toString(),
                                )
                            )
                            genreHolder.add(bookInfo.get("modelBookGenre").toString())
                        }

                        var itemsProcessed = 0
                        bookListPopularTemp.forEachIndexed { index, bookData ->
                            displayPopularRV(bookData, recyclerView, activity, bookData.modelBookImage, genreHolder[index] ) {
                                itemsProcessed++
                                if (itemsProcessed == bookListPopularTemp.size) {
                                    displayInfoToRecyclerView(recyclerView, activity, bookListPopularFinal, hostFragment)

                                    Resources.setPermanentDataForSearch(bookListPopularPermanent)
                                }
                            }
                        }
                    }
                }
        } catch (exception: Exception) {
            Toast.makeText(
                activity,
                "An error occurred: ${exception.localizedMessage}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun displayInfoToRecyclerView(recyclerView: RecyclerView, activity: Activity, bookListPopularFinal: ArrayList<PopularModel>, hostFragment: Fragment) {
        val adapter = PopularAdapter { clickedItemInfo ->
            clickedFunction(clickedItemInfo, hostFragment)
        }
        recyclerView.adapter = adapter
        adapter.setList(bookListPopularFinal)

        com.example.ccc_library_app.ui.account.util.Resources.dismissDialog()
    }

    private fun clickedFunction(clickedItemInfo: PopularModel, hostFragment: Fragment) {
        hostFragment.findNavController().navigate(R.id.action_homeFragment_to_clickedBookFragment, bundleOf("bookTitleKey" to clickedItemInfo.bookTitle))
    }

    private fun displayPopularRV(
        data: FirebaseDataModel,
        recyclerView: RecyclerView,
        activity: Activity,
        bookCode: String,
        modelBookGenre: String,
        completion: (Boolean) -> Unit
    ) {
        getImage(data.modelBookImage, activity) { bitmapImage ->
            if (bitmapImage == null) {
                showToastMessage(activity, "An error occurred displaying the books")
                completion(false) // Signal failure
            } else {
                bookListPopularFinal.add(
                    PopularModel(
                        bitmapToUri(activity, bitmapImage, bookCode)!!,
                        data.modelBookTitle
                    )
                )
                completion(true)
                bookListPopularPermanent.add(
                    BookListItemModel(
                        bitmapToUri(activity, bitmapImage, bookCode)!!,
                        data.modelBookTitle,
                        "Genre: $modelBookGenre"
                    )
                )
            }
        }
    }

    private fun getImage(filePath: String, activity: Activity, callback: (Bitmap?) -> Unit) {
        firebaseStorage.child(filePath).getBytes(1_048_576L)
            .addOnSuccessListener { data ->
                // Convert the byte array to a Bitmap
                val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                callback(bitmap)
            }.addOnFailureListener { exception ->
                // Indicate failure by passing null to the callback
                callback(null)

                // Handle the failure, e.g., show an error message or log the exception
                Log.e("MyTag", "Error downloading image: ${exception.message}", exception)
                showToastMessage(
                    activity,
                    "Error downloading image: ${exception.localizedMessage}"
                )
            }
    }

    private fun bitmapToUri(activity: Activity, bitmap: Bitmap, bookNumber: String): Uri? {
        // Get the directory for storing the image
        val imagesDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val formattedFileName = bookNumber.split("/")
        val imageFile = File(imagesDir, "${formattedFileName[1]}.png")

        try {
            // Create a FileOutputStream to write the bitmap to the file
            val fos = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.close()

            // Create a Uri from the file
            return Uri.fromFile(imageFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    private fun showToastMessage(activity: Activity, message: String) {
        Toast.makeText(
            activity,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    fun initFeaturedBook(
        ivFeaturedImage: ImageView,
        tvFeaturedTitle: TextView,
        tvFeaturedDescription: TextView,
        activity: Activity
    ) {
        var tempHighestFeaturedBookModel: CompleteFeaturedBookModel? = null

        firebaseFireStore.collection("ccc-library-app-book-data")
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    var count = 0
                    for (document in querySnapshot) {
                        getBookCompleteInfo(document, activity) { tempCompleteFeaturedBookHolder ->
                            count++
                            if (tempCompleteFeaturedBookHolder != null) {
                                if (tempHighestFeaturedBookModel == null) {
                                    tempHighestFeaturedBookModel = tempCompleteFeaturedBookHolder
                                } else {
                                    if (document.get("modelBookCode").toString().toInt() > tempHighestFeaturedBookModel!!.count.toInt()) {
                                        tempHighestFeaturedBookModel = tempCompleteFeaturedBookHolder
                                    }
                                }
                            }

                            // Check if all callbacks are received
                            if (count == querySnapshot.size()) {
                                // All callbacks received, proceed with the logic
                                ivFeaturedImage.setImageURI(tempHighestFeaturedBookModel!!.image)
                                tvFeaturedTitle.text = tempHighestFeaturedBookModel!!.featuredTitle
                                tvFeaturedDescription.text = tempHighestFeaturedBookModel!!.featuredDescription
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "initFeaturedBook: empty")
                }
            }
    }

    private fun getBookCompleteInfo(
        document: QueryDocumentSnapshot?,
        activity: Activity,
        callback: (CompleteFeaturedBookModel?) -> Unit
    ) {
        var completeFeaturedBookModelTemp: CompleteFeaturedBookModel?

        firebaseFireStore.collection("ccc-library-app-book-info")
            .document(document!!.get("modelBookCode").toString())
            .get()
            .addOnSuccessListener { documentSnapshot ->
                var imageBitmap: Bitmap? = null
                getImage(documentSnapshot.data!!["modelBookImage"].toString(), activity) { bitmapImage ->
                    if (bitmapImage == null) {
                        showToastMessage(activity, "An error occurred displaying the books")
                    } else {
                        imageBitmap = bitmapImage
                    }

                    completeFeaturedBookModelTemp = CompleteFeaturedBookModel(
                        bitmapToUri(activity, imageBitmap!!, "book_images/${documentSnapshot.data!!["modelBookCode"]}"
                        )!!,
                        documentSnapshot.data!!["modelBookTitle"].toString(),
                        documentSnapshot.data!!["modelBookDescription"].toString(),
                        document.get("modelBookCount").toString()
                    )

                    // Pass the result to the callback
                    callback(completeFeaturedBookModelTemp)
                }
            }
    }

    fun initFeaturedClickedFunction(
        hostFragment: Fragment,
        bookTitle: String
    ) {
        hostFragment.findNavController().navigate(R.id.action_homeFragment_to_clickedBookFragment, bundleOf("bookTitleKey" to bookTitle))
    }

    fun displayTally(tvInventoryCurrent: TextView, tvInventoryBorrowed: TextView) {
        var bookNumberCount: Int = 0
        var bookBorrowedCount: Int = 0

        // Create a list of tasks
        val tasks = mutableListOf<Task<QuerySnapshot>>()

        // Add the first task
        tasks.add(firebaseFireStore.collection("ccc-library-app-book-info").get())

        // Add the second task
        tasks.add(firebaseFireStore.collection("ccc-library-app-borrow-data").get())

        // Create a combined task to wait for all tasks to complete
        val combinedTask = Tasks.whenAllSuccess<QuerySnapshot>(tasks)

        // Add a listener to the combined task
        combinedTask.addOnSuccessListener { results ->
            // Process the results of the individual tasks
            val bookInfoSnapshot = results[0] as QuerySnapshot
            val borrowDataSnapshot = results[1] as QuerySnapshot

            bookNumberCount = bookInfoSnapshot.size()
            bookBorrowedCount = borrowDataSnapshot.size()

            // Update the TextViews
            tvInventoryCurrent.text = (bookNumberCount - bookBorrowedCount).toString()
            tvInventoryBorrowed.text = bookBorrowedCount.toString()
        }

        // Handle errors
        combinedTask.addOnFailureListener { exception ->
            // Handle the failure
            Log.e("Firebase", "Error retrieving data", exception)
        }
    }

    fun initSeeAllBottomDialog(
        tvInventoryCurrent: TextView,
        ivInventoryCurrent: ImageView,
        tvInventoryBorrowed: TextView,
        ivInventoryBorrow: ImageView,
        hostFragment: Fragment,
        activity: Activity
    ) {
        tvInventoryBorrowed.setOnClickListener {
            bottomSheetSeeAllBorrow(hostFragment, activity)
        }
        ivInventoryBorrow.setOnClickListener {
            bottomSheetSeeAllBorrow(hostFragment, activity)
        }


        tvInventoryCurrent.setOnClickListener {
            bottomSheetSeeAllCurrent(hostFragment, activity)
        }
        ivInventoryCurrent.setOnClickListener {
            bottomSheetSeeAllCurrent(hostFragment, activity)
        }
    }

    private fun bottomSheetSeeAllCurrent(hostFragment: Fragment, activity: Activity) {
        com.example.ccc_library_app.ui.account.util.Resources.displayCustomDialog(
            activity,
            R.layout.custom_dialog_loading
        )

        try {
            val task = mutableListOf<Task<QuerySnapshot>>()
            task.add(firebaseFireStore.collection("ccc-library-app-book-info").get())
            task.add(firebaseFireStore.collection("ccc-library-app-borrow-data").get())

            val returnedTask = Tasks.whenAllSuccess<QuerySnapshot>(task)
            returnedTask.addOnSuccessListener { result ->
                val resultCurrent = result[0] as QuerySnapshot
                val resultBorrow = result[1] as QuerySnapshot

                if (!resultCurrent.isEmpty) {
                    val listOfBookInfo: ArrayList<InventoryItemsDataModel> = ArrayList()
                    val listOfBorrowBooksTitle: ArrayList<String> = ArrayList()

                    for (document in resultBorrow.documents) {
                        listOfBorrowBooksTitle.add(document.get("modelBookName").toString())
                    }

                    parentLoopCurrent@ for (document in resultCurrent.documents) {
                        var imageUriSource: Uri? = null

                        val imageListSource = bookListPopularFinal
                        childLoopCurrent@ for (data in imageListSource) {
                            if (data.bookTitle == document.get("modelBookTitle")) {
                                imageUriSource = data.uriImage
                                break@childLoopCurrent
                            }
                        }

                        //  Exclude the book if it is present on the list of Borrowed books
                        if (!listOfBorrowBooksTitle.contains(document.get("modelBookTitle").toString())) {
                            listOfBookInfo.add(
                                InventoryItemsDataModel(
                                    imageUriSource!!,
                                    document.get("modelBookTitle").toString(),
                                    document.get("modelBookAuthor").toString(),
                                    document.get("modelBookGenre").toString()
                                )
                            )
                        }
                    }
                    InventorySeeAllBottomSheetFragment(listOfBookInfo)
                        .show(hostFragment.parentFragmentManager, "SeeAll_BottomSheetFragment")
                    com.example.ccc_library_app.ui.account.util.Resources.dismissDialog()
                } else {
                    showToastMessage(activity, "Nothing to show. List of available books is empty.")
                    com.example.ccc_library_app.ui.account.util.Resources.dismissDialog()
                }
            }
        } catch (err: Exception) {
            showToastMessage(activity, "An error occurred: ${err.localizedMessage}")
            Log.e(TAG, "bottomSheetSeeAllCurrent: ${err.message}", )
            com.example.ccc_library_app.ui.account.util.Resources.dismissDialog()
        }
    }

    private fun bottomSheetSeeAllBorrow(hostFragment: Fragment, activity: Activity) {
        com.example.ccc_library_app.ui.account.util.Resources.displayCustomDialog(
            activity,
            R.layout.custom_dialog_loading
        )

        try {
            val task = mutableListOf<Task<QuerySnapshot>>()
            task.add(firebaseFireStore.collection("ccc-library-app-borrow-data").get())

            val returnedTask = Tasks.whenAllSuccess<QuerySnapshot>(task)
            returnedTask.addOnSuccessListener { result ->
                if (result.isNotEmpty()) {
                    val listOfBookInfo: ArrayList<InventoryItemsDataModel> = ArrayList()

                    for (items in result) {
                        for (item in items) {
                            var imageUriSource: Uri? = null

                            val imageListSource = bookListPopularFinal
                            for (data in imageListSource) {
                                if (data.bookTitle == item.get("modelBookName")) {
                                    imageUriSource = data.uriImage
                                }
                            }

                            listOfBookInfo.add(
                                InventoryItemsDataModel(
                                    imageUriSource!!,
                                    item.get("modelBookName").toString(),
                                    item.get("modelBookAuthor").toString(),
                                    item.get("modelBookGenre").toString()
                                )
                            )
                        }
                    }
                    InventorySeeAllBottomSheetFragment(listOfBookInfo)
                        .show(hostFragment.parentFragmentManager, "SeeAll_BottomSheetFragment")
                    com.example.ccc_library_app.ui.account.util.Resources.dismissDialog()
                } else {
                    showToastMessage(activity, "Nothing to show. Borrow list is empty.")
                    com.example.ccc_library_app.ui.account.util.Resources.dismissDialog()
                }
            }

        } catch (err: Exception) {
            showToastMessage(activity, "An error occurred: ${err.localizedMessage}")
            Log.e(TAG, "bottomSheetSeeAllBorrow: ${err.message}", )
            com.example.ccc_library_app.ui.account.util.Resources.dismissDialog()
        }
    }
}