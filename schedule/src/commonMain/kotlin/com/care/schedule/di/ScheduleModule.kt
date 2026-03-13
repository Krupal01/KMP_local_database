package com.care.schedule.di

import com.care.schedule.presentation.viewmodel.ScheduleViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val scheduleModule: Module = module {
    includes(schedulePlatformModule)

    factory {
        ScheduleViewModel(
            taskScheduler = get()
        )
    }
}