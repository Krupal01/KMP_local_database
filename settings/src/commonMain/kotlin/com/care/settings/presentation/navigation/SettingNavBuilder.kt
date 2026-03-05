package com.care.settings.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.care.settings.presentation.screen.SettingScreen

fun NavGraphBuilder.settingGraph(
    onNavigateBack: () -> Unit
) {
    composable<SettingRoute.Settings> { navBackStackEntry ->
        SettingScreen(
            onNavigateBack = onNavigateBack
        )
    }
}

