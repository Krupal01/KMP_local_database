package com.care.kmp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.care.kmp.data.UserRepositoryImpl
import com.care.kmp.database.LocalDatabase
import com.care.kmp.presentation.UserViewModel
import com.care.kmp.presentation.UserViewModelFactory
import database.AndroidDriverFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val viewModel : UserViewModel by viewModels {
            UserViewModelFactory(LocalDatabase(AndroidDriverFactory(this).createDriver()))
        }

//        val viewModel : UserViewModel = UserViewModelFactory.create(LocalDatabase(AndroidDriverFactory(this)))

        setContent {
            App(viewModel)
        }
    }
}

