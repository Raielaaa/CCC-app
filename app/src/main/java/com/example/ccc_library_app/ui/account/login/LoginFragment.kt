package com.example.ccc_library_app.ui.account.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ccc_library_app.R
import com.example.ccc_library_app.databinding.FragmentLoginBinding
import com.example.ccc_library_app.ui.account.register.RegisterViewModel
import com.example.ccc_library_app.ui.account.util.Resources
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        registerViewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        initializeNetworkDialog()
        initializeClickedViews()
        binding.etEmail.requestFocus()

        return binding.root
    }

    private fun initializeNetworkDialog() {
        if (!registerViewModel.isNetworkAvailable(requireContext())) {
            Resources.displayCustomDialog(
                activity = requireActivity(),
                hostFragment = this@LoginFragment,
                layoutDialog = R.layout.custom_dialog_no_connection
            )
        }
    }

    private fun initializeClickedViews() {
        binding.apply {
            cvExit.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_homeAccountFragment)
            }
            btnLoginLogin.setOnClickListener {
                handleLoginButtonClick()
            }
            tvNoAccount.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
        }
    }

    private fun handleLoginButtonClick() {
        val inputtedEmail: String? = binding.etEmail.text?.toString()
        val inputtedPassword: String? = binding.etPassword.text?.toString()

        if (loginViewModel.validateInputLogin(
                binding.txtInputLayoutEmail,
                binding.txtInputLayoutPW,
                binding.etEmail,
                binding.etPassword,
                requireActivity()
            )
        ) {
            if (inputtedEmail != null && inputtedPassword != null) {
                loginViewModel.validateCredentials(
                    email = inputtedEmail,
                    password = inputtedPassword,
                    context = requireContext(),
                    fragment = this@LoginFragment,
                    etEmail = binding.etEmail,
                    etPassword = binding.etPassword,
                    txtInputLayoutEmail = binding.txtInputLayoutEmail,
                    txtInputLayoutPW = binding.txtInputLayoutPW
                )
            } else {
                Toast.makeText(requireContext(), "Unknown error", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show()
        }
    }
}