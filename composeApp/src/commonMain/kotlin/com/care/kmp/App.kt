package com.care.kmp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.care.kmp.presentation.UserIntent
import com.care.kmp.presentation.UserViewModel
import com.care.kmp.presentation.UserViewModelFactory
import com.care.kmp.presentation.view.UserItem
import org.jetbrains.compose.resources.painterResource

import kmp.composeapp.generated.resources.Res
import kmp.composeapp.generated.resources.compose_multiplatform

@Composable
fun App(viewModel: UserViewModel) {

    val state by viewModel.state.collectAsState()

    var input by remember { mutableStateOf("") }

    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("User List")

            OutlinedTextField(
                value = input,
                onValueChange = { input = it }
            )

            Button(onClick = {
                viewModel.onIntent(UserIntent.AddUser(input))
                input = ""
            }) {
                Text("Add")
            }

            Button(onClick = {
                viewModel.onIntent(UserIntent.LoadUsers)
                input = ""
            }) {
                Text("Load")
            }

            state.users.forEach { user ->
                UserItem(
                    user = user,
                    onUpdate = { id, name ->
                        viewModel.onIntent(UserIntent.UpdateUser(id, name))
                    },
                    onDelete = { id ->
                        viewModel.onIntent(UserIntent.DeleteUser(id))
                    }
                )
            }
        }
    }
}