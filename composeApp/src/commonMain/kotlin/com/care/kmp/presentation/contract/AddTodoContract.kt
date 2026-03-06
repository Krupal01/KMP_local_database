package com.care.kmp.presentation.contract

import com.care.kmp.domain.model.Priority
import com.care.kmp.domain.model.Todo

data class AddTodoUiState(
    val existingTodo: Todo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class AddTodoEvents{
    data class AddTodo(val title: String, val description: String, val priority: Priority): AddTodoEvents()
    data class UpdateTodo(val todo: Todo): AddTodoEvents()
}

sealed class AddTodoEffects{
    data class ShowToast(val message: String): AddTodoEffects()
    object NavigateBack: AddTodoEffects()
}