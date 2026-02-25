package com.care.kmp

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.lifecycle.viewmodel.compose.viewModel
import com.care.kmp.database.LocalDatabase
import com.care.kmp.presentation.UserViewModel
import com.care.kmp.presentation.UserViewModelFactory
import database.JsDriverFactory
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.getValue

@OptIn(ExperimentalComposeUiApi::class)
fun main() {

//    val viewModel : UserViewModel = UserViewModelFactory.create(LocalDatabase(JsDriverFactory()))
//    ComposeViewport {
//        val viewModel : UserViewModel =
//            viewModel(factory = UserViewModelFactory(LocalDatabase(JsDriverFactory().provideDbDriver())))
//
//        App(viewModel)
//    }

    val scope = MainScope()

    scope.launch {
        val driver = JsDriverFactory().provideDbDriver(AppDatabase.Schema)
        val database = LocalDatabase(driver)

        ComposeViewport {
            val viewModel: UserViewModel =
                viewModel(factory = UserViewModelFactory(database))

            App(viewModel)
        }
    }
}