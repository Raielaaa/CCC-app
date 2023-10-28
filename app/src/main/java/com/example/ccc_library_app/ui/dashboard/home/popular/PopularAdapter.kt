package com.example.ccc_library_app.ui.dashboard.home.popular

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ccc_library_app.databinding.MainPopularListItemBinding

class PopularAdapter(

) : RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {
    inner class PopularViewHolder(private val binding: MainPopularListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(popularModel: PopularModel) {
            binding.apply {
                ivBookImage.setImageURI(popularModel.uriImage)
                txtBookTitle.text = popularModel.bookTitle
            }
        }
    }

    private var collection: ArrayList<PopularModel> = ArrayList()

    fun setList(popularModel: List<PopularModel>) {
        collection.apply {
            clear()
            addAll(popularModel)
            this@PopularAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        val binding = MainPopularListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PopularViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return collection.size
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        holder.bind(collection[position])
    }
}