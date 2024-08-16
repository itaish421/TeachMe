package com.example.teachme

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.teachme.models.Teacher
import com.example.teachme.models.User
import java.lang.Exception

class StudentViewModel : ViewModel() {

    val loadingState = MutableLiveData<LoadingState>(LoadingState.Loaded)
    val exceptionsState = MutableLiveData<Exception>()
    val userState: LiveData<Resource<User>> = Database.userLiveData

    private val _teachers = MutableLiveData<List<Teacher>>(listOf())
    val teachers: LiveData<List<Teacher>> get() = _teachers

    var hasGraph: Boolean = false
    init {
        Database.startListening()
        Database.getTeachersBySubject(
            subject = "all",
            teachersLiveData = _teachers,
            exceptionLiveData = exceptionsState
        )
    }

    fun getTeachersBySubject(subject: String) {
        Database.getTeachersBySubject(
            subject = subject,
            teachersLiveData = _teachers,
            exceptionLiveData = exceptionsState
        )
    }

    override fun onCleared() {
        super.onCleared()
        Database.stopListening()
    }

    fun logOut() {
        Database.logOut()
    }
}