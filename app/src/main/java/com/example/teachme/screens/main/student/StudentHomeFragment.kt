package com.example.teachme.screens.main.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.teachme.StudentViewModel
import com.example.teachme.adapters.TeacherRvAdapter
import com.example.teachme.components.TeacherCalendar
import com.example.teachme.databinding.FragmentStudentHomeBinding
import com.example.teachme.models.LessonRequest
import com.example.teachme.models.LessonRequestStatus
import com.example.teachme.models.Student
import com.example.teachme.models.Teacher
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

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
        viewModel.userState.observe(viewLifecycleOwner) {
            val user = it.data as? Student ?: return@observe
            val adapter = binding.rvTeachers.adapter as? TeacherRvAdapter ?: return@observe
            adapter.setStudent(user)
        }
        viewModel.teachers.observe(viewLifecycleOwner) {
            val user = viewModel.userState.value?.data as? Student ?: return@observe
            binding.rvTeachers.adapter = TeacherRvAdapter(
                teachers = it,
                student = user,
                listener = object : TeacherRvAdapter.TeacherListener {
                    override fun onScheduleWithTeacher(teacher: Teacher) {
                        TeacherCalendar(
                            teacher,
                            listener = object : TeacherCalendar.ScheduleListener {
                                override fun schedule(subject: String, date: Long) {
                                    val student = viewModel.userState.value?.data ?: return


                                    val request = LessonRequest(
                                        studentId = FirebaseAuth.getInstance().uid!!,
                                        teacherId = teacher.id,
                                        subject = subject,
                                        date = date,
                                        status = LessonRequestStatus.Pending
                                    )

                                    viewModel.scheduleLessonRequest(teacher, request) {
                                        Snackbar.make(
                                            binding.root,
                                            "Sent lesson request to ${teacher.fullName}",
                                            Snackbar.LENGTH_LONG
                                        ).show()
                                    }

                                }
                            })
                            .show(childFragmentManager, "onScheduleWithTeacher")
                    }

                    override fun onRateTeacher(teacher: Teacher, rating: Double) {
                        viewModel.rateTeacher(teacher,rating) {
                            Snackbar.make(binding.root, "Rating submitted!", Snackbar.LENGTH_LONG).show()
                        }
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