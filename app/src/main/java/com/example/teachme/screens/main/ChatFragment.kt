package com.example.teachme.screens.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.teachme.R
import com.example.teachme.StudentViewModel
import com.example.teachme.adapters.ChatsAdapter
import com.example.teachme.adapters.MessagesAdapter
import com.example.teachme.databinding.FragmentChatBinding
import com.example.teachme.databinding.FragmentChatsBinding
import com.example.teachme.models.ChatRoom
import com.example.teachme.models.ChatRoomPopulated
import com.example.teachme.models.Student
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding: FragmentChatBinding get() = _binding!!

    private val viewModel by activityViewModels<StudentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val args by navArgs<ChatFragmentArgs>()
    private var currChatListener : ValueEventListener? = null
    private var adapter: MessagesAdapter? = null


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString()
            if(message.isNotEmpty()) {
                viewModel.sendMessageToChat(message)
                binding.etMessage.setText("")
            }
        }
        binding.allChats.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.currChat.observe(viewLifecycleOwner) {
            (it as? ChatRoomPopulated)?.let { room ->
                adapter = MessagesAdapter(room)
                Log.d("ChangedRoom", room.messages.size.toString())
                binding.rvMessages.adapter = adapter
                binding.rvMessages.scrollToPosition(room.messages.size-1)
                val user = viewModel.userState.value?.data ?: return@observe
                if(user is Student) {
                    binding.topLayout.text = "Chat with ${room.teacher.fullName}"
                }
                else {
                    binding.topLayout.text = "Chat with ${room.student.fullName}"
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        val chatID = args.chatId
        currChatListener = viewModel.listenCurrChat(chatID)
    }

    override fun onPause() {
        super.onPause()
        val chatID = args.chatId
        currChatListener?.let {
            FirebaseDatabase.getInstance().getReference("chats")
                .child(chatID)
                .removeEventListener(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}