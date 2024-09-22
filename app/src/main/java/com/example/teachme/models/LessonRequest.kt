package com.example.teachme.models



class LessonRequestsData(
    var requests: List<LessonRequest> = listOf(),
    var users: List<User> = listOf()
) {
    fun copy(): LessonRequestsData = LessonRequestsData(ArrayList(requests), ArrayList(users))
}

open class LessonRequest(
    var id: String = "",
    var studentId: String = "",
    var teacherId: String = "",
    var date: Long = 0,
    var subject: String = "",
    var status: LessonRequestStatus = LessonRequestStatus.Pending,
) {
        fun populated(userList: List<User>) : LessonRequestPopulated {
            val teacher = userList.find { it.id == teacherId } as Teacher
            val student = userList.find { it.id == studentId } as Student
            return LessonRequestPopulated(
                id, studentId, teacherId, date, subject, status,
                teacher, student
            )
    }
}

class LessonRequestPopulated(
     id: String = "",
     studentId: String = "",
     teacherId: String = "",
     date: Long = 0,
     subject: String = "",
     status: LessonRequestStatus = LessonRequestStatus.Pending,
     val teacher: Teacher,
     val student: Student
) :LessonRequest(
    id, studentId, teacherId, date, subject, status
)