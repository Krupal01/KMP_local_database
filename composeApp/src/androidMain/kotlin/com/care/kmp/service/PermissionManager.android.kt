package com.care.kmp.service

import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.core.app.ActivityCompat

actual class PermissionManager(private val activity: Activity) {

    actual fun checkAndRequestPermissions() {

        val permissions = mutableListOf<String>()

        // Notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        // Media permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        ActivityCompat.requestPermissions(
            activity,
            permissions.toTypedArray(),
            1001
        )
    }
}