package com.care.kmp

import android.app.Application

class TodoApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(this)
    }
}