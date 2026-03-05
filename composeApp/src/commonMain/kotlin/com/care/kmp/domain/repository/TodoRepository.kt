package com.care.kmp.domain.repository

import com.care.kmp.domain.model.Todo as TodoModel
import com.care.kmp.domain.model.Priority

interface TodoRepository {

    suspend fun getAllTodos(): List<TodoModel>

    suspend fun getTodoById(id: String): TodoModel?

    suspend fun insertTodo(todo: TodoModel)

    suspend fun updateTodo(todo: TodoModel)

    suspend fun toggleComplete(id: String, isCompleted: Boolean)

    suspend fun deleteTodo(id: String)

    suspend fun deleteAllTodos()
}