package com.example.teachme.adapters

import android.content.res.Resources.Theme
import android.graphics.Color
import android.util.LayoutDirection.RTL
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.teachme.R
import com.example.teachme.databinding.MessageItemBinding
import com.example.teachme.models.ChatRoom
import com.example.teachme.models.Message
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar

class MessagesAdapter(
    private val chatRoom: ChatRoom,
) : RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {

    private val c: Calendar = Calendar.getInstance()
    fun getTime(date: Long): String {
        c.timeInMillis = date
        return "${c.get(Calendar.HOUR_OF_DAY)}:${c.get(Calendar.MINUTE)}"
    }
    inner class MessageViewHolder(
        val binding: MessageItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindMessage(message: Message) {
            val senderName = if(message.sender == chatRoom.studentId) {
                chatRoom.student.fullName
            }
            else {
                chatRoom.teacher.fullName
            }
            binding.nameTv.text = "${senderName}: "
            if(message.sender == FirebaseAuth.getInstance().uid) {
                binding.root.layoutDirection = View.LAYOUT_DIRECTION_RTL
            }
            else {
                binding.root.layoutDirection = View.LAYOUT_DIRECTION_LTR
                binding.layout.setBackground( ResourcesCompat.getDrawable(binding.root.context.resources, R.drawable.rect_other,null))
            }
            binding.message.text = message.content
            binding.time.text = getTime(message.date)
        }
    }

    fun refresh() {
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = MessageItemBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return MessageViewHolder(binding)
    }

    override fun getItemCount(): Int = chatRoom.messages.size

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = chatRoom.messages[position]
        holder.bindMessage(message)
    }


}