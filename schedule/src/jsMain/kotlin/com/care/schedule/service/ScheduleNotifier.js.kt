package com.care.schedule.service

import kotlinx.browser.window
import org.w3c.notifications.DEFAULT
import org.w3c.notifications.DENIED
import org.w3c.notifications.GRANTED
import org.w3c.notifications.Notification
import org.w3c.notifications.NotificationOptions
import org.w3c.notifications.NotificationPermission
import kotlin.js.json

actual class ScheduleNotifier {

    actual fun requestPermission() {
        Notification.requestPermission()
    }

    actual fun notify(
        scheduleId: String,
        taskTitle: String,
        subjectName: String?,
        priority: String
    ) {
        when (Notification.permission) {
            NotificationPermission.GRANTED -> {
                // permission already granted, show directly
                console.warn("Notification permission granted by user")
                showNotification(taskTitle, subjectName, priority)
            }
            NotificationPermission.DEFAULT -> {
                // not asked yet, ask first then show
                console.warn("Notification permission request to user")
                Notification.requestPermission { permission ->
                    if (permission == NotificationPermission.GRANTED) {
                        showNotification(taskTitle, subjectName, priority)
                    } else {
                        window.alert(taskTitle)
                    }
                }
            }
            NotificationPermission.DENIED -> {
                // user blocked, can't show
                console.warn("Notification permission denied by user")
            }
        }
    }

    private fun showNotification(
        taskTitle: String,
        subjectName: String?,
        priority: String
    ) {
        val body = "${subjectName ?: "General"} • $priority"
        val title = "Time to work: $taskTitle"
        val options = NotificationOptions(
            body = body
        )

        Notification(title,options)
    }
}