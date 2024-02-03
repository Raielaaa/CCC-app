package com.example.ccc_library_app.ui.dashboard.home.inventory

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ccc_library_app.R
import com.example.ccc_library_app.databinding.FragmentInventorySeelAllBottomSheetListDialogItemBinding
import com.example.ccc_library_app.ui.dashboard.util.CompleteBookInfoModel
import com.google.firebase.storage.FirebaseStorage

class InventorySeeAllAdapter(
    private val collections: ArrayList<CompleteBookInfoModel>,
    private val context: Context,
    private val storage: FirebaseStorage
) : RecyclerView.Adapter<InventorySeeAllAdapter.InventorySeeAllViewHolder>() {
    inner class InventorySeeAllViewHolder(
        private val binding: FragmentInventorySeelAllBottomSheetListDialogItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CompleteBookInfoModel) {
            binding.apply {

                val gsReference = storage.getReferenceFromUrl("gs://ccc-library-system.appspot.com/book_images/${item.modelBookCode}.jpg")
                Glide.with(context)
                    .load(gsReference)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(ivDialogImage)

                tvDialogTitle.text = item.modelBookTitle
                tvDialogAuthor.text = "Author: ${item.modelBookAuthor}"
                tvDialogGenre.text = "Genre: ${item.modelBookGenre}"
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