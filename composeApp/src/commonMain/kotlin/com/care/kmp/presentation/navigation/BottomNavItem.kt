package com.care.kmp.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Map
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: Route,
    val label: String,
    val icon: ImageVector
) {
    object Todo : BottomNavItem(Route.TodoList, "Todo", Icons.Filled.Checklist)
    object Map : BottomNavItem(Route.Map, "Map", Icons.Default.Map)
}