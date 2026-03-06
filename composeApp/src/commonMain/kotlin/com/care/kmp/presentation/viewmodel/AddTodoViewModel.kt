package com.care.kmp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.care.kmp.domain.model.Priority
import com.care.kmp.domain.model.Todo
import com.care.kmp.domain.usecase.TodoUseCases
import com.care.kmp.presentation.contract.AddTodoEffects
import com.care.kmp.presentation.contract.AddTodoEvents
import com.care.kmp.presentation.contract.AddTodoUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddTodoViewModel(
    private val useCases: TodoUseCases
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddTodoUiState())
    val uiState: StateFlow<AddTodoUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<AddTodoEffects>()
    val effects: SharedFlow<AddTodoEffects> = _effects.asSharedFlow()

    fun sendEvent(event: AddTodoEvents){
        when(event){
            is AddTodoEvents.AddTodo -> {
                insertTodo(event.title, event.description, event.priority)
            }
            is AddTodoEvents.UpdateTodo -> {
                updateTodo(event.todo)
            }
        }
    }

    private fun sendEffect(effect: AddTodoEffects) {
        viewModelScope.launch {
            _effects.emit(effect)
        }
    }

    private fun insertTodo(title: String, description: String, priority: Priority) {
        viewModelScope.launch {
            runCatching {
                useCases.insertTodo(
                    Todo(title = title, description = description, priority = priority)
                )
            }
                .onSuccess { sendEffect(AddTodoEffects.NavigateBack) }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    private fun updateTodo(todo: Todo) {
        viewModelScope.launch {
            runCatching { useCases.updateTodo(todo) }
                .onSuccess { sendEffect(AddTodoEffects.NavigateBack) }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }
}