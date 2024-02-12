package com.example.ccc_library_app.ui.dashboard.bookmark.rv

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ccc_library_app.R
import com.example.ccc_library_app.databinding.BookmarkListItemsBinding
import com.example.ccc_library_app.ui.dashboard.bookmark.BookmarkModel
import com.example.ccc_library_app.ui.dashboard.util.Constants
import com.example.ccc_library_app.ui.dashboard.util.DataCache
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BookmarkAdapter(
    private val firebaseStorage: FirebaseStorage,
    private val context: Context,
    private val tvTopUsername: TextView,
    private val tvTopBorrowCount: TextView,
    private val tvTopStatus: TextView,
    private val cvTopStatus: CardView,
    private val ivTopLogout: ImageView,
    private val hostFragment: Fragment,
    private val firebaseFireStore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>() {

    private var collections = ArrayList<BookmarkModel>()

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newCollection: ArrayList<BookmarkModel>) {
        collections.apply {
            clear()
            addAll(newCollection)
            notifyDataSetChanged()
        }
    }

    inner class BookmarkViewHolder(private val binding: BookmarkListItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: BookmarkModel) {
            binding.apply {
                tvBookmarkTitle.text = data.bookTitle
                tvBookmarkBorrow.text = data.bookBorrow
                tvBookmarkDeadline.text = data.bookDeadline
                tvBookmarkGenre.text = data.bookGenre

                if (getBorrowTimeDifference(data.bookDeadline).toInt() <= 0) {
                    tvBookmarkStatus.text = "PAST-DUE!"
                    tvBookmarkStatus.setTextColor(ContextCompat.getColor(context, R.color.red))
                    tvBookmarkDeadline.setTextColor(ContextCompat.getColor(context, R.color.red))

                    DataCache.bookmarkPastDueCounter++
                } else {
                    tvBookmarkStatus.text = "ON-BORROW"
                    tvBookmarkStatus.setTextColor(ContextCompat.getColor(context, R.color.green))
                }

                val gsReference = firebaseStorage.getReferenceFromUrl("gs://ccc-library-system.appspot.com/book_images/${data.bookCode}.jpg")
                Glide.with(context)
                    .load(gsReference)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(ivBookmarkImage)
            }
            initTopView()
        }

        private fun getBorrowTimeDifference(modelBorrowDeadlineDateTime: String): String {
            val borrowDeadlineDateTime = modelBorrowDeadlineDateTime.split("-")

            val borrowDeadlineDateTimeFinal = "${borrowDeadlineDateTime[0]} ${borrowDeadlineDateTime[1].replace("\\s*:\\s*".toRegex(), ":")}"
            val currentDateTime = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()).format(
                Date()
            )

            return calculateDateTimeDifferenceInMinutes(currentDateTime, borrowDeadlineDateTimeFinal).toString()
        }

        private fun calculateDateTimeDifferenceInMinutes(dateTime1: String, dateTime2: String): Long {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())

            // Parse date-time strings to Date objects
            val parsedDateTime1 = dateFormat.parse(dateTime1)
            val parsedDateTime2 = dateFormat.parse(dateTime2)

            // Calculate time difference in milliseconds
            val timeDifference = parsedDateTime2!!.time - parsedDateTime1!!.time

            // Convert milliseconds to minutes
            return timeDifference / (60 * 1000)
        }
    }

    private fun initTopView() {
        val borrowCount = DataCache.bookmarkBorrowCount
        val pastDueCount = DataCache.bookmarkPastDueCounter

        tvTopBorrowCount.text = "$borrowCount / 3"
        tvTopStatus.apply {
            if (borrowCount == 0) {
                text = "NA"
                setTextColor(ContextCompat.getColor(hostFragment.requireContext(), R.color.white))
            } else if (borrowCount == 3) {
                text = "LIMIT REACHED!"
                setTextColor(ContextCompat.getColor(hostFragment.requireContext(), R.color.light_red))
            } else {
                text = "ON-BORROW"
                setTextColor(ContextCompat.getColor(hostFragment.requireContext(), R.color.green))
            }
        }

        cvTopStatus.apply {
            if (pastDueCount > 0) {
                setCardBackgroundColor(ContextCompat.getColor(hostFragment.requireContext(), R.color.light_red))
            } else {
                setCardBackgroundColor(ContextCompat.getColor(hostFragment.requireContext(), R.color.green))
            }

            if (borrowCount == 0) {
                setCardBackgroundColor(ContextCompat.getColor(hostFragment.requireContext(), R.color.white))
            }
        }

        ivTopLogout.setOnClickListener {
            com.example.ccc_library_app.ui.account.util.Resources.displayCustomDialogLogout(
                hostFragment.requireActivity(),
                R.layout.custom_dialog_logout,
                "Logout Notice",
                "Are you sure you want to exit the application?"
            )
        }

        firebaseFireStore.collection("ccc-library-app-user-data")
            .document(firebaseAuth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                tvTopUsername.text = "Hello, ${documentSnapshot.get("modelUsername")}!"
            }.addOnFailureListener { exception ->
                Log.e(Constants.TAG, "initTopView: ${exception.message}")
                Toast.makeText(
                    hostFragment.requireContext(),
                    "Failed to get username",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val binding = BookmarkListItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookmarkViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return collections.size
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        holder.bind(collections[position])
    }
}