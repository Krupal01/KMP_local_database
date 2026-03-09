package com.care.kmp.repository

import com.care.kmp.domain.model.Todo
import com.care.kmp.domain.repository.TodoRepository

class FakeTodoRepository : TodoRepository {

    private val todos = mutableListOf<Todo>()
    var shouldThrow = false

    override suspend fun getAllTodos(): List<Todo> {
        if (shouldThrow) throw Exception("DB error")
        return todos.toList()
    }

    override suspend fun getTodoById(id: String): Todo? {
        if (shouldThrow) throw Exception("DB error")
        return todos.find { it.id == id }
    }

    override suspend fun insertTodo(todo: Todo) {
        if (shouldThrow) throw Exception("DB error")
        todos.add(todo)
    }

    override suspend fun updateTodo(todo: Todo) {
        if (shouldThrow) throw Exception("DB error")
        val index = todos.indexOfFirst { it.id == todo.id }
        if (index != -1) todos[index] = todo
    }

    override suspend fun toggleComplete(id: String, isCompleted: Boolean) {
        if (shouldThrow) throw Exception("DB error")
        val index = todos.indexOfFirst { it.id == id }
        if (index != -1) todos[index] = todos[index].copy(isCompleted = isCompleted)
    }

    override suspend fun deleteTodo(id: String) {
        if (shouldThrow) throw Exception("DB error")
        todos.removeAll { it.id == id }
    }

    override suspend fun deleteAllTodos() {
        if (shouldThrow) throw Exception("DB error")
        todos.clear()
    }

    // ─── Test Helpers ─────────────────────────────────────────────
    fun seedTodos(vararg todo: Todo) = todos.addAll(todo)
    fun allTodos() = todos.toList()
    fun reset() { todos.clear(); shouldThrow = false }
}