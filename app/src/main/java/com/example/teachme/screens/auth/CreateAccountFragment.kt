package com.example.teachme.screens.auth

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.teachme.AuthViewModel
import com.example.teachme.Dialogs
import com.example.teachme.databinding.FragmentCreateAccountBinding
import com.example.teachme.models.RegisterForm

class CreateAccountFragment : Fragment() {
    private var _binding: FragmentCreateAccountBinding? = null
    private val binding: FragmentCreateAccountBinding get() = _binding!!

    private var extraDetailsDialog: AlertDialog? = null

    val viewModel: AuthViewModel by activityViewModels()
    private var selectedProfileImage: Uri? = null

    val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        binding.selectedImage.setImageURI(uri)
        selectedProfileImage = uri
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCreateAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnHaveAccount.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.pickImageLayout.setOnClickListener {
            galleryLauncher.launch("image/*")
        }
        binding.btnCreateAccount.setOnClickListener {


            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val fullName = binding.etFullName.text.toString()
            val phone = binding.etPhone.text.toString()

            if (phone.isEmpty()) {
                binding.etPhoneLayout.error = "Phone must not be empty"
                return@setOnClickListener
            }
            if (!Patterns.PHONE.matcher(phone).matches()) {
                binding.etPhoneLayout.error = "Phone invalid"
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                binding.etEmailLayout.error = "Email must not be empty"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.etPasswordLayout.error = "Password must not be empty"
                return@setOnClickListener
            }
            if (fullName.isEmpty()) {
                binding.etFullNameLayout.error = "Full name must not be empty"
                return@setOnClickListener
            }

            if (binding.rgAccountType.checkedRadioButtonId == binding.rbTeacherAccount.id) {
                // Create account Teacher
                extraDetailsDialog?.show() ?: run {
                    extraDetailsDialog =
                        Dialogs.openTeacherExtraDetailsDialog(requireContext()) { details ->
                            binding.btnCreateAccount.isEnabled = false
                            viewModel.register(
                                form = RegisterForm(
                                    teacher = true,
                                    teacherDetails = details,
                                    email = email,
                                    password = password,
                                    fullName = fullName,
                                    phone = phone,
                                    image = selectedProfileImage
                                )
                            ) {
                                binding.btnCreateAccount.isEnabled = true
                            }
                        }
                }
            } else { // Create Account Student
                binding.btnCreateAccount.isEnabled = false
                viewModel.register(
                    form = RegisterForm(
                        teacher = false,
                        email = email,
                        password = password,
                        phone = phone,
                        fullName = fullName,
                        image = selectedProfileImage
                    )
                ) {
                    binding.btnCreateAccount.isEnabled = true
                }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        extraDetailsDialog = null
    }

}