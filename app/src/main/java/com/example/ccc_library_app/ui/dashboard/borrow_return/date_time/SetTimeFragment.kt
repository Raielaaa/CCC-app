package com.example.ccc_library_app.ui.dashboard.borrow_return.date_time

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.ccc_library_app.R
import com.example.ccc_library_app.databinding.FragmentSetTimeBinding
import java.text.DecimalFormat

class SetTimeFragment(
    private val dateTimeSelectedListener: DateTimeSelectedListener
) : DialogFragment() {
    private lateinit var binding: FragmentSetTimeBinding
    private val dateTimeDecimalFormal: DecimalFormat by lazy { DecimalFormat("00") }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSetTimeBinding.inflate(inflater, container, false)

        isCancelable = false
        initViews()

        return binding.root
    }

    private fun initViews() {
        binding.apply {
            tvCancel.setOnClickListener {
                this@SetTimeFragment.dismiss()
            }
            tvSet.setOnClickListener {
                tvSet.setOnClickListener {
                    val selectedHour: Int = if (timePicker.hour > 12) timePicker.hour - 12 else timePicker.hour
                    val selectedMinute: Int = timePicker.minute
                    val amOrPm = if (timePicker.hour >= 12) "PM" else "AM"

                    dateTimeSelectedListener.onTimeSelected("${dateTimeDecimalFormal.format(selectedHour)}:${dateTimeDecimalFormal.format(selectedMinute)} $amOrPm")
                    this@SetTimeFragment.dismiss()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setWindowAnimations(R.style.animation)
    }
}