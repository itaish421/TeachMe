package com.example.teachme.models

class Teacher(
    id: String = "",
    fullName: String = "",
    email: String = "",
    image: String = "",
    var teacherDetails: TeacherDetails = TeacherDetails(),
    var incomingRequests: List<LessonRequest> = listOf(),
) : User(id, fullName, email, image, isTeacher = true)