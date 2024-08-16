package com.example.teachme.components

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.DialogFragment
import com.example.teachme.databinding.FragmentTeacherCalendarDialogBinding
import com.example.teachme.models.Teacher

class TeacherCalendar(private val teacher: Teacher) : DialogFragment() {

    private var _binding: FragmentTeacherCalendarDialogBinding? = null
    private val binding: FragmentTeacherCalendarDialogBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTeacherCalendarDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.teacherCalendar.setOnDateChangeListener { p0, y, m, d ->
            println("${d},${m},${y}")
            renderAvailableHours()
        }
    }

    @SuppressLint("SetTextI18n")
    fun renderAvailableHours() {
        binding.hoursGrid.removeAllViews()
        for (i in 8..20) {
            val radioButton = RadioButton(requireContext())
            radioButton.text = "${
                if (i < 10) {
                    "0${i}"
                } else {
                    i
                }
            }:00"
            binding.hoursGrid.addView(radioButton)
        }

        for (i in 0 until binding.hoursGrid.childCount) {
            val view: View = binding.hoursGrid.getChildAt(i)
            if (view is RadioButton) {
                view.setOnClickListener { _: View? ->
                    // Uncheck all radio buttons
                    for (j in 0 until binding.hoursGrid.childCount) {
                        val child: View = binding.hoursGrid.getChildAt(j)
                        if (child is RadioButton) {
                            (child as RadioButton).isChecked = false
                        }
                    }
                    // Check the clicked radio button
                    view.isChecked = true
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}