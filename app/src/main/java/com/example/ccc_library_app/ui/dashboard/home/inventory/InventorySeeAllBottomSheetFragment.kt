package com.example.ccc_library_app.ui.dashboard.home.inventory

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.ccc_library_app.R
import com.example.ccc_library_app.databinding.FragmentInventorySeelAllBottomSheetListDialogItemBinding
import com.example.ccc_library_app.databinding.FragmentInventorySeelAllBottomSheetListDialogBinding

class InventorySeeAllBottomSheetFragment(
    private val collections: ArrayList<InventoryItemsDataModel>
) : BottomSheetDialogFragment() {

    private var _binding: FragmentInventorySeelAllBottomSheetListDialogBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentInventorySeelAllBottomSheetListDialogBinding.inflate(inflater, container, false)

        val adapter = InventorySeeAllAdapter(collections)
        _binding!!.rvDialogMain.adapter = adapter

        return binding.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}