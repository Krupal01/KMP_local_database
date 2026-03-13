package com.care.schedule.presentation.navigation

import com.care.schedule.domain.model.TodoModel
import kotlinx.serialization.Serializable

sealed interface ScheduleRoute {

    @Serializable
    data class Schedule(val encodedTodo: String) : ScheduleRoute
}