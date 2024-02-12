package com.example.ccc_library_app.ui.dashboard.list.selected

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.example.ccc_library_app.R
import com.example.ccc_library_app.databinding.FragmentClickedBookBinding
import com.example.ccc_library_app.ui.account.util.Resources
import com.example.ccc_library_app.ui.dashboard.home.main.HomeFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClickedBookFragment : Fragment() {
    private lateinit var clickedViewModel: ClickedBookViewModel
    private lateinit var binding: FragmentClickedBookBinding
    private val TAG: String = "MyTag"

    private lateinit var homeFragmentViewModel: HomeFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        clickedViewModel = ViewModelProvider(this)[ClickedBookViewModel::class.java]
        binding = FragmentClickedBookBinding.inflate(inflater, container, false)
        homeFragmentViewModel = ViewModelProvider(this@ClickedBookFragment)[HomeFragmentViewModel::class.java]

        dismissDialog()
        displayBookInfo()
        initBackButton()
        initFAB()

        return binding.root
    }

    private fun initFAB() {
        binding.fabBookmarkCamera.setOnClickListener {
            homeFragmentViewModel.captureQR(requireActivity())
        }
    }

    private fun initBackButton() {
        binding.ivBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_clickedBookFragment_to_bookListFragment)
        }
    }

    private fun displayBookInfo() {
        binding.apply {
            clickedViewModel.displayBookData(
                tvBookTitleBookList,
                tvAuthor,
                tvGenre,
                tvPublisher,
                tvPublicationDate,
                tvSynopsis,
                arguments?.getString("bookTitleKey")!!,
                requireActivity(),
                ivMainBG,
                this@ClickedBookFragment
            )
        }
    }

    private fun displayToastMessage(message: String?) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun dismissDialog() {
        Resources.dismissDialog()
    }
}