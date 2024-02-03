package com.example.ccc_library_app.ui.dashboard.home.inventory

import android.app.Activity
import android.os.Bundle
import android.util.DisplayMetrics
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.ccc_library_app.R
import com.example.ccc_library_app.databinding.FragmentInventorySeelAllBottomSheetListDialogBinding
import com.example.ccc_library_app.ui.dashboard.util.CompleteBookInfoModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.storage.FirebaseStorage

class InventorySeeAllBottomSheetFragment(
    private val collections: ArrayList<CompleteBookInfoModel>,
    private val label: String
) : BottomSheetDialogFragment() {

    private var _binding: FragmentInventorySeelAllBottomSheetListDialogBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        (dialog as? BottomSheetDialog)?.let {
//            it.behavior.peekHeight = 1000
//        }

        _binding = FragmentInventorySeelAllBottomSheetListDialogBinding.inflate(inflater, container, false)

        val adapter = InventorySeeAllAdapter(collections, requireContext(), FirebaseStorage.getInstance())
        _binding!!.apply {
            rvDialogMain.adapter = adapter
            tvLabel.text = label

            btnViewAll.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_bookListFragment)
                dismiss()
            }
        }

        return binding.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}