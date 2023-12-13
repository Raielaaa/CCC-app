package com.example.ccc_library_app.ui.dashboard.list.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ccc_library_app.R
import com.example.ccc_library_app.ui.account.util.Resources
import com.example.ccc_library_app.ui.dashboard.list.BookListAdapter
import com.example.ccc_library_app.ui.dashboard.list.BookListItemDecoration
import com.example.ccc_library_app.ui.dashboard.list.BookListItemModel
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class BookListViewModel @Inject constructor(
    @Named("FirebaseStorage.Instance")
    val firebaseStorage: StorageReference
) : ViewModel() {
    //  General
    private val TAG: String = "MyTag"

    //  Recycler view
    private lateinit var adapterForRV: BookListAdapter
    @SuppressLint("StaticFieldLeak")
    private lateinit var rvMainVM: RecyclerView

    //  Temp list
    private val tempListForSearch: ArrayList<BookListItemModel> = ArrayList()
    private val listForAll: ArrayList<BookListItemModel> = ArrayList()
    private val listForAcc: ArrayList<BookListItemModel> = ArrayList()
    private val listForLit: ArrayList<BookListItemModel> = ArrayList()
    private val listForSocial: ArrayList<BookListItemModel> = ArrayList()
    private val listForScience: ArrayList<BookListItemModel> = ArrayList()
    private val listForTech: ArrayList<BookListItemModel> = ArrayList()

    fun navigateHome(hostFragment: Fragment, ivHome: ImageView) {
        com.example.ccc_library_app.ui.dashboard.util.Resources.navigate(hostFragment, ivHome, R.id.action_bookListFragment_to_homeFragment
        )
    }

    fun navigateBookmark(hostFragment: Fragment, ivBookmark: ImageView) {
        com.example.ccc_library_app.ui.dashboard.util.Resources.navigate(hostFragment, ivBookmark, R.id.action_bookListFragment_to_bookmarkFragment
        )
    }

    fun navigateSettings(hostFragment: Fragment, ivSettings: ImageView) {
        com.example.ccc_library_app.ui.dashboard.util.Resources.navigate(hostFragment, ivSettings, R.id.action_bookListFragment_to_settingsFragment
        )
    }

    fun setUpRecyclerView(
        activity: Activity,
        rvMain: RecyclerView,
        hostFragment: Fragment,
        etBookListSearch: EditText,
        tvGenreBookList: TextView
    ) {
        displayInfoToRecyclerView(rvMain, activity, hostFragment, com.example.ccc_library_app.ui.dashboard.util.Resources.getPermanentDataForSearch())
        setUpSearch(
            etBookListSearch,
            tvGenreBookList,
            com.example.ccc_library_app.ui.dashboard.util.Resources.getPermanentDataForSearch()
        )
    }

    private fun displayInfoToRecyclerView(rvMain: RecyclerView, activity: Activity, hostFragment: Fragment, bookList: ArrayList<BookListItemModel>) {
        adapterForRV = BookListAdapter(bookList) { bookData ->
            navigateFragment(bookData, hostFragment)
        }
        rvMainVM = rvMain

        rvMainVM.apply {
            layoutManager = GridLayoutManager(activity, 2)
            adapter = adapterForRV
            addItemDecoration(
                BookListItemDecoration(
                    2,
                    50,
                    true
                )
            )
        }
    }

    private fun navigateFragment(bookData: BookListItemModel, hostFragment: Fragment) {
        Resources.displayCustomDialog(
            hostFragment.requireActivity(),
            R.layout.custom_dialog_loading
        )
        hostFragment.findNavController().navigate(R.id.action_bookListFragment_to_clickedBookFragment, bundleOf("bookTitleKey" to bookData.tvBookTitle))
    }

    fun addImagesToCloudTBD(
        bitmap: Bitmap,
        fileName: String
    ) : UploadTask {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        return firebaseStorage.child(fileName).putBytes(data)
    }

    fun initSelectedGenre(
        ivAll: CardView,
        ivAcc: CardView,
        ivLit: CardView,
        ivSocial: CardView,
        ivScience: CardView,
        ivTech: CardView,
        hostFragment: Fragment,
        tvGenreBookList: TextView,
        activity: Activity
    ) {
        for (data in com.example.ccc_library_app.ui.dashboard.util.Resources.getPermanentDataForSearch()) {
            when (data.tvBookGenre) {
                "Genre: Accounting" -> listForAcc.add(data)
                "Genre: Literature" -> listForLit.add(data)
                "Genre: Social science" -> listForSocial.add(data)
                "Genre: Science" -> listForScience.add(data)
                "Genre: Technology" -> listForTech.add(data)
                else -> listForAll.add(data)
            }
        }

        ivAll.setOnClickListener {
            initSelectedGenreHelper(
                tvGenreBookList,
                hostFragment,
                activity,
                R.string.main_bookList_all_code,
                com.example.ccc_library_app.ui.dashboard.util.Resources.getPermanentDataForSearch()
            )
        }
        ivAcc.setOnClickListener {
            initSelectedGenreHelper(
                tvGenreBookList,
                hostFragment,
                activity,
                R.string.main_bookList_accounting_code,
                listForAcc
            )
        }
        ivLit.setOnClickListener {
            initSelectedGenreHelper(
                tvGenreBookList,
                hostFragment,
                activity,
                R.string.main_bookList_literature_code,
                listForLit
            )
        }
        ivSocial.setOnClickListener {
            initSelectedGenreHelper(
                tvGenreBookList,
                hostFragment,
                activity,
                R.string.main_bookList_social_code,
                listForSocial
            )
        }
        ivScience.setOnClickListener {
            initSelectedGenreHelper(
                tvGenreBookList,
                hostFragment,
                activity,
                R.string.main_bookList_science_code,
                listForScience
            )
        }
        ivTech.setOnClickListener {
            initSelectedGenreHelper(
                tvGenreBookList,
                hostFragment,
                activity,
                R.string.main_bookList_tech_code,
                listForTech
            )
        }
    }

    private fun initSelectedGenreHelper(
        tvGenre: TextView,
        hostFragment: Fragment,
        activity: Activity,
        genreCode: Int,
        listItem: ArrayList<BookListItemModel>
    ) {
        tvGenre.text = activity.getString(genreCode)

        adapterForRV = BookListAdapter(listItem) { bookData ->
            navigateFragment(bookData, hostFragment)
        }
        rvMainVM.adapter = adapterForRV
        adapterForRV.notifyDataSetChanged()
    }

    private fun setUpSearch(etBookListSearch: EditText, tvGenreBookList: TextView, listOfBookListItemModelFS: ArrayList<BookListItemModel>) {
        val filteredList: ArrayList<BookListItemModel> = ArrayList()

        etBookListSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(input: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val stringInput = input.toString().lowercase().trim()

                when (tvGenreBookList.text.toString()) {
                    "All books" -> {
                        filterFunction(
                            stringInput,
                            filteredList,
                            listOfBookListItemModelFS,
                            "All"
                        )
                    }
                    "Accounting books" -> {
                        filterFunction(
                            stringInput,
                            filteredList,
                            listForAcc,
                            "Accounting"
                        )
                    }
                    "Literature books" -> {
                        filterFunction(
                            stringInput,
                            filteredList,
                            listForLit,
                            "Literature"
                        )
                    }
                    "Social science books" -> {
                        filterFunction(
                            stringInput,
                            filteredList,
                            listForSocial,
                            "Social science"
                        )
                    }
                    "Science books" -> {
                        filterFunction(
                            stringInput,
                            filteredList,
                            listForScience,
                            "Science"
                        )
                    }
                    "Technology books" -> {
                        filterFunction(
                            stringInput,
                            filteredList,
                            listForTech,
                            "Technology"
                        )
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) { }

        })
    }

    fun filterFunction(
        stringInput: String,
        filteredList: ArrayList<BookListItemModel>,
        listOfBookListItemModelFS: ArrayList<BookListItemModel>,
        genre: String
    ) {
        if (stringInput.isNotEmpty()) {
            filteredList.clear()

            for (model in listOfBookListItemModelFS) {
                if (model.tvBookTitle.lowercase().trim().contains(stringInput) ||
                    model.tvBookGenre.lowercase().trim().contains(stringInput))
                    filteredList.add(model)
            }

            adapterForRV.updateData(filteredList)
        } else if (stringInput.isEmpty() || stringInput == "") {
            when (genre) {
                "All" -> adapterForRV.updateData(com.example.ccc_library_app.ui.dashboard.util.Resources.getPermanentDataForSearch())
                "Accounting" -> bookListResetFromSearch("Genre: Accounting")
                "Literature" -> bookListResetFromSearch("Genre: Literature")
                "Social science" -> bookListResetFromSearch("Genre: Social science")
                "Science" -> bookListResetFromSearch("Genre: Science")
                "Technology" -> bookListResetFromSearch("Genre: Technology")
            }
        }
    }

    private fun bookListResetFromSearch(genre: String) {
        tempListForSearch.clear()
        for (data in com.example.ccc_library_app.ui.dashboard.util.Resources.getPermanentDataForSearch()) {
            if (data.tvBookGenre == genre) {
                tempListForSearch.add(data)
                Log.d(TAG, "filterFunction: if-counter")
            }
        }
        adapterForRV.updateData(tempListForSearch)
    }

    fun visitWebsite(requireActivity: Activity) {
        val url = "https://www.getfreeebooks.com/"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

        try {
            requireActivity.startActivity(intent)
        } catch (err: Exception) {
            Toast.makeText(requireActivity, "An error occurred: ${err.localizedMessage}", Toast.LENGTH_LONG).show()
            Log.e(TAG, "visitWebsite: ${err.message}", )
        }
    }
}