package com.example.teachme.models

class Student(
    id: String = "",
    fullName: String = "",
    email: String = "",
    image: String = "",
    var ratedTeachers: MutableList<String> = mutableListOf(),
    var outgoingRequests: MutableList<LessonRequest> = mutableListOf(),
) : User(id, fullName, email, image)