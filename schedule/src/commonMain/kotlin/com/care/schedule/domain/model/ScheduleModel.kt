package com.care.schedule.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ScheduleModel(
    val scheduleId: String,
    val taskId: String,
    val taskTitle: String,
    val taskPriority: String,
    val subjectName: String?,
    val scheduledTimeMillis: Long,
    val durationMinutes: Int
)