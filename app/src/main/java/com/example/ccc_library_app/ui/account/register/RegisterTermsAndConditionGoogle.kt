package com.example.ccc_library_app.ui.account.register

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import com.example.ccc_library_app.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RegisterTermsAndConditionGoogle : BottomSheetDialogFragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: Editor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register_terms_and_condition, container, false)
        sharedPreferences = requireParentFragment().requireActivity().getSharedPreferences("TermsConditionsSP", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        val btnDecline = view.findViewById<AppCompatButton>(R.id.btnDecline)
        val btnAccept = view.findViewById<AppCompatButton>(R.id.btnAccept)

        btnDecline.setOnClickListener {
            this.dismiss()
        }

        btnAccept.setOnClickListener {
            // Access the parent fragment and call its method
            editor.apply {
                putBoolean("booleanKeyGoogle",
                    !sharedPreferences.getBoolean("booleanKeyGoogle", true)
                )
                commit()
            }
            this.dismiss()
        }

        return view
    }
}