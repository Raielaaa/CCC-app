package com.example.ccc_library_app.ui.account.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ccc_library_app.R
import com.example.ccc_library_app.databinding.FragmentLoginBinding
import com.example.ccc_library_app.ui.account.register.RegisterViewModel
import com.example.ccc_library_app.ui.account.util.Resources
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        registerViewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        initNetworkDialog()
        initClickedViews()

        return binding.root
    }

    private fun initNetworkDialog() {
        if (!registerViewModel.isNetworkAvailable(requireContext())) {
            //  Displays no-internet-connection dialog
            Resources.displayCustomDialog(
                activity = requireActivity(),
                hostFragment = this@LoginFragment,
                layoutDialog = R.layout.custom_dialog_no_connection
            )
        }
    }

    private fun initClickedViews() {
        binding.apply {
            cvExit.setOnClickListener {
                findNavController().navigate(R.id.homeAccountFragment)
            }
            btnLoginLogin.setOnClickListener {
                val inputtedEmail: String? = etEmail.text?.toString()
                val inputtedPassword: String? = etPassword.text?.toString()
                if (
                    inputtedEmail.isNullOrEmpty() ||
                    inputtedPassword.isNullOrEmpty()
                ) {
                    txtInputLayoutEmail.boxStrokeColor = resources.getColor(R.color.required)
                    txtInputLayoutPW.boxStrokeColor = resources.getColor(R.color.required)
                    etEmail.requestFocus()

                    Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_LONG).show()
                } else {
                    loginViewModel.validateCredentials(
                        email = inputtedEmail,
                        password = inputtedPassword,
                        context = requireContext(),
                        fragment = this@LoginFragment,
                        etEmail = etEmail,
                        etPassword = etPassword,
                        txtInputLayoutEmail = txtInputLayoutEmail,
                        txtInputLayoutPW = txtInputLayoutPW
                    )
                }
            }
        }
    }
}