package com.example.teachme.screens.main.teacher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.teachme.StudentViewModel
import com.example.teachme.adapters.TeacherRvAdapter
import com.example.teachme.databinding.FragmentStudentHomeBinding
import com.example.teachme.databinding.FragmentTeacherHomeBinding
import com.example.teachme.models.Teacher
import com.google.android.material.snackbar.Snackbar

class TeacherHomeFragment : Fragment() {

    private var _binding: FragmentTeacherHomeBinding? = null
    private val binding: FragmentTeacherHomeBinding get() = _binding!!
    private val viewModel: StudentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTeacherHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogout.setOnClickListener {
            viewModel.logOut()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}