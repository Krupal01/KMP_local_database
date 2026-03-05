package com.care.kmp.data.local

import app.cash.sqldelight.db.SqlDriver
import com.care.kmp.AppDatabase
import com.care.kmp.database.Todo
import com.care.kmp.domain.model.User
import com.care.kmp.domain.model.Todo as TodoModel


class LocalDatabase(
    driver: SqlDriver
){
    private val  database = AppDatabase(
        driver = driver,
        todoAdapter = Todo.Adapter(
            priorityAdapter = priorityAdapter,
            createdAtAdapter = localDateTimeAdapter,
            dueDateAdapter = localDateTimeAdapter
        )
    )

    private val userQueries = database.userQueries
    private val todoQueries = database.todoQueries



    fun getAllUser() : List<User> {
        return userQueries.selectAllUser().executeAsList().map {
            User(
                id = it.id,
                name = it.name
            )
        }
    }

    suspend fun insertUser(name: String) {
        userQueries.insertUser(name)
    }


    suspend fun updateUser(id: Long, name: String) {
        userQueries.updateUser(
            name = name,
            id = id
        )
    }

    suspend fun deleteUser(id: Long) {
        userQueries.deleteUser(id)
    }

    fun getAllTodos(): List<TodoModel> {
        return todoQueries.selectAllTodo().executeAsList().map { it.toModel() }
    }

    fun getTodoById(id: String): TodoModel? {
        return todoQueries.selectTodoById(id).executeAsOneOrNull()?.toModel()
    }

    suspend fun insertTodo(todo: TodoModel) {
        println("insertTodo: $todo")
        todoQueries.insertTodo(
            id = todo.id,
            title = todo.title,
            description = todo.description,
            isCompleted = todo.isCompleted,
            priority = todo.priority,
            createdAt = todo.createdAt,
            dueDate = todo.dueDate
        )
    }

    suspend fun updateTodo(todo: TodoModel) {
        todoQueries.updateTodo(
            title = todo.title,
            description = todo.description,
            isCompleted = todo.isCompleted,
            priority = todo.priority,
            dueDate = todo.dueDate,
            id = todo.id
        )
    }

    suspend fun toggleComplete(id: String, isCompleted: Boolean) {
        todoQueries.toggleCompleteTodo(
            isCompleted = isCompleted,
            id = id
        )
    }

    suspend fun deleteTodo(id: String) {
        todoQueries.deleteTodo(id)
    }

    suspend fun deleteAllTodos() {
        todoQueries.deleteAllTodo()
    }

    // ─── Mapper ──────────────────────────────────────────────────────────────

    private fun Todo.toModel() = TodoModel(
        id = id,
        title = title,
        description = description,
        isCompleted = isCompleted,
        priority = priority,
        createdAt = createdAt,
        dueDate = dueDate
    )
}