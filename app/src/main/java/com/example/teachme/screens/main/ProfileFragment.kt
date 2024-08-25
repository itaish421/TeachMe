package com.example.teachme.screens.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.teachme.StudentViewModel
import com.example.teachme.databinding.FragmentProfileBinding
import com.example.teachme.models.LessonRequestStatus
import com.example.teachme.models.Student
import com.example.teachme.models.Teacher
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding get() = _binding!!


    private val viewModel by activityViewModels<StudentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.userState.observe(viewLifecycleOwner) {
            val user = it.data ?: return@observe
            if (user is Student) {
                binding.tvSubjectsITeach.visibility = View.GONE
            } else {
                val teacher = user as Teacher
                binding.tvSubjectsITeach.text =
                    "Subjects i teach: ${teacher.teacherDetails.subjects.joinToString(", ")}"
            }

            binding.tvFullName.text = "Name: ${user.fullName}"
            Picasso.get()
                .load(user.image)
                .into(binding.ivProfile)




            binding.tvEmail.text = "Email: ${user.email}"
        }
        viewModel.requests.observe(viewLifecycleOwner) {
            val (upComingCount, approvedCount) = Pair(
                it.filter { lesson ->
                    lesson.status == LessonRequestStatus.Approved
                            && lesson.date > System.currentTimeMillis()
                }.size,
                it.filter { lesson -> lesson.status == LessonRequestStatus.Approved }.size
            )
            binding.tvUpcommingLessons.text = "Upcoming lessons: ${upComingCount}"
            binding.tvApprovedLessons.text = "Approved lessons: ${approvedCount}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}