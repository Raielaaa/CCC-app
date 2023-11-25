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

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding =
            FragmentInventorySeelAllBottomSheetListDialogBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.findViewById<RecyclerView>(R.id.list)?.layoutManager = LinearLayoutManager(context)
        activity?.findViewById<RecyclerView>(R.id.list)?.adapter = ItemAdapter(collections)
    }

    private inner class ViewHolder(binding: FragmentInventorySeelAllBottomSheetListDialogItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: InventoryItemsDataModel) {

        }
    }

    private inner class ItemAdapter(
        private val mItemCount: ArrayList<InventoryItemsDataModel>
    ) : RecyclerView.Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                FragmentInventorySeelAllBottomSheetListDialogItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(collections[position])
        }

        override fun getItemCount(): Int {
            return mItemCount.size
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}