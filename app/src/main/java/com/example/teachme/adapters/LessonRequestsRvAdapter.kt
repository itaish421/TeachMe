package com.example.teachme.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.teachme.databinding.LessonItemBinding
import com.example.teachme.models.LessonRequest
import com.example.teachme.models.LessonRequestPopulated
import com.example.teachme.models.LessonRequestStatus
import com.example.teachme.models.LessonRequestsData
import com.example.teachme.models.Teacher
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso




class LessonRequestsRvAdapter(
    private val data: LessonRequestsData,
    private val actions: LessonRequestActions,
) : RecyclerView.Adapter<LessonRequestsRvAdapter.LessonRequestViewHolder>() {

    val requests = data.requests.map {  it.populated(data.users)  }

    interface TeacherAction {
        fun approve(request: LessonRequest)
        fun decline(request: LessonRequest)
    }

    interface StudentAction {
        fun startChat(teacher: Teacher)
    }

    sealed class LessonRequestActions {
        data class TeacherActions(val listener: TeacherAction) : LessonRequestActions()
        data class StudentActions(val listener: StudentAction) : LessonRequestActions()
    }

    private val isTeacher: Boolean = if (requests.isEmpty()) {
        false
    } else {
        requests[0].teacherId == FirebaseAuth.getInstance().uid
    }

    inner class LessonRequestViewHolder(
        private val binding: LessonItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {


        private fun openPhone(phone: String) {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phone")
            }
            binding.root.context.startActivity(intent)
        }

        fun bind(request: LessonRequestPopulated, pos: Int) {

            binding.tvStatus.text = request.status.name
            binding.lessonSubjectTv.text = request.subject
            binding.lessonDateTv.text = formatDate(request.date)

            if (isTeacher) {
                val action = actions as LessonRequestActions.TeacherActions
                if (request.status == LessonRequestStatus.Pending) {
                    binding.controlsLayout.visibility = View.VISIBLE
                    binding.scheduleBtnApprove.setOnClickListener {
                        action.listener.approve(request)
                        notifyItemChanged(pos)
                    }
                    binding.scheduleBtnDecline.setOnClickListener {
                        action.listener.decline(request)
                        notifyItemChanged(pos)
                    }
                } else {
                    binding.controlsLayout.visibility = View.GONE
                    if (request.status == LessonRequestStatus.Approved) {
                        binding.controlsLayoutApproved.visibility = View.VISIBLE
                        binding.contactBtn.setOnClickListener {
                            openPhone(request.student.phone)
                        }
                    }
                }
                Picasso.get()
                    .load(request.student.image)
                    .into(binding.teacherImage)
                binding.lessonNameTv.text = request.student.fullName
            } else {
                val action = actions as LessonRequestActions.StudentActions
                binding.startChatWithTeacherBtn.setOnClickListener {
                    action.listener.startChat(
                        Teacher(
                            id = request.teacherId,
                            phone = request.teacher.phone,
                            fullName = request.teacher.fullName,
                            image = request.teacher.image
                        )
                    )
                }


                binding.startChatWithTeacherBtn.visibility = View.VISIBLE
                binding.controlsLayout.visibility = View.VISIBLE
                binding.scheduleBtnApprove.visibility = View.GONE
                binding.scheduleBtnDecline.visibility = View.GONE
                if (request.status == LessonRequestStatus.Approved) {
                    binding.controlsLayoutApproved.visibility = View.VISIBLE
                    binding.contactBtn.setOnClickListener {
                        openPhone(request.teacher.phone)
                    }
                }
                Picasso.get()
                    .load(request.teacher.image)
                    .into(binding.teacherImage)
                binding.lessonNameTv.text = request.teacher.fullName
            }
        }

        private fun formatDate(date: Long): String {
            return android.text.format.DateFormat.format("dd/MM/yyyy HH:00", date).toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonRequestViewHolder {
        val binding = LessonItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LessonRequestViewHolder(binding)
    }

    override fun getItemCount() = requests.size
    override fun onBindViewHolder(holder: LessonRequestViewHolder, position: Int) {
        val request = requests[position]
        holder.bind(request, position)
    }
}