package com.example.teachme.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.teachme.StudentViewModel
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class LessonRequestsMediator(
    val usersLiveData: LiveData<List<User>>,
    val requestsLiveData: LiveData<List<LessonRequest>>
) : ReadOnlyProperty<StudentViewModel, MediatorLiveData<LessonRequestsData>> {

    private val mediator = MediatorLiveData(LessonRequestsData()).apply {

        addSource(usersLiveData) { users ->
            val prev = this.value
            prev?.users = users
            this.postValue(prev)
        }
        addSource(requestsLiveData) { requests ->
            val prev = this.value
            prev?.requests = requests
            this.postValue(prev)
        }
    }

    override fun getValue(thisRef: StudentViewModel, property: KProperty<*>): MediatorLiveData<LessonRequestsData> {
        return mediator
    }

}
