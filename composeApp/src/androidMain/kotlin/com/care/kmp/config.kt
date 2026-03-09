package com.care.kmp

import android.app.Activity
import android.app.Application
import android.content.Context
import com.care.kmp.di.appModule
import com.care.kmp.service.PermissionManager
import database.AndroidDriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

fun initKoin(context: Context, activity: Activity) {
    startKoin {
        androidContext(context)
        modules(appModule(AndroidDriverFactory(context).createDriver(),
            PermissionManager(activity)
        ))
    }
}