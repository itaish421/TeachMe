package com.example.teachme.screens.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.teachme.AuthViewModel
import com.example.teachme.Dialogs
import com.example.teachme.R
import com.example.teachme.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding: FragmentSignInBinding get() = _binding!!
    val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.forgotPass.setOnClickListener {
            Dialogs.openPasswordResetDialog(requireContext())
        }
        binding.btnNoAccount.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_createAccountFragment)
        }

        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            if (email.isEmpty()) {
                binding.etEmailLayout.error = "Email must not be empty"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.etPasswordLayout.error = "Password must not be empty"
                return@setOnClickListener
            }

            binding.btnSignIn.isEnabled = false
            viewModel.login(email, password) {
                binding.btnSignIn.isEnabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}