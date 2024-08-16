package com.example.teachme

import android.provider.ContactsContract.Data
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teachme.models.RegisterForm
import com.example.teachme.models.User
import kotlinx.coroutines.launch
import java.lang.Exception

class AuthViewModel : ViewModel() {


    val loadingState = MutableLiveData<LoadingState>(LoadingState.Loaded)
    val exceptionsState = MutableLiveData<Exception>()
    val userState: LiveData<Resource<User>> = Database.userLiveData


    init {
        Database.startListening()
    }


    fun login(email:String, password: String, callback: () -> Unit) {
        viewModelScope.launch {
            try {
                loadingState.postValue(LoadingState.Loading)
                Database.login(email, password)
            } catch (e: Exception) {
                exceptionsState.postValue(e)
            } finally {
                loadingState.postValue(LoadingState.Loaded)
                callback()
            }
        }
    }

    fun register(form: RegisterForm, callback: () -> Unit) {
        viewModelScope.launch {
            try {
                loadingState.postValue(LoadingState.Loading)
                Database.register(form, viewModelScope)
            } catch (e: Exception) {
                exceptionsState.postValue(e)
            } finally {
                loadingState.postValue(LoadingState.Loaded)
                callback()
            }
        }
    }



    override fun onCleared() {
        super.onCleared()
        Database.stopListening()
    }

}