package com.example.teachme.models

class LessonRequest(
    var studentId: String,
    var teacherId: String,
    var date: Long,
    var subject: String,
    var status: LessonRequestStatus,
)