package com.example.ccc_library_app.ui.dashboard.borrow_return

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.ccc_library_app.R
import com.example.ccc_library_app.databinding.FragmentBorrowReturnDialogBinding
import com.example.ccc_library_app.ui.dashboard.borrow_return.date_time.DateTimeSelectedListener
import com.example.ccc_library_app.ui.dashboard.borrow_return.date_time.SetDateFragment
import com.example.ccc_library_app.ui.dashboard.borrow_return.date_time.SetTimeFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BorrowReturnDialogFragment : BottomSheetDialogFragment(), DateTimeSelectedListener {
    private lateinit var binding: FragmentBorrowReturnDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBorrowReturnDialogBinding.inflate(inflater, container, false)

        initViews()

        return binding.root
    }

    private fun initViews() {
        initSpinner()
        initDateTimeChooser()
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
    }

    override fun onTimeSelected(selectedTime: String) {
    }
}