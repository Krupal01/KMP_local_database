package com.care.kmp.presentation.view

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.care.kmp.presentation.navigation.BottomNavItem
import com.care.kmp.presentation.navigation.Route

@Composable
fun AppBottomBar(
    clickOnTab: (BottomNavItem) -> Unit,
    currentRoute: Route,
    items: List<BottomNavItem> = listOf(
        BottomNavItem.Todo,
        BottomNavItem.Map
    )
) {

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    clickOnTab(item)
                },
                label = { Text(item.label) },
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                    )
                }
            )
        }
    }
}