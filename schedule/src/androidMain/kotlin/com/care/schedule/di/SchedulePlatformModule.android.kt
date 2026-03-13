package com.care.schedule.di

import com.care.schedule.service.ScheduleNotifier
import com.care.schedule.service.TaskScheduler
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

// androidMain/di/SchedulePlatformModule.kt
actual val schedulePlatformModule: Module = module {
    single { TaskScheduler(androidContext()) }
    single { ScheduleNotifier(androidContext()) }
}