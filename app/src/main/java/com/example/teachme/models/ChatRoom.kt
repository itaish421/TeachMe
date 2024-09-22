package com.example.teachme.models

open class ChatRoom(
    val id: String = "",
    val studentId: String = "",
    val teacherId: String = "",
    val messages: MutableList<Message> = mutableListOf(),
) {
    fun populated(userList: List<User>) : ChatRoomPopulated {
        val teacher = userList.find { it.id == teacherId } as Teacher
        val student = userList.find { it.id == studentId } as Student
        return ChatRoomPopulated(
            id, studentId, teacherId, messages,
            teacher, student
        )
    }
}

class ChatRoomPopulated(
     id: String = "",
     studentId: String = "",
     teacherId: String = "",
     messages: MutableList<Message> = mutableListOf(),
     val teacher: Teacher = Teacher(),
     val student: Student = Student()
) : ChatRoom(id, studentId, teacherId, messages) {

}