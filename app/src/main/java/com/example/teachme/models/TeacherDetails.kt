package com.example.teachme.models

data class TeacherDetails(
    var pricePerHour: Double = 0.0,
    var subjects: List<String> = listOf(),
    var description: String = "",
    var rating: Double = 0.0,
    var ratingStudents: List<String> = listOf(), // ids of students that rated this teacher
)