package com.care.schedule.service

import com.care.schedule.domain.model.ScheduleModel

expect class TaskScheduler {
    fun schedule(schedule: ScheduleModel)
    fun cancel(scheduleId: String)
}