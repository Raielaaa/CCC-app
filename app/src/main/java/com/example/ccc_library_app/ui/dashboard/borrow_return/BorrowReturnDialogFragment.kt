package com.example.ccc_library_app.ui.dashboard.borrow_return

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.ccc_library_app.R
import com.example.ccc_library_app.databinding.FragmentBorrowReturnDialogBinding
import com.example.ccc_library_app.ui.dashboard.borrow_return.date_time.DateTimeSelectedListener
import com.example.ccc_library_app.ui.dashboard.borrow_return.date_time.SetDateFragment
import com.example.ccc_library_app.ui.dashboard.borrow_return.date_time.SetTimeFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class BorrowReturnDialogFragment(
    private val barcodeValue: String,
    private val firebaseFireStore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStorage: FirebaseStorage,
    private val bitmap: Bitmap
) : BottomSheetDialogFragment(), DateTimeSelectedListener {
    private lateinit var binding: FragmentBorrowReturnDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBorrowReturnDialogBinding.inflate(inflater, container, false)

        initViews()

        return binding.root
    }

    private fun initViews() {
        initSpinner()
        initDateTimeChooser()
        initContentDisplay()
    }

    private fun initContentDisplay() {
        Log.d("MyTag", "initContentDisplay: $barcodeValue")
        val bookCodeFromQR = barcodeValue.split(":")[3]

        firebaseFireStore.collection("ccc-library-app-book-info")
            .document(bookCodeFromQR)
            .get()
            .addOnCompleteListener { result ->
                if (result.isSuccessful) {
                    //  Retrieving book value from DB
                    val bookAuthor = result.result.get("modelBookAuthor")
                    val bookCode = result.result.get("modelBookCode")
                    val bookDescription = result.result.get("modelBookDescription")
                    val bookGenre = result.result.get("modelBookGenre")
                    val bookImage = result.result.get("modelBookImage")
                    val bookPublicationDate = result.result.get("modelBookPublicationDate")
                    val bookPublisher = result.result.get("modelBookPublisher")
                    val bookTitle = result.result.get("modelBookTitle")
                    val bookStatus = result.result.get("modelStatus")

                    //  Retrieving user information
                    firebaseFireStore.collection("ccc-library-app-user-data")
                        .document(firebaseAuth.currentUser!!.uid)
                        .get()
                        .addOnCompleteListener { resultChild ->
                            if (resultChild.isSuccessful) {
                                val userFirstName = resultChild.result.get("modelFirstName")
                                val userLastName = resultChild.result.get("modelLastName")
                                val userProgram = resultChild.result.get("modelProgram")
                                val userSection = resultChild.result.get("modelSection")
                                val userUsername = resultChild.result.get("modelUsername")
                                val userYear = resultChild.result.get("modelYear")
                                val userEmail = firebaseAuth.currentUser!!.email

                                binding.apply {
                                    tvBookName.text = bookTitle.toString()
                                    tvUserEmail.text = "$userLastName-$userYear$userSection-$userEmail"
                                    tvBookAuthor.text = bookAuthor.toString()
                                    tvBookGenreBRD.text = bookGenre.toString()

                                    tvBookStatus.apply {
                                        if (bookStatus.toString() == "Available") {
                                            text = bookStatus.toString()
                                            setTextColor(ContextCompat.getColor(context, R.color.green))
                                        } else {
                                            text = bookStatus.toString()
                                            setTextColor(ContextCompat.getColor(context, R.color.red))
                                        }
                                    }

                                    ivImage.setImageBitmap(bitmap)

                                    val gsReference = firebaseStorage.getReferenceFromUrl("gs://ccc-library-system.appspot.com/book_images/$bookCode.jpg")
                                    Glide.with(requireContext())
                                        .load(gsReference)
                                        .placeholder(R.drawable.placeholder_image)
                                        .error(R.drawable.error_image)
                                        .into(ivBookImageBRD)
                                }
                            }
                        }.addOnFailureListener { exceptionChild ->
                            Toast.makeText(
                                context,
                                "An error occurred: ${exceptionChild.localizedMessage}",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("MyTag", "initContentDisplay: ${exceptionChild.message}")
                        }
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(
                    context,
                    "An error occurred: ${exception.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("MyTag", "initContentDisplay: ${exception.message}")
            }
    }

    private fun initDateTimeChooser() {
        binding.apply {
            cvSetDate.setOnClickListener {
                SetDateFragment(this@BorrowReturnDialogFragment).show(parentFragmentManager, "SetDate_Dialog")
            }
            cvSetTime.setOnClickListener {
                SetTimeFragment(this@BorrowReturnDialogFragment).show(parentFragmentManager, "SetTime_Dialog")
            }
        }
    }

    private fun initSpinner() {
        binding.apply {
            val spinner: Spinner = spUser2
            val spinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.borrow_return_option,
                android.R.layout.simple_spinner_dropdown_item
            )

            spinner.adapter = spinnerAdapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedItem = parent?.getItemAtPosition(position).toString()

                    if (selectedItem == "RETURN") {
                        cvSetDate.isEnabled = false
                        setViewAndChildrenEnabled(cvSetDate, false)

                        cvSetTime.isEnabled = false
                        setViewAndChildrenEnabled(cvSetTime, false)
                    } else {
                        cvSetDate.isEnabled = true
                        setViewAndChildrenEnabled(cvSetDate, true)

                        cvSetTime.isEnabled = true
                        setViewAndChildrenEnabled(cvSetTime, true)
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    //  Nothing
                }
            }
        }
    }

    private fun setViewAndChildrenEnabled(view: View, enabled: Boolean) {
        view.isEnabled = enabled
        if (view is ViewGroup) {
            for (index in 0 until view.childCount) {
                val child = view.getChildAt(index)
                setViewAndChildrenEnabled(child, enabled)
            }
        }
    }

    override fun onDateSelected(selectedDate: String) {
        binding.tvDate.text = selectedDate
    }

    override fun onTimeSelected(selectedTime: String) {
        binding.tvTime.text = selectedTime
    }
}