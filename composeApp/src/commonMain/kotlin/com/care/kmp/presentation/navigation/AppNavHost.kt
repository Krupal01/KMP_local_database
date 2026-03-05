package com.care.kmp.presentation.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.care.kmp.presentation.screen.AddTodoScreen
import com.care.kmp.presentation.screen.TodoListScreen

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
    }
}