package com.care.kmp.presentation.navigation

import com.care.kmp.domain.model.Todo
import kotlinx.serialization.Serializable

sealed interface Route {

    @Serializable
    data object TodoList : Route

    @Serializable
    data object AddTodo : Route

    @Serializable
    data object EditTodo : Route
}