package com.example.teachme.models

class ChatRoom(
    val id: String = "",
    val student: Student = Student(),
    val teacher: Teacher = Teacher(),
    val studentId: String = "",
    val teacherId: String = "",
    val messages: MutableList<Message> = mutableListOf(),
)