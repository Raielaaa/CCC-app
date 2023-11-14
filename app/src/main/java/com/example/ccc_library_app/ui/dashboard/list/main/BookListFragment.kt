package com.example.ccc_library_app.ui.dashboard.list.main

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ccc_library_app.R
import com.example.ccc_library_app.databinding.FragmentBookListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookListFragment : Fragment() {
    private lateinit var bookListViewModel: BookListViewModel
    private lateinit var binding: FragmentBookListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookListBinding.inflate(inflater, container, false)
        bookListViewModel = ViewModelProvider(this)[BookListViewModel::class.java]

        initBottomNavigationBar()
        initRecyclerView()
        highlightSelectedGenre()
//        addBookImageToCloudTBD()

        return binding.root
    }

    private fun highlightSelectedGenre() {
        binding.apply {
            bookListViewModel.initSelectedGenre(
                ivAll,
                ivAcc,
                ivLit,
                ivSocial,
                ivScience,
                ivTech,
                this@BookListFragment,
                tvGenreBookList,
                requireActivity()
            )
        }
    }

    data class ImageDetails(
        val bitmap: Bitmap,
        val fileName: String
    )

    private fun addBookImageToCloudTBD() {
        val listOfBitmapImage = listOf(
            ImageDetails(convertPNGImageResourceToBitmap(R.drawable.book_1), "book_images/539874.jpg"),
            ImageDetails(convertPNGImageResourceToBitmap(R.drawable.book_2), "book_images/372518.jpg"),
            ImageDetails(convertPNGImageResourceToBitmap(R.drawable.book_3), "book_images/785463.jpg"),
            ImageDetails(convertPNGImageResourceToBitmap(R.drawable.book_4), "book_images/954726.jpg"),
            ImageDetails(convertPNGImageResourceToBitmap(R.drawable.book_5), "book_images/839247.jpg"),
            ImageDetails(convertPNGImageResourceToBitmap(R.drawable.book_6), "book_images/468912.jpg"),
            ImageDetails(convertPNGImageResourceToBitmap(R.drawable.book_7), "book_images/621357.jpg"),
            ImageDetails(convertPNGImageResourceToBitmap(R.drawable.book_8), "book_images/526741.jpg"),
            ImageDetails(convertPNGImageResourceToBitmap(R.drawable.book_9), "book_images/874592.jpg"),
            ImageDetails(convertPNGImageResourceToBitmap(R.drawable.book_10), "book_images/183490.jpg")
        )

        for (images in listOfBitmapImage) {
            bookListViewModel.addImagesToCloudTBD(images.bitmap, images.fileName)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("MyTag", "addBookImageToCloudTBD: Upload successful")
                    } else {
                        Log.d("MyTag", "addBookImageToCloudTBD: Upload failed")
                    }
                }
        }
    }

    private fun convertPNGImageResourceToBitmap(imageResource: Int) : Bitmap = BitmapFactory.decodeResource(requireContext().resources, imageResource)

    private fun initRecyclerView() {
        bookListViewModel.setUpRecyclerView(
            requireActivity(),
            binding.rvMain,
            this@BookListFragment
        )
    }

    private fun initBottomNavigationBar() {
        binding.apply {
            bookListViewModel.apply {
                navigateHome(this@BookListFragment, ivHome)
                navigateBookmark(this@BookListFragment, ivBookmark)
                navigateSettings(this@BookListFragment, ivSettings)
            }
        }
    }
}