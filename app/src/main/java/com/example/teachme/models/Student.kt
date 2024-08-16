package com.example.teachme.models

class Student(
    id: String = "",
    fullName: String = "",
    email: String = "",
    image: String = "",
    var outgoingRequests: List<LessonRequest> = listOf(),
) : User(id, fullName, email, image)