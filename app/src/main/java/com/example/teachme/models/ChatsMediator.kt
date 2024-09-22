package com.example.teachme.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.teachme.StudentViewModel
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ChatsMediator(
    val usersLiveData: LiveData<List<User>>,
    val chatsLiveData: LiveData<List<ChatRoom>>
) : ReadOnlyProperty<StudentViewModel, MediatorLiveData<List<ChatRoom>>> {

    private val mediator = MediatorLiveData<List<ChatRoom>>(listOf()).apply {
        addSource(usersLiveData) { users ->
            val prev = this.value
            this.postValue(prev?.map { it.populated(users) })
        }
        addSource(chatsLiveData) { chats ->
            val prev = this.value
            this.postValue(usersLiveData.value?.let { users -> chats.map { it.populated(users) } } ?: prev)
        }
    }

    override fun getValue(thisRef: StudentViewModel, property: KProperty<*>): MediatorLiveData<List<ChatRoom>> {
        return mediator
    }

}
