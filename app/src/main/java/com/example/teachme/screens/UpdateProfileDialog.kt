package com.example.teachme.screens

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.example.teachme.databinding.UpdateDialogBinding
import com.example.teachme.models.User
import com.squareup.picasso.Picasso

class UpdateProfileDialog(private val user: User, private val onSaveChanges: (newName: String, newImage: Uri?) -> Unit): DialogFragment() {



    private var _binding: UpdateDialogBinding? = null
    private val binding: UpdateDialogBinding get() = _binding!!

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
        savedInstanceState: Bundle?
    ): View {
        _binding = UpdateDialogBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Picasso.get().load(user.image).into(binding.selectedImage)
        binding.etName.setText(user.fullName)
        binding.ivUpdate.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        binding.btnSaveChanges.setOnClickListener {
            val newName = binding.etName.text.toString()
            if(newName.isEmpty()) {
                Toast.makeText(requireContext(), "Could not set name to empty", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            onSaveChanges.invoke(newName, selectedProfileImage)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}