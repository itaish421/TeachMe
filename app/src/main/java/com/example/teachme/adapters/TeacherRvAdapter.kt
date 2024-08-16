package com.example.teachme.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.teachme.databinding.TeacherItemBinding
import com.example.teachme.models.Teacher
import com.squareup.picasso.Picasso

class TeacherRvAdapter(
    private val teachers: List<Teacher>,
    private val listener: TeacherListener,
) :
    RecyclerView.Adapter<TeacherRvAdapter.TeacherViewHolder>() {

    interface TeacherListener {
        fun onScheduleWithTeacher(teacher: Teacher)
    }

    inner class TeacherViewHolder(private val binding: TeacherItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(teacher: Teacher) {
            Picasso.get()
                .load(teacher.image)
                .into(binding.teacherImage)

            binding.teacherNameTv.text = teacher.fullName
            binding.teacherSubjectsTv.text = teacher.teacherDetails.subjects.joinToString(",")
            binding.teacherDescTv.text = teacher.teacherDetails.description

            binding.teacherRatingBar.rating = teacher.teacherDetails.rating.toFloat()

            binding.scheduleBtn.setOnClickListener {
                listener.onScheduleWithTeacher(teacher)
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
        holder.bind(teacher)
    }
}