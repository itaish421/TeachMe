package com.example.teachme.screens.main.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.teachme.StudentViewModel
import com.example.teachme.adapters.TeacherRvAdapter
import com.example.teachme.components.TeacherCalendar
import com.example.teachme.databinding.FragmentStudentHomeBinding
import com.example.teachme.models.Teacher
import com.google.android.material.snackbar.Snackbar

class StudentHomeFragment : Fragment() {

    private var _binding: FragmentStudentHomeBinding? = null
    private val binding: FragmentStudentHomeBinding get() = _binding!!
    private val viewModel: StudentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentStudentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogout.setOnClickListener {
            viewModel.logOut()
        }
        viewModel.teachers.observe(viewLifecycleOwner) {
            binding.rvTeachers.adapter = TeacherRvAdapter(
                teachers = it,
                listener = object : TeacherRvAdapter.TeacherListener {
                    override fun onScheduleWithTeacher(teacher: Teacher) {
                        TeacherCalendar(teacher)
                            .show(childFragmentManager, "onScheduleWithTeacher")
                    }
                }
            )
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}