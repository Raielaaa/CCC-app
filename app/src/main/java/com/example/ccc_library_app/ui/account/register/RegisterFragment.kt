package com.example.ccc_library_app.ui.account.register

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.ccc_library_app.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var credentialsModel: CredentialsModel
    private val TAG: String = "MyTag"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container,false)
        registerViewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        initClickedButton()

        return binding.root
    }

    private fun initClickedButton() {
        binding.apply {
            btnRegister.setOnClickListener {
                try {
                    //  Insert credential-values in DataModel data class
                    credentialsModel = CredentialsModel(
                        etFirstName.text.toString(),
                        etLastName.text.toString(),
                        etProgram.text.toString(),
                        etYear.text.toString(),
                        etSection.text.toString(),
                        etUsername.text.toString(),
                        etEmail.text.toString(),
                        etPassword.text.toString(),
                        etConfirmPassword.text.toString()
                    )
                } catch (e: NumberFormatException) {
                    Toast.makeText(requireContext(), e.localizedMessage, Toast.LENGTH_LONG).show()
                    Log.e(TAG, "initClickedButton-NumberFormatException: ${e.message}")
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), e.localizedMessage, Toast.LENGTH_LONG).show()
                    Log.e(TAG, "initClickedButton-Exception: ${e.message}")
                }

                //  Checks if all fields have input
                if (registerViewModel.validateInputRegister(
                    credentialsModel.modelFirstName,
                    credentialsModel.modelLastName,
                    credentialsModel.modelProgram,
                    credentialsModel.modelYear,
                    credentialsModel.modelSection,
                    credentialsModel.modelUsername,
                    credentialsModel.modelEmail,
                    credentialsModel.modelPassword,
                    credentialsModel.modelConfirmPassword,
                )) {
                    //  Checks if password matches with confirm password
                    if (registerViewModel.validatePasswordRegister(
                        credentialsModel.modelPassword,
                        credentialsModel.modelConfirmPassword
                    )) {
                        //  Inserts data to firebase auth and firebase fireStore
                        registerViewModel.insertDataToFirebase(
                            credentialsModel.modelFirstName,
                            credentialsModel.modelLastName,
                            credentialsModel.modelProgram,
                            credentialsModel.modelYear,
                            credentialsModel.modelSection,
                            credentialsModel.modelUsername,
                            credentialsModel.modelEmail,
                            credentialsModel.modelPassword,
                            requireContext()
                        )
                    } else {
                        //  Clears entries for password and confirm password
                        etPassword.setText("")
                        etConfirmPassword.setText("")

                        Toast.makeText(requireContext(), "Password and Confirm password doesn't match", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_LONG).show()
                    Log.d(TAG, "initClickedButton: ${credentialsModel}")
                }
            }
        }
    }
}