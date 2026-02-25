package com.care.kmp.presentation.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.care.kmp.domain.User

@Composable
fun UserItem(
    user: User,
    onUpdate: (Long, String) -> Unit,
    onDelete: (Long) -> Unit
) {
    var editText by remember { mutableStateOf(user.name) }
    var isEditing by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (isEditing) {

            OutlinedTextField(
                value = editText,
                onValueChange = { editText = it },
                modifier = Modifier.weight(1f)
            )

            Button(onClick = {
                onUpdate(user.id, editText)
                isEditing = false
            }) {
                Text("Save")
            }

        } else {

            Text(
                text = user.name,
                modifier = Modifier.weight(1f)
            )

            Button(onClick = { isEditing = true }) {
                Text("Edit")
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = { onDelete(user.id) }
        ) {
            Text("Delete")
        }
    }
}