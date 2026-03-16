package com.care.kmp.presentation.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.care.kmp.domain.model.Todo
import com.care.kmp.domain.model.toTodoModel
import com.care.kmp.presentation.screen.AddTodoScreen
import com.care.kmp.presentation.screen.MapScreen
import com.care.kmp.presentation.screen.TodoListScreen
import com.care.schedule.presentation.navigation.ScheduleRoute
import com.care.schedule.presentation.navigation.scheduleGraph
import com.care.settings.presentation.navigation.SettingRoute
import com.care.settings.presentation.navigation.settingGraph
import kotlinx.serialization.json.Json

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.TodoList,
        modifier = modifier
    ) {

        composable<Route.TodoList> { backStackEntry ->
            TodoListScreen(
                backStackEntry = backStackEntry,
                onNavigateToAdd = {
                    navController.navigate(Route.AddTodo)
                },
                onNavigateToEdit = { todo ->
                    val todoJson = Json.encodeToString(todo)
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("todo", todoJson)  // store as JSON string
                    navController.navigate(Route.EditTodo)
                },
                onNavigateToSettings = {
                    navController.navigate(SettingRoute.Settings)
                },
                onNavigateToSchedule = { todo ->
                    navController.navigate(
                        ScheduleRoute.Schedule(
                            encodedTodo = todo.toTodoModel().encode()
                        )
                    )
                },
                onNavigateToMap = {
                    navController.navigate(Route.Map)
                }
            )
        }

        composable<Route.AddTodo> {
            AddTodoScreen(
                onNavigateBack = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("todoAdded", true)
                    navController.popBackStack()
                }
            )
        }

        composable<Route.EditTodo> { backStackEntry ->
            val todoJson = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<String>("todo")  // retrieve as String

            val todo = todoJson?.let { Json.decodeFromString<Todo>(it) }
            AddTodoScreen(
                existingTodo = todo,
                onNavigateBack = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("todoAdded", true)
                    navController.popBackStack()
                }
            )
        }

        composable <Route.Map>{
            MapScreen()
        }

        settingGraph(
            onNavigateBack = { navController.popBackStack() }
        )

        scheduleGraph(
            onNavigateBack = { navController.popBackStack() }
        )
    }
}