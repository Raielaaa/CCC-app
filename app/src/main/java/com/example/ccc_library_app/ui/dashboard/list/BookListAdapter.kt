package com.example.ccc_library_app.ui.dashboard.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ccc_library_app.databinding.FragmentBookListListItemBinding

class BookListAdapter(
    private val collections: List<BookListItemModel>,
    private val clickedListener: (BookListItemModel) -> Unit
) : RecyclerView.Adapter<BookListAdapter.BookListViewHolder>() {
    inner class BookListViewHolder(private val binding: FragmentBookListListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(bookListModel: BookListItemModel, clickedListener: (BookListItemModel) -> Unit) {
            binding.apply {
                ivBook.setImageURI(bookListModel.ivBook)
                tvBookTitle.text = bookListModel.tvBookTitle
                tvBookGenre.text = bookListModel.tvBookGenre
            }
            binding.root.setOnClickListener {
                clickedListener(bookListModel)
            }
        }
    }


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