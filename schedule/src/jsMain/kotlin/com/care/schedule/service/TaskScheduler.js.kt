package com.care.schedule.service

import com.care.schedule.domain.model.ScheduleModel
import kotlinx.browser.window
import kotlin.js.Date

actual class TaskScheduler {

    // store timeout ids to cancel later
    private val scheduledJobs = mutableMapOf<String, Int>()

    actual fun schedule(schedule: ScheduleModel) {
        val delay = schedule.scheduledTimeMillis - Date().getTime().toLong()
        if (delay <= 0) return

        val timeoutId = window.setTimeout(
            handler = {
                ScheduleNotifier().notify(
                    scheduleId = schedule.scheduleId,
                    taskTitle = schedule.taskTitle,
                    subjectName = schedule.subjectName,
                    priority = schedule.taskPriority
                )
            },
            timeout = delay.toInt()
        )

        scheduledJobs[schedule.scheduleId] = timeoutId
    }

    actual fun cancel(scheduleId: String) {
        scheduledJobs[scheduleId]?.let { timeoutId ->
            window.clearTimeout(timeoutId)
            scheduledJobs.remove(scheduleId)
        }
    }
}