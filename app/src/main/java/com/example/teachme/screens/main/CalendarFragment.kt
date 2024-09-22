package com.example.teachme.screens.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.teachme.R
import com.example.teachme.StudentViewModel
import com.example.teachme.adapters.LessonRequestsRvAdapter
import com.example.teachme.databinding.FragmentCalendarBinding
import com.example.teachme.databinding.FragmentProfileBinding
import com.example.teachme.models.LessonRequest
import com.example.teachme.models.LessonRequestStatus
import com.example.teachme.models.Teacher
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding: FragmentCalendarBinding get() = _binding!!

    private val viewModel by activityViewModels<StudentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.requestsMediator.observe(viewLifecycleOwner) { requestsCopy ->
            val requests = requestsCopy.copy()
            val currentUser = viewModel.userState.value?.data ?: return@observe
            if(requests.requests.isEmpty()) {
                binding.noLessonsLayout.visibility = View.VISIBLE
                binding.exploreTeachers.setOnClickListener {
                    findNavController().popBackStack()
                }
            }
            binding.rvRequests.adapter = LessonRequestsRvAdapter(
                if (requests.requests.isNotEmpty()
                    && requests.requests[0].teacherId == FirebaseAuth.getInstance().uid
                ) {
                    requests.requests =
                    requests.requests.filter { it.status == LessonRequestStatus.Approved }
                    requests
                } else {
                    requests
                },
                if(currentUser.isTeacher) {
                    LessonRequestsRvAdapter.LessonRequestActions.TeacherActions(
                        object : LessonRequestsRvAdapter.TeacherAction {
                            override fun approve(request: LessonRequest) {
                                viewModel.changeRequestStatus(request, LessonRequestStatus.Approved) {
                                    Snackbar.make(binding.root, "Request approved", Snackbar.LENGTH_LONG)
                                        .show()
                                }
                            }

                            override fun decline(request: LessonRequest) {
                                viewModel.changeRequestStatus(request, LessonRequestStatus.Rejected) {
                                    Snackbar.make(binding.root, "Request declined", Snackbar.LENGTH_LONG)
                                        .show()
                                }
                            }
                        }
                    )
                }
                else {
                    LessonRequestsRvAdapter.LessonRequestActions.StudentActions(
                        object : LessonRequestsRvAdapter.StudentAction {
                            override fun startChat(teacher: Teacher) {
                                viewModel.startChatWithTeacher(teacher) {
                                    val act = CalendarFragmentDirections.actionCalendarFragmentToChatFragment(it.id)
                                    findNavController().navigate(act)
                                }
                            }
                        }
                    )
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}