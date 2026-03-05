package com.care.kmp.presentation.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.care.kmp.domain.model.Todo
import com.care.kmp.presentation.screen.AddTodoScreen
import com.care.kmp.presentation.screen.TodoListScreen
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
    }
}