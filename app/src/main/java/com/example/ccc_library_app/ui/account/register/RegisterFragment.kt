package com.example.ccc_library_app.ui.account.register

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ccc_library_app.R
import com.example.ccc_library_app.databinding.FragmentRegisterBinding
import com.example.ccc_library_app.ui.account.util.Resources
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class RegisterFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var credentialsModel: CredentialsModel
    private lateinit var registerTermsAndCondition: RegisterTermsAndCondition
    private lateinit var sharedPreferences: SharedPreferences
    private val TAG: String = "MyTag"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        registerViewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
        sharedPreferences = requireActivity().getSharedPreferences("TermsConditionsSP", Context.MODE_PRIVATE)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        initializeViews()

        return binding.root
    }

    private fun initializeViews() {
        initializeNetworkDialog()
        initializeClickedButtons()
        initializePasswordStrengthValidation()
        initializeGoogleSignInFunction()
        initializeRegisterFunction()
        binding.etFirstName.requestFocus()
    }

    private fun initializeNetworkDialog() {
        if (!registerViewModel.isNetworkAvailable(requireContext())) {
            Resources.displayCustomDialog(
                activity = requireActivity(),
                hostFragment = this@RegisterFragment,
                layoutDialog = R.layout.custom_dialog_no_connection
            )
        }
    }

    private fun initializePasswordStrengthValidation() {
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
        }
    }

    private fun initializeGoogleSignInFunction() {
        binding.cvGoogleRegister.setOnClickListener {
            if (validateInputForGoogleSignIn()) {
                registerViewModel.signInUsingGoogle(requireActivity())
            } else {
                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInputForGoogleSignIn(): Boolean {
        binding.apply {
            return registerViewModel.validateInputRegisterForGoogleSignIn(
                etFirstName,
                etLastName,
                etProgram,
                etYear,
                etSection,
                etUsername
            )
        }
    }

    private fun initializeRegisterFunction() {
        binding.btnRegister.setOnClickListener {
            try {
                credentialsModel = createCredentialsModel()
            } catch (e: Exception) {
                handleError(e)
            }

            if (validateInputRegister(credentialsModel)) {
                if (!validatePasswordFormat(
                    binding.tvPassword1,
                    binding.tvPassword2,
                    binding.tvPassword3,
                    binding.tvPassword4
                )) {
                    handleInvalidPasswordFormat()
                } else if (!validatePasswordsMatch()) {
                    handlePasswordsMismatch()
                } else {
                    registerTermsAndCondition.show(this.requireFragmentManager(), "TermsAndConditionDialog")
                }
            } else {
                handleMissingInput()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        registerTermsAndCondition = RegisterTermsAndCondition()
        registerTermsAndCondition.setTargetFragment(this@RegisterFragment, 0)
    }

    fun insertDataToFirebase() {
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
    }

    private fun createCredentialsModel(): CredentialsModel {
        return CredentialsModel(
            binding.etFirstName.text.toString(),
            binding.etLastName.text.toString(),
            binding.etProgram.text.toString(),
            binding.etYear.text.toString(),
            binding.etSection.text.toString(),
            binding.etUsername.text.toString(),
            binding.etEmail.text.toString(),
            binding.etPassword.text.toString(),
            binding.etConfirmPassword.text.toString()
        )
    }

    private fun handleError(e: Exception) {
        Toast.makeText(requireContext(), e.localizedMessage, Toast.LENGTH_LONG).show()
        Log.e(TAG, "Exception: ${e.message}")
    }

    private fun validatePasswordFormat(vararg tvPasswords: TextView): Boolean {
        for (tvPassword in tvPasswords) {
            if (tvPassword.currentTextColor != ContextCompat.getColor(requireContext(), R.color.Theme_color))
                return false
        }
        return true
    }

    private fun handleInvalidPasswordFormat() {
        binding.apply {
            txtInputLayoutPassword.boxStrokeColor = resources.getColor(R.color.required)
            etPassword.setText("")
            etPassword.requestFocus()
            Toast.makeText(requireContext(), "Invalid password format", Toast.LENGTH_LONG).show()
        }
    }

    private fun validatePasswordsMatch(): Boolean {
        return registerViewModel.validatePasswordRegister(
            credentialsModel.modelPassword,
            credentialsModel.modelConfirmPassword
        )
    }

    private fun handlePasswordsMismatch() {
        binding.apply {
            etPassword.setText("")
            etConfirmPassword.setText("")
            Toast.makeText(requireContext(), "Password and Confirm password don't match", Toast.LENGTH_LONG).show()
            txtInputLayoutPassword.boxStrokeColor = resources.getColor(R.color.required)
            txtInputLayoutConfirmPassword.boxStrokeColor = resources.getColor(R.color.required)
        }
    }

    private fun validateInputRegister(credentialsModel: CredentialsModel): Boolean {
        return registerViewModel.validateInputRegister(
            credentialsModel.modelFirstName,
            credentialsModel.modelLastName,
            credentialsModel.modelProgram,
            credentialsModel.modelYear,
            credentialsModel.modelSection,
            credentialsModel.modelUsername,
            credentialsModel.modelEmail,
            credentialsModel.modelPassword,
            credentialsModel.modelConfirmPassword
        )
    }

    private fun handleMissingInput() {
        Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "All fields are required")
        registerViewModel.setFocus(
            binding.etFirstName,
            binding.etLastName,
            binding.etProgram,
            binding.etYear,
            binding.etSection,
            binding.etUsername,
            binding.etEmail,
            binding.etPassword,
            binding.etConfirmPassword
        )
    }

    private fun initializeClickedButtons() {
        binding.apply {
            cvExit.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_homeAccountFragment)
            }

            tvHaveAccount.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Log.d(TAG, "onSharedPreferenceChanged: testListener")
        if (key == "booleanKey") {
            val value = sharedPreferences!!.getBoolean(key, false)
            if (value) {
                insertDataToFirebase()
            } else {
                insertDataToFirebase()
            }
        }
    }
}