package com.example.ccc_library_app.ui.dashboard.home.popular

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.ccc_library_app.R
import com.example.ccc_library_app.databinding.MainPopularListItemBinding
import com.google.firebase.storage.FirebaseStorage
import android.graphics.drawable.Drawable
import com.bumptech.glide.request.target.Target

class PopularAdapter(
    private val context: Context,
    private val storage: FirebaseStorage,
    private val bookListPopularTemp: ArrayList<FirebaseDataModel>,
    private val clickedListener: (FirebaseDataModel) -> Unit
) : RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {
    inner class PopularViewHolder(private val binding: MainPopularListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(popularModel: FirebaseDataModel, clickedListener: (FirebaseDataModel) -> Unit) {
            binding.apply {
                txtBookTitle.text = popularModel.modelBookTitle

                val firebaseStorage = FirebaseStorage.getInstance()
                val gsReference = firebaseStorage.getReferenceFromUrl("gs://ccc-library-system.appspot.com/${popularModel.modelBookImage}")

                Glide.with(context)
                    .load(gsReference)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(ivBookImage)

                root.setOnClickListener {
                    clickedListener(popularModel)
                }
            }
        }
    }

    private var collection: ArrayList<FirebaseDataModel> = ArrayList()

    fun setList(popularModel: List<FirebaseDataModel>) {
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
        holder.bind(collection[position], clickedListener)
    }
}