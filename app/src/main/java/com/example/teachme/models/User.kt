package com.example.teachme.models

open class User(
    var id: String = "",
    var fullName: String = "",
    var email: String = "",
    var image: String = "",
    var isTeacher: Boolean = false
)