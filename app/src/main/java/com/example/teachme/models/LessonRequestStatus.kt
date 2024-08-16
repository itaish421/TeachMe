package com.example.teachme.models

enum class LessonRequestStatus {
    Pending, Approved, Rejected;

    fun fromOrdinal(ordinal: Int): LessonRequestStatus {
        if (ordinal == 1) return Approved
        if (ordinal == 2) return Rejected
        return Pending
    }

    fun toInt(): Int = ordinal
}