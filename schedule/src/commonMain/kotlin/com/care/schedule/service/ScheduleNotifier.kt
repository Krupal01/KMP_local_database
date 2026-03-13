package com.care.schedule.service

expect class ScheduleNotifier {
    fun notify(
        scheduleId: String,
        taskTitle: String,
        subjectName: String?,
        priority: String
    )
    fun requestPermission()
}