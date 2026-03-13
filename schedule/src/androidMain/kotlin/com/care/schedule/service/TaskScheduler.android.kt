package com.care.schedule.service

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.care.schedule.domain.model.ScheduleModel
import com.care.schedule.worker.ScheduleWorker
import java.lang.System.currentTimeMillis
import java.util.concurrent.TimeUnit

actual class TaskScheduler(val context: Context) {
    actual fun schedule(schedule: ScheduleModel) {
        val delay = schedule.scheduledTimeMillis - currentTimeMillis()
        if (delay <= 0) return

        val inputData = workDataOf(
            ScheduleWorker.KEY_SCHEDULE_ID to schedule.scheduleId,
            ScheduleWorker.KEY_TASK_TITLE to schedule.taskTitle,
            ScheduleWorker.KEY_SUBJECT_NAME to schedule.subjectName,
            ScheduleWorker.KEY_PRIORITY to schedule.taskPriority
        )

        val workRequest = OneTimeWorkRequestBuilder<ScheduleWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag(schedule.scheduleId)
            .build()

        WorkManager.getInstance(context = context)
            .enqueueUniqueWork(
                schedule.scheduleId,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
    }

    actual fun cancel(scheduleId: String) {
        WorkManager.getInstance(context)
            .cancelAllWorkByTag(scheduleId)
    }
}