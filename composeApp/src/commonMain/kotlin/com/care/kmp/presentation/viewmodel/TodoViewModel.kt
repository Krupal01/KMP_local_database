package com.care.kmp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.care.kmp.domain.model.Priority
import com.care.kmp.domain.model.Todo
import com.care.kmp.domain.usecase.TodoUseCases
import com.care.kmp.presentation.contract.AddTodoEvents
import com.care.kmp.presentation.contract.TodoEffects
import com.care.kmp.presentation.contract.TodoEffects.*
import com.care.kmp.presentation.contract.TodoEvents
import com.care.kmp.presentation.contract.TodoUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TodoViewModel(
    private val useCases: TodoUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(TodoUiState())
    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<TodoEffects>()
    val effects: SharedFlow<TodoEffects> = _effects.asSharedFlow()

    fun sendEvent(event: TodoEvents) {
        when (event) {
            is TodoEvents.LoadTodos -> loadTodos()
            is TodoEvents.AddTodo -> {
                sendEffect(NavigateToAddTodo)
            }

            is TodoEvents.DeleteTodo -> deleteTodo(event.id)
            is TodoEvents.FilterTodos -> setFilter(event.filterCompleted)
            is TodoEvents.ToggleTodo -> toggleComplete(event.id, event.isCompleted)
            is TodoEvents.UpdateTodo -> {
                sendEffect(NavigateToUpdateTodo(event.todo))
            }
            TodoEvents.OnClickSettings -> {
                sendEffect(NavigateToSettings)
            }

            is TodoEvents.OnClickSchedule -> {
                sendEffect(NavigateToSchedule(event.todo))
            }

            is TodoEvents.ShowDetails -> {
                sendEffect(ShowDetailSheet(event.todo))
            }

            TodoEvents.OnClickMap -> {
                sendEffect(NavigateToMap)
            }
        }
    }


    val visibleTodos: List<Todo>
        get() = _uiState.value.let { state ->
            when (state.filterCompleted) {
                true  -> state.todos.filter { it.isCompleted }
                false -> state.todos.filter { !it.isCompleted }
                null  -> state.todos
            }
        }

    init {
        loadTodos()
    }

    private fun sendEffect(effect: TodoEffects) {
        viewModelScope.launch {
            _effects.emit(effect)
        }
    }

    private fun loadTodos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { useCases.getAllTodos() }
                .onSuccess { todos ->
                    _uiState.update {
                        it.copy(
                            todos = todos,
                            isLoading = false
                        )
                    }
                    println("Loaded todos: $todos")
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            error = e.message,
                            isLoading = false
                        )
                    }
                    println("Error loading todos: ${e.message}")
                }
        }
    }

    private fun toggleComplete(id: String, isCompleted: Boolean) {
        viewModelScope.launch {
            runCatching { useCases.toggleComplete(id, isCompleted) }
                .onSuccess {
                    loadTodos()
                    sendEffect(TodoEffects.ShowToast("Todo toggled"))
                }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    private fun deleteTodo(id: String) {
        viewModelScope.launch {
            runCatching { useCases.deleteTodo(id) }
                .onSuccess { loadTodos() }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    private fun setFilter(completed: Boolean?) {
        _uiState.update { it.copy(filterCompleted = completed) }
    }
}