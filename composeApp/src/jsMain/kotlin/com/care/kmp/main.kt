package com.care.kmp

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.lifecycle.viewmodel.compose.viewModel
import app.cash.sqldelight.db.SqlDriver
import com.care.kmp.data.local.LocalDatabase
import com.care.kmp.data.network.ApiService
import com.care.kmp.di.appModule
import com.care.kmp.presentation.viewmodel.UserViewModel
import com.care.kmp.presentation.viewmodel.UserViewModelFactory
import com.care.kmp.service.PermissionManager
import com.care.schedule.di.scheduleModule
import database.JsDriverFactory
import kotlinx.browser.document
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.core.context.startKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {

    val scope = MainScope()

    scope.launch {
        val driver = JsDriverFactory().provideDbDriver(AppDatabase.Schema)

        initKoin(driver)

        ComposeViewport(document.body!!) {
            App()
        }
    }
}

fun initKoin(driver: SqlDriver) {
    startKoin {
        modules(
            appModule(driver, permissionManager = PermissionManager()),
            scheduleModule
        )
    }
}