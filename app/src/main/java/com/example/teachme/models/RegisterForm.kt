package com.example.teachme.models

import android.net.Uri

class RegisterForm(
    var teacher: Boolean,
    var email: String,
    var password: String,
    var fullName: String,
    var image: Uri?,
    var phone: String,
    var teacherDetails: TeacherDetails? = null,
)