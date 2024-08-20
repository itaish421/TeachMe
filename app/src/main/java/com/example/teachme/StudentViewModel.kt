package com.example.teachme

import android.provider.ContactsContract.Data
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teachme.models.LessonRequest
import com.example.teachme.models.LessonRequestStatus
import com.example.teachme.models.Student
import com.example.teachme.models.Teacher
import com.example.teachme.models.User
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

    init {
        Database.startListening()
        Database.getTeachersBySubject(
            subject = "all",
            teachersLiveData = _teachers,
            exceptionLiveData = exceptionsState
        )
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