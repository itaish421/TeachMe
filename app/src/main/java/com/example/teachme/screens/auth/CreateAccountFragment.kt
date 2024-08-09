package com.example.teachme.screens.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.teachme.Dialogs
import com.example.teachme.databinding.FragmentCreateAccountBinding

class CreateAccountFragment : Fragment() {
    private var _binding: FragmentCreateAccountBinding? = null
    private val binding: FragmentCreateAccountBinding get() = _binding!!


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
        binding.btnCreateAccount.setOnClickListener {


            if (binding.rgAccountType.checkedRadioButtonId == binding.rbTeacherAccount.id) {
                // Create account
                Dialogs.openTeacherExtraDetailsDialog(requireContext()) { details ->
                    println(details)
                }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}