package com.care.kmp.service

import com.google.firebase.messaging.FirebaseMessaging

actual class PushManager actual constructor() {
    actual fun getToken() : String {
        var token = ""
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener {
                token = it
            }
        return token
    }
}