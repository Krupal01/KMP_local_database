package com.care.settings.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface SettingRoute {

    @Serializable
    data object Settings : SettingRoute
}