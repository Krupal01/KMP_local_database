package com.care.kmp.data.repository

import com.care.kmp.data.local.LocalDatabase
import com.care.kmp.data.network.ApiService
import com.care.kmp.domain.model.Todo as TodoModel
import com.care.kmp.domain.repository.TodoRepository
import com.care.kmp.getPlatform

class TodoRepositoryImpl(
    private val localDatabase: LocalDatabase,
    private val todoApiService: ApiService
) : TodoRepository {

    override suspend fun getAllTodos(): List<TodoModel> {
        println("Getting all todos")
        val todos =  if (getPlatform().name.equals("Web with Kotlin/JS")) {
            todoApiService.getAllTodos()
        } else {
            localDatabase.getAllTodos()
        }
        println("Got all todos: $todos")
        return todos
    }

    override suspend fun getTodoById(id: String): TodoModel? {
        return if (getPlatform().name.equals("Web with Kotlin/JS")) {
            todoApiService.getTodoById(id)
        } else {
            localDatabase.getTodoById(id)
        }
    }

    override suspend fun insertTodo(todo: TodoModel) {
        println("Inserting todo: $todo")
        if (getPlatform().name.equals("Web with Kotlin/JS")) todoApiService.addTodo(todo)
        else localDatabase.insertTodo(todo)
    }

    override suspend fun updateTodo(todo: TodoModel) {
        if (getPlatform().name.equals("Web with Kotlin/JS")) todoApiService.updateTodo(todo)
        else localDatabase.updateTodo(todo)
    }

    override suspend fun toggleComplete(id: String, isCompleted: Boolean) {
        if (getPlatform().name.equals("Web with Kotlin/JS")) todoApiService.toggleComplete(id)
        else localDatabase.toggleComplete(id, isCompleted)
    }

    override suspend fun deleteTodo(id: String) {
        if (getPlatform().name.equals("Web with Kotlin/JS")) todoApiService.deleteTodo(id)
        else localDatabase.deleteTodo(id)
    }

    override suspend fun deleteAllTodos() {
        if (getPlatform().name.equals("Web with Kotlin/JS")) todoApiService.deleteAllTodos()
        else localDatabase.deleteAllTodos()
    }
}