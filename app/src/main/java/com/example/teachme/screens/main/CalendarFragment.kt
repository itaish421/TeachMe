package com.example.teachme.screens.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.teachme.StudentViewModel
import com.example.teachme.adapters.LessonRequestsRvAdapter
import com.example.teachme.databinding.FragmentCalendarBinding
import com.example.teachme.databinding.FragmentProfileBinding
import com.example.teachme.models.LessonRequest
import com.example.teachme.models.LessonRequestStatus
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

        viewModel.requests.observe(viewLifecycleOwner) { requests ->

            binding.rvRequests.adapter = LessonRequestsRvAdapter(
                if (requests.isNotEmpty()
                    && requests[0].teacherId == FirebaseAuth.getInstance().uid
                ) {
                    requests.filter { it.status == LessonRequestStatus.Approved }
                } else {
                    requests
                },
                object : LessonRequestsRvAdapter.LessonRequestsListener {
                    override fun approve(request: LessonRequest) {
                        TODO("Not yet implemented")
                    }

                    override fun decline(request: LessonRequest) {
                        TODO("Not yet implemented")
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