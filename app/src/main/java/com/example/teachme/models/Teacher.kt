package com.example.teachme.models

class Teacher(
    id: String = "",
    fullName: String = "",
    email: String = "",
    image: String = "",
    var teacherDetails: TeacherDetails = TeacherDetails(),
    var incomingRequests: MutableList<LessonRequest> = mutableListOf(),
) : User(id, fullName, email, image, isTeacher = true)