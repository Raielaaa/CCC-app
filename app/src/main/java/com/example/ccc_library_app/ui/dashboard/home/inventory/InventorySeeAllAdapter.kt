package com.example.ccc_library_app.ui.dashboard.home.inventory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ccc_library_app.databinding.FragmentInventorySeelAllBottomSheetListDialogItemBinding

class InventorySeeAllAdapter(
    private val collections: ArrayList<InventoryItemsDataModel>
) : RecyclerView.Adapter<InventorySeeAllAdapter.InventorySeeAllViewHolder>() {
    inner class InventorySeeAllViewHolder(
        private val binding: FragmentInventorySeelAllBottomSheetListDialogItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: InventoryItemsDataModel) {
            binding.apply {
                ivDialogImage.setImageURI(item.bookImage)
                tvDialogTitle.text = item.bookTitle
                tvDialogAuthor.text = "Author: ${item.bookAuthor}"
                tvDialogGenre.text = "Genre: ${item.bookGenre}"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventorySeeAllViewHolder {
        val binding = FragmentInventorySeelAllBottomSheetListDialogItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return InventorySeeAllViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return collections.size
    }

    override fun onBindViewHolder(holder: InventorySeeAllViewHolder, position: Int) {
        holder.bind(collections[position])
    }
}