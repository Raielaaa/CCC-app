package com.example.ccc_library_app.ui.dashboard.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ccc_library_app.databinding.FragmentBookListListItemBinding

class BookListAdapter(

) : RecyclerView.Adapter<BookListAdapter.BookListViewHolder>() {
    inner class BookListViewHolder(private val binding: FragmentBookListListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(bookListModel: BookListItemModel) {
            binding.apply {
                ivBook.setImageURI(bookListModel.ivBook)
                tvBookTitle.text = bookListModel.tvBookTitle
                tvBookGenre.text = bookListModel.tvBookGenre
            }
        }
    }

    private val collections: ArrayList<BookListItemModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookListViewHolder {
        val binding = FragmentBookListListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookListViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(bookInfo: List<BookListItemModel>) {
        collections.apply {
            clear()
            addAll(bookInfo)
        }
        this@BookListAdapter.notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return collections.size
    }

    override fun onBindViewHolder(holder: BookListViewHolder, position: Int) {
        holder.bind(collections[position])
    }
}