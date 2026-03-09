package com.care.kmp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.care.kmp.presentation.navigation.AppNavHost
import com.care.kmp.presentation.viewmodel.UserIntent
import com.care.kmp.presentation.viewmodel.UserViewModel
import com.care.kmp.presentation.view.UserItem
import com.care.kmp.service.PermissionManager
import org.koin.compose.koinInject

@Composable
fun App() {

    val permissionManager: PermissionManager = koinInject()

    LaunchedEffect(Unit) {
        permissionManager.checkAndRequestPermissions()
    }


    MaterialTheme {
        AppNavHost()
    }
}