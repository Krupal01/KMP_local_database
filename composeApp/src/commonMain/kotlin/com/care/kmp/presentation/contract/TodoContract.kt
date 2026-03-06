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

    object AddTodo: TodoEvents()
    data class UpdateTodo(val todo: Todo): TodoEvents()
    data class DeleteTodo(val id: String): TodoEvents()
    data class ToggleTodo(val id: String, val isCompleted: Boolean): TodoEvents()
    data class FilterTodos(val filterCompleted: Boolean?): TodoEvents()

    object OnClickSettings: TodoEvents()
}

sealed class TodoEffects{
    data class ShowToast(val message: String): TodoEffects()
    object NavigateToAddTodo: TodoEffects()
    data class NavigateToUpdateTodo(val todo: Todo): TodoEffects()

    object NavigateToSettings: TodoEffects()
}