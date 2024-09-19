package com.example.teachme.components

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.view.allViews
import androidx.fragment.app.DialogFragment
import com.example.teachme.databinding.FragmentTeacherCalendarDialogBinding
import com.example.teachme.models.LessonRequestStatus
import com.example.teachme.models.Teacher
import java.util.Calendar
import java.util.Date

class TeacherCalendar(
    private val teacher: Teacher,
    private val listener: ScheduleListener,
) : DialogFragment() {

    interface ScheduleListener {
        fun schedule(
            subject: String,
            date: Long,
        )
    }

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

    private var d: Int = -1
    private var m: Int = -1
    private var y: Int = -1
    private var hour: Int? = null


    private fun getSelectedHour(): Int? {
        val selectedRadioGroupButton =
            binding.hoursGrid.allViews
                .find {
                    if (it is RadioButton) {
                        return@find it.isChecked
                    } else {
                        return@find false
                    }
                } as RadioButton? ?: return null
        val hour = selectedRadioGroupButton.text.toString()
            .split(":")[0]
        if (hour[0] == '0')
            return hour[1].toString().toInt()
        return hour.toInt()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.subjectSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            teacher.teacherDetails.subjects
        )
        binding.teacherCalendar.minDate = System.currentTimeMillis()
        binding.teacherCalendar.setOnDateChangeListener { p0, y, m, d ->
            println("${d},${m},${y}")
            this.d = d
            this.m = m
            this.y = y
            renderAvailableHours()
        }

        binding.scheduleBtnCancel.setOnClickListener {
            dismiss()
        }
        binding.scheduleBtnSubmit.setOnClickListener {
            val subject =
                teacher.teacherDetails.subjects[binding.subjectSpinner.selectedItemPosition]
            if (m == -1 || y == -1 || d == -1) {
                Toast.makeText(requireContext(), "Please choose a date", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            hour = getSelectedHour()
            if (hour == null) {
                Toast.makeText(requireContext(), "Please choose a hour", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_MONTH, d)
            calendar.set(Calendar.MONTH, m)
            calendar.set(Calendar.YEAR, y)
            calendar.set(Calendar.HOUR_OF_DAY, hour!!)
            listener.schedule(
                subject = subject,
                date = calendar.timeInMillis
            )
            dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    fun renderAvailableHours() {
        binding.hoursGrid.removeAllViews()

        val date = Calendar.getInstance()
        date.set(Calendar.DAY_OF_MONTH, d)
        date.set(Calendar.MONTH, m)
        date.set(Calendar.YEAR, y)

        val unavailableDates = teacher.incomingRequests
            .filter {
                it.status != LessonRequestStatus.Rejected
            }
            .map {
                it.date
            }
            .toSet()

        val c = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
        }
        val h = c.get(Calendar.HOUR_OF_DAY)
        for (i in 8..20) {
            val radioButton = RadioButton(requireContext())

            date.set(Calendar.HOUR_OF_DAY, i)
            if ((h >= i && itsToday()) || unavailableDates.any {
                    val c = Calendar.getInstance()
                    c.timeInMillis = it
                    return@any c.get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH)
                            && c.get(Calendar.YEAR) == date.get(Calendar.YEAR)
                            && c.get(Calendar.MONTH) == date.get(Calendar.MONTH)
                            && c.get(Calendar.HOUR_OF_DAY) == date.get(Calendar.HOUR_OF_DAY)
                }) {
                radioButton.isEnabled = false
            }
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

    private fun itsToday(): Boolean {
        val today = Calendar.getInstance()
        return d == today.get(Calendar.DAY_OF_MONTH)
                && y == today.get(Calendar.YEAR)
                && m == today.get(Calendar.MONTH)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}