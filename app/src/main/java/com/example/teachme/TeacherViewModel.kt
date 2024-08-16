package com.example.teachme

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.teachme.models.User
import java.lang.Exception

class TeacherViewModel: ViewModel() {

    val loadingState = MutableLiveData<LoadingState>(LoadingState.Loaded)
    val exceptionsState = MutableLiveData<Exception>()
    val userState: LiveData<Resource<User>> = Database.userLiveData

    init {
        Database.startListening()
    }
    override fun onCleared() {
        super.onCleared()
        Database.stopListening()
    }

    fun logOut() {
       Database.logOut()
    }
}