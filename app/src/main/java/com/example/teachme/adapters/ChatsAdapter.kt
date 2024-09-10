package com.example.teachme.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.teachme.databinding.ChatItemBinding
import com.example.teachme.models.ChatRoom
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class ChatsAdapter(
    private val chats: List<ChatRoom>,
    val listener: ChatListener,
) : RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder>() {


    interface ChatListener {
        fun enterChat(room: ChatRoom)
    }


    inner class ChatsViewHolder(val binding: ChatItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(room: ChatRoom) {
            val currentUserId = FirebaseAuth.getInstance().uid!!
            binding.chatItem.setOnClickListener {
                listener.enterChat(room)
            }
            binding.userNameTv.text = "Chat with ${if (currentUserId == room.studentId) {
                Picasso.get().load(room.teacher.image)
                    .into(binding.userPhoto)
                binding.userPhoto
                room.teacher.fullName
            } else {
                Picasso.get().load(room.student.image)
                    .into(binding.userPhoto)
                room.student.fullName
            }}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder {
        val binding = ChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatsViewHolder(binding)
    }

    override fun getItemCount(): Int = chats.size

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        val chat = chats[position]
        holder.bind(chat)
    }
}