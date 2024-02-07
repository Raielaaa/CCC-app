package com.example.ccc_library_app.ui.dashboard.list

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ccc_library_app.R
import com.example.ccc_library_app.databinding.FragmentBookListListItemBinding
import com.example.ccc_library_app.ui.dashboard.util.CompleteBookInfoModel
import com.google.firebase.storage.FirebaseStorage

class BookListAdapter(
    private val storage: FirebaseStorage,
    private val context: Context,
    private val collections: ArrayList<CompleteBookInfoModel>,
    private val clickedListener: (CompleteBookInfoModel) -> Unit
) : RecyclerView.Adapter<BookListAdapter.BookListViewHolder>() {
    inner class BookListViewHolder(private val binding: FragmentBookListListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(bookListModel: CompleteBookInfoModel, clickedListener: (CompleteBookInfoModel) -> Unit) {
            binding.apply {
                val gsReference = storage.getReferenceFromUrl("gs://ccc-library-system.appspot.com/${bookListModel.modelBookImage}")
                Glide.with(context)
                    .load(gsReference)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(ivBook)
                tvBookTitle.text = bookListModel.modelBookTitle
                tvBookGenre.text = bookListModel.modelBookGenre

                tvListStatus.apply {
                    if (bookListModel.modelBookStatus == "Available") {
                        text = "AVAILABLE"
                        setTextColor(ContextCompat.getColor(context, R.color.green))
                    } else {
                        text = "UNAVAILABLE"
                        setTextColor(ContextCompat.getColor(context, R.color.red))
                    }
                }
            }
            binding.root.setOnClickListener {
                clickedListener(bookListModel)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<CompleteBookInfoModel>) {
        collections.clear()
        collections.addAll(newData)
        notifyDataSetChanged()
    }

    fun getAllData() : List<CompleteBookInfoModel> = collections

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookListViewHolder {
        val binding = FragmentBookListListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return collections.size
    }

    override fun onBindViewHolder(holder: BookListViewHolder, position: Int) {
        holder.bind(collections[position], clickedListener)
    }
}