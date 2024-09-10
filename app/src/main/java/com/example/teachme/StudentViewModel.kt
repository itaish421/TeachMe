package com.example.teachme

import android.provider.ContactsContract.Data
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teachme.models.ChatRoom
import com.example.teachme.models.LessonRequest
import com.example.teachme.models.LessonRequestStatus
import com.example.teachme.models.Student
import com.example.teachme.models.Teacher
import com.example.teachme.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch

class StudentViewModel : ViewModel() {

    val loadingState = MutableLiveData<LoadingState>(LoadingState.Loaded)
    val exceptionsState = MutableLiveData<Exception>()
    val userState: LiveData<Resource<User>> = Database.userLiveData

    private val _teachers = MutableLiveData<List<Teacher>>(listOf())
    val teachers: LiveData<List<Teacher>> get() = _teachers

    var hasGraph: Boolean = false

    private var requestListener: ListenerRegistration? = null
    private val _requests = MutableLiveData<List<LessonRequest>>(listOf())
    val requests: LiveData<List<LessonRequest>> get() = _requests

    private val _myChats: MutableLiveData<List<ChatRoom>> = MutableLiveData()
    val myChats: LiveData<List<ChatRoom>> get() = _myChats
    private val _currChat: MutableLiveData<ChatRoom?> = MutableLiveData()
    val currChat: LiveData<ChatRoom?> get() = _currChat
    var currChatListener: ValueEventListener? = null

    init {
        Database.startListening()
        Database.getTeachersBySubject(
            subject = "all",
            teachersLiveData = _teachers,
            exceptionLiveData = exceptionsState
        )
    }

    fun sendMessageToChat(content: String) {
        val senderId = FirebaseAuth.getInstance().uid ?: return
        val currentRoom = currChat.value ?: return
        Database.sendMessageToChat(currentRoom, senderId, content)
    }

    fun listenCurrChat(chatId: String): ValueEventListener {
        currChatListener?.let {
            FirebaseDatabase.getInstance().getReference("chats")
                .child(chatId)
                .removeEventListener(it)
        }
        currChatListener = Database.listenToChat(chatId) {
            _currChat.postValue(it)
        }
        return currChatListener!!
    }

    fun invokeGetChats() {
        viewModelScope.launch {
            try {
                val chats = Database.getChats()
                _myChats.postValue(chats)
            } catch (e: Exception) {
                exceptionsState.postValue(e)
            }

        }
    }


    fun startChatWithTeacher(teacher: Teacher, callback: (ChatRoom) -> Unit) {
        val student = userState.value?.data as? Student ?: return
        viewModelScope.launch {
            try {
                val room = Database.startChatWithTeacher(teacher, student)
                callback.invoke(room)
            } catch (e: Exception) {
                exceptionsState.postValue(e)
            }
        }
    }

    fun changeRequestStatus(
        request: LessonRequest,
        status: LessonRequestStatus,
        callback: () -> Unit,
    ) {
        val user = userState.value?.data as? Teacher ?: return
        viewModelScope.launch {

            try {
                loadingState.postValue(LoadingState.Loading)
                Database.changeRequestStatus(user, request, status)
            } catch (e: Exception) {
                exceptionsState.postValue(e)
            } finally {
                loadingState.postValue(LoadingState.Loaded)
                callback()
            }
        }
    }


    fun rateTeacher(teacher: Teacher, rating: Double, callback: () -> Unit) {
        val user = userState.value?.data as? Student ?: return
        viewModelScope.launch {
            try {
                Database.rateTeacher(teacher = teacher, student = user, rating = rating)
            } catch (e: Exception) {
                exceptionsState.postValue(e)
            } finally {
                callback()
            }
        }
    }

    fun getMyRequests() {
        if (requestListener != null) return
        val user = userState.value?.data ?: return
        requestListener = Database.listenMyRequests(
            user = user,
            _requests
        )
    }

    fun getTeachersBySubject(subject: String) {
        Database.getTeachersBySubject(
            subject = subject,
            teachersLiveData = _teachers,
            exceptionLiveData = exceptionsState
        )
    }


    fun scheduleLessonRequest(
        teacher: Teacher,
        request: LessonRequest,
        callback: () -> Unit,
    ) {
        viewModelScope.launch {
            try {
                loadingState.postValue(LoadingState.Loading)
                Database.scheduleLessonRequest(teacher, request)
            } catch (e: Exception) {
                exceptionsState.postValue(e)
            } finally {
                loadingState.postValue(LoadingState.Loaded)
                callback.invoke()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Database.stopListening()
        requestListener?.remove()
    }

    fun logOut() {
        Database.logOut()
    }
}