package com.example.teachme.models

data class TeacherDetails(
    var pricePerHour: Double = 0.0,
    var subjects: List<String>,
    var description: String,
)