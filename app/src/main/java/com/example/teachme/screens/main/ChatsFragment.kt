package com.example.teachme.screens.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.teachme.StudentViewModel
import com.example.teachme.adapters.ChatsAdapter
import com.example.teachme.databinding.FragmentChatsBinding
import com.example.teachme.models.ChatRoom

class ChatsFragment : Fragment() {

    private var _binding: FragmentChatsBinding? = null
    private val binding: FragmentChatsBinding get() = _binding!!

    private val viewModel by activityViewModels<StudentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var adapter: ChatsAdapter? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.chatsMediator.observe(viewLifecycleOwner) {
            adapter = ChatsAdapter(it, object : ChatsAdapter.ChatListener {
                override fun enterChat(room: ChatRoom) {
                    val act = ChatsFragmentDirections.actionChatsFragmentToChatFragment(room.id)
                    findNavController().navigate(act)
                }
            })
            binding.rvChats.adapter = adapter
        }

    }


    override fun onResume() {
        super.onResume()
        viewModel.invokeGetChats()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}