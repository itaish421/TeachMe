package com.example.teachme.models

class Teacher(
    id: String = "",
    fullName: String = "",
    email: String = "",
    phone: String = "",
    image: String = "",
    var ratings: MutableList<Double> = mutableListOf(),
    var teacherDetails: TeacherDetails = TeacherDetails(),
    var incomingRequests: MutableList<LessonRequest> = mutableListOf(),
) : User(
    id = id,
    fullName = fullName,
    phone = phone,
    email = email,
    image = image,
    isTeacher = true
)