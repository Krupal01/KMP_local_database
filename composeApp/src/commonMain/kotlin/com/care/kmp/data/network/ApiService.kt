package com.care.kmp.data.network

import com.care.kmp.domain.model.User
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import com.care.kmp.domain.model.Todo as TodoModel

private const val BASE_URL = "https://mockapi.com"

class ApiService(
    private val client: ApiClient
) {
    suspend fun getAllUsers(): List<User> {
        return client.httpClient.get("$BASE_URL/users").body()
    }

    suspend fun addUser(name: String): User {
        return client.httpClient.post("$BASE_URL/users") {
            contentType(ContentType.Application.Json)
            setBody(User(0, name))
        }.body()
    }

    suspend fun updateUser(id: Long, name: String): User {
        return client.httpClient.put("$BASE_URL/users/$id") {
            contentType(ContentType.Application.Json)
            setBody(User(id, name))
        }.body()
    }

    suspend fun deleteUser(id: Long) {
        client.httpClient.delete("$BASE_URL/users/$id")
    }

    suspend fun getAllTodos(): List<TodoModel> {
        return client.httpClient.get("$BASE_URL/todos").body()
    }

    suspend fun getTodoById(id: String): TodoModel {
        return client.httpClient.get("$BASE_URL/todos/$id").body()
    }

    suspend fun addTodo(todo: TodoModel): TodoModel {
        return client.httpClient.post("$BASE_URL/todos") {
            contentType(ContentType.Application.Json)
            setBody(todo)
        }.body()
    }

    suspend fun updateTodo(todo: TodoModel): TodoModel {
        return client.httpClient.put("$BASE_URL/todos/${todo.id}") {
            contentType(ContentType.Application.Json)
            setBody(todo)
        }.body()
    }

    suspend fun toggleComplete(id: String): TodoModel {
        return client.httpClient.patch("$BASE_URL/todos/$id/toggle").body()
    }

    suspend fun deleteTodo(id: String) {
        client.httpClient.delete("$BASE_URL/todos/$id")
    }

    suspend fun deleteAllTodos() {
        client.httpClient.delete("$BASE_URL/todos")
    }
}