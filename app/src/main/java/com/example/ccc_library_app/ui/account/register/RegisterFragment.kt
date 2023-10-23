package com.example.ccc_library_app.ui.account.register

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ccc_library_app.R
import com.example.ccc_library_app.databinding.FragmentRegisterBinding
import com.example.ccc_library_app.ui.account.util.Resources
import dagger.hilt.android.AndroidEntryPoint


@Suppress("DEPRECATION")
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

        initNetworkDialog()
        initClickedButton()

        return binding.root
    }

    private fun initNetworkDialog() {
        if (!registerViewModel.isNetworkAvailable(requireContext())) {
            //  Displays no-internet-connection dialog
            Resources.displayCustomDialog(
                activity = requireActivity(),
                hostFragment = this@RegisterFragment,
                layoutDialog = R.layout.custom_dialog_no_connection
            )
        }
    }

    private fun initClickedButton() {
        binding.apply {
            registerViewModel.validatePasswordStrength(
                etPassword,
                ivPassword1,
                tvPassword1,
                ivPassword2,
                tvPassword2,
                ivPassword3,
                tvPassword3,
                ivPassword4,
                tvPassword4,
                requireContext()
            )

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
                    //  Checks if inputted password follows the indicated password format
                    if (
                        tvPassword1.currentTextColor != ContextCompat.getColor(requireContext(), R.color.Theme_color) ||
                        tvPassword2.currentTextColor != ContextCompat.getColor(requireContext(), R.color.Theme_color) ||
                        tvPassword3.currentTextColor != ContextCompat.getColor(requireContext(), R.color.Theme_color) ||
                        tvPassword4.currentTextColor != ContextCompat.getColor(requireContext(), R.color.Theme_color)
                    ) {
                        txtInputLayoutPassword.boxStrokeColor = resources.getColor(R.color.required)
                        etPassword.setText("")
                        etPassword.requestFocus()
                        Toast.makeText(requireContext(), "Invalid password format", Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }

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
                            requireContext(),
                            requireActivity(),
                            this@RegisterFragment
                        )
                    } else {
                        //  Clears entries for password and confirm password
                        etPassword.setText("")
                        etConfirmPassword.setText("")

                        Toast.makeText(requireContext(), "Password and Confirm password doesn't match", Toast.LENGTH_LONG).show()
                        txtInputLayoutPassword.boxStrokeColor = resources.getColor(R.color.required)
                        txtInputLayoutConfirmPassword.boxStrokeColor = resources.getColor(R.color.required)
                    }
                } else {
                    Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_LONG).show()
                    Log.d(TAG, "initClickedButton: $credentialsModel")
                }
            }

            cvExit.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_homeAccountFragment)
            }

            tvHaveAccount.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }

            cvGoogleRegister.setOnClickListener {
                registerViewModel.signInUsingGoogle(requireActivity())
            }
        }
    }
}