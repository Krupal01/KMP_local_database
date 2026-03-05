package com.care.kmp.domain.usecase

import com.care.kmp.domain.model.Priority
import com.care.kmp.domain.model.Todo as TodoModel
import com.care.kmp.domain.repository.TodoRepository

class GetAllTodosUseCase(private val repository: TodoRepository) {
    suspend operator fun invoke(): List<TodoModel> = repository.getAllTodos()
}

class GetTodoByIdUseCase(private val repository: TodoRepository) {
    suspend operator fun invoke(id: String): TodoModel? = repository.getTodoById(id)
}

class InsertTodoUseCase(private val repository: TodoRepository) {
    suspend operator fun invoke(todo: TodoModel) = repository.insertTodo(todo)
}

class UpdateTodoUseCase(private val repository: TodoRepository) {
    suspend operator fun invoke(todo: TodoModel) = repository.updateTodo(todo)
}

class ToggleCompleteTodoUseCase(private val repository: TodoRepository) {
    suspend operator fun invoke(id: String, isCompleted: Boolean) =
        repository.toggleComplete(id, isCompleted)
}

class DeleteTodoUseCase(private val repository: TodoRepository) {
    suspend operator fun invoke(id: String) = repository.deleteTodo(id)
}

class DeleteAllTodosUseCase(private val repository: TodoRepository) {
    suspend operator fun invoke() = repository.deleteAllTodos()
}

// ─── Wrapper ──────────────────────────────────────────────────────────────────

data class TodoUseCases(
    val getAllTodos: GetAllTodosUseCase,
    val getTodoById: GetTodoByIdUseCase,
    val insertTodo: InsertTodoUseCase,
    val updateTodo: UpdateTodoUseCase,
    val toggleComplete: ToggleCompleteTodoUseCase,
    val deleteTodo: DeleteTodoUseCase,
    val deleteAllTodos: DeleteAllTodosUseCase
)