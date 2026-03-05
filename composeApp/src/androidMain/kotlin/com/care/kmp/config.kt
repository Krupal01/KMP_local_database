package com.care.kmp

import android.app.Application
import com.care.kmp.di.appModule
import database.AndroidDriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

fun initKoin(application: Application) {
    startKoin {
        androidContext(application)
        modules(appModule(AndroidDriverFactory(application).createDriver()))
    }
}