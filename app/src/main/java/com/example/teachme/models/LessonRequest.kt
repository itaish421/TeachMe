package com.example.teachme.models

class LessonRequest(
    var id: String = "",
    var studentId: String = "",
    var teacherId: String = "",
    var teacherImage: String = "",
    var studentImage: String = "",
    var teacherName : String = "",
    var studentName : String = "",
    var teacherPhone: String = "0502210923",
    var studentPhone: String = "0502210923",
    var date: Long = 0,
    var subject: String = "",
    var status: LessonRequestStatus = LessonRequestStatus.Pending,
)