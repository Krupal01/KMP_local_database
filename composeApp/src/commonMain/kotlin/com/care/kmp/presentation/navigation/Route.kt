package com.care.kmp.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {

    @Serializable
    data object TodoList : Route

    @Serializable
    data object AddTodo : Route
}