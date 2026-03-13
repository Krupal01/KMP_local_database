package com.care.schedule.presentation.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.care.schedule.domain.model.TodoModel
import com.care.schedule.presentation.screen.ScheduleScreen

// ScheduleNavGraph.kt
fun NavGraphBuilder.scheduleGraph(
    onNavigateBack: () -> Unit
) {
    composable<ScheduleRoute.Schedule> { backStackEntry ->
        val route: ScheduleRoute.Schedule = backStackEntry.toRoute()
        val todo = remember(route.encodedTodo) {
            TodoModel.decode(route.encodedTodo)
        }
        ScheduleScreen(
            todo = todo,
            onNavigateBack = onNavigateBack
        )
    }
}

