package com.care.schedule.worker

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.care.schedule.service.ScheduleNotifier

class ScheduleWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        val scheduleId = inputData.getString(KEY_SCHEDULE_ID) ?: return Result.failure()
        val taskTitle = inputData.getString(KEY_TASK_TITLE) ?: return Result.failure()
        val subjectName = inputData.getString(KEY_SUBJECT_NAME)
        val priority = inputData.getString(KEY_PRIORITY) ?: "High"

        ScheduleNotifier(applicationContext).notify(
            scheduleId = scheduleId,
            taskTitle = taskTitle,
            subjectName = subjectName,
            priority = priority
        )

        return Result.success()
    }

    companion object {
        const val KEY_SCHEDULE_ID = "schedule_id"
        const val KEY_TASK_TITLE = "task_title"
        const val KEY_SUBJECT_NAME = "subject_name"
        const val KEY_PRIORITY = "priority"
    }
}