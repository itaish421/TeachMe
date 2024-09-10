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
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.timerTask

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


    val vowList = listOf(
        "inspire and empower each student to reach their full potential.",
        "create a supportive and engaging learning environment.",
        "provide personalized attention tailored to each learner's needs.",
        "foster a love for learning through innovative and interactive methods.",
        "stay updated with the latest educational practices and technologies.",
        "encourage critical thinking and problem-solving skills.",
        "uphold the highest standards of integrity and professionalism.",
        "celebrate each student’s unique strengths and achievements.",
        "build strong, positive relationships with students and their families.",
        "continuously strive for excellence in education and personal development."
    )
    private var vowsTimer: Timer? = null
    override fun onPause() {
        super.onPause()
        vowsTimer?.cancel()
    }
    override fun onResume() {
        super.onResume()
        vowsTimer?.cancel()
        vowsTimer = Timer()
        vowsTimer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                requireActivity().runOnUiThread {
                    binding.vows.text = vowList.random()
                }
            }
        }, 0, 4000)
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
            binding.hiTv.text = "Hi ${user.fullName}"
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