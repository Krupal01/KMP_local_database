package com.care.kmp.presentation.contract

import com.care.kmp.domain.model.Todo

data class TodoUiState(
    val todos: List<Todo> = emptyList(),
    val filterCompleted: Boolean? = null,   // null = show all
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class TodoEvents{
    object LoadTodos: TodoEvents()
    data class AddTodo(val title: String, val description: String, val priority: String): TodoEvents()
    data class UpdateTodo(val id: String, val title: String, val description: String, val priority: String): TodoEvents()
    data class DeleteTodo(val id: String): TodoEvents()
    data class ToggleTodo(val id: String, val isCompleted: Boolean): TodoEvents()
    data class FilterTodos(val filterCompleted: Boolean?): TodoEvents()
}

sealed class TodoEffects{
    object LoadTodos: TodoEffects()
    data class ShowToast(val message: String): TodoEffects()
    object NavigateToAddTodo: TodoEffects()
    data class NavigateToUpdateTodo(val todo: Todo): TodoEffects()
}