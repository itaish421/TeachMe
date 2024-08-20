package com.example.teachme.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.teachme.databinding.LessonItemBinding
import com.example.teachme.models.LessonRequest
import com.example.teachme.models.LessonRequestStatus
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class LessonRequestsRvAdapter(
    private val requests: List<LessonRequest>,
    private val listener: LessonRequestsListener? = null,
) : RecyclerView.Adapter<LessonRequestsRvAdapter.LessonRequestViewHolder>() {
    interface LessonRequestsListener {
        fun approve(request: LessonRequest)
        fun decline(request: LessonRequest)
    }


    private val isTeacher: Boolean = if (requests.isEmpty()) {
        false
    } else {
        requests[0].teacherId == FirebaseAuth.getInstance().uid
    }

    inner class LessonRequestViewHolder(
        private val binding: LessonItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(request: LessonRequest, pos: Int) {

            binding.tvStatus.text = request.status.name
            binding.lessonSubjectTv.text = request.subject

            if (isTeacher) {
                if (request.status == LessonRequestStatus.Pending) {
                    binding.controlsLayout.visibility = View.VISIBLE
                    binding.scheduleBtnApprove.setOnClickListener {
                        listener?.approve(request)
                        notifyItemChanged(pos)
                    }
                    binding.scheduleBtnDecline.setOnClickListener {
                        listener?.decline(request)
                        notifyItemChanged(pos)
                    }
                }
                else {
                    binding.controlsLayout.visibility = View.GONE

                }
                Picasso.get()
                    .load(request.studentImage)
                    .into(binding.teacherImage)
                binding.lessonNameTv.text = request.studentName
            } else {
                binding.controlsLayout.visibility = View.GONE
                Picasso.get()
                    .load(request.teacherImage)
                    .into(binding.teacherImage)
                binding.lessonNameTv.text = request.teacherName
            }
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