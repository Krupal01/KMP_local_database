package com.care.kmp.service

import kotlinx.browser.window

actual class PermissionManager {

    actual fun checkAndRequestPermissions() {

        requestNotificationPermission()
        requestMediaPermission()
    }

    private fun requestNotificationPermission() {

        val notification = js("Notification")

        if (notification != undefined) {

            notification.requestPermission().then { permission ->
                println("Notification permission: $permission")
            }

        } else {
            println("Notifications not supported in this browser")
        }
    }

    private fun requestMediaPermission() {

        val navigator = window.asDynamic().navigator
        val mediaDevices = navigator.mediaDevices

        if (mediaDevices != undefined) {

            mediaDevices.getUserMedia(
                js("{ video: true, audio: true }")
            ).then {
                println("Media permission granted")
            }.catch { error: dynamic ->
                println("Media permission denied: $error")
            }

        } else {
            println("Media devices not supported")
        }
    }
}