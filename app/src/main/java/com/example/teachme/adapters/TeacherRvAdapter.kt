package com.example.teachme.adapters

import android.app.AlertDialog
import android.graphics.Color
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.recyclerview.widget.RecyclerView
import com.example.teachme.R
import com.example.teachme.databinding.TeacherItemBinding
import com.example.teachme.models.Student
import com.example.teachme.models.Teacher
import com.squareup.picasso.Picasso

class TeacherRvAdapter(
    private var student: Student? = null,
    private val teachers: List<Teacher>,
    private val listener: TeacherListener,
) :
    RecyclerView.Adapter<TeacherRvAdapter.TeacherViewHolder>() {


    fun setStudent(student: Student) {
        this.student = student
        notifyDataSetChanged()
    }

    interface TeacherListener {
        fun onScheduleWithTeacher(teacher: Teacher)
        fun onRateTeacher(teacher: Teacher, rating: Double)
    }

    inner class TeacherViewHolder(private val binding: TeacherItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(teacher: Teacher, student: Student?) {
            Picasso.get()
                .load(teacher.image)
                .into(binding.teacherImage)

            val ratingAvg = teacher.ratings.average()
            binding.teacherRatingBar.rating = ratingAvg.toFloat()
            binding.teacherNameTv.text = teacher.fullName
            binding.teacherSubjectsTv.text = teacher.teacherDetails.subjects.joinToString(",")
            binding.teacherDescTv.text = teacher.teacherDetails.description


            binding.scheduleBtn.setOnClickListener {
                listener.onScheduleWithTeacher(teacher)
            }
            student?.let {
                if (it.ratedTeachers.contains(teacher.id)) {
                    binding.rateThisTeacher.text = "You have rated this teacher"
                    binding.rateThisTeacher.setBackgroundColor(Color.GRAY)
                    binding.rateThisTeacher.isEnabled = false
                } else {
                    binding.rateThisTeacher.setOnClickListener {
                        val view = LayoutInflater.from(binding.root.context)
                            .inflate(R.layout.rate_layout, null, false)
                        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)
                        AlertDialog.Builder(binding.root.context)
                            .setView(view)
                            .setPositiveButton("Submit rating") { p0, p1 ->
                                val rating = ratingBar.rating.toDouble()
                                listener.onRateTeacher(teacher, rating)
                            }
                            .setNegativeButton("Cancel", null)
                            .show()
                    }
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherViewHolder {
        val binding = TeacherItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TeacherViewHolder(binding)
    }

    override fun getItemCount(): Int = teachers.size
    override fun onBindViewHolder(holder: TeacherViewHolder, position: Int) {
        val teacher = teachers[position]
        holder.bind(teacher, student)

    }
}