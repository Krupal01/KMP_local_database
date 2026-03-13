package com.care.kmp.domain.model

import com.care.schedule.domain.model.TodoModel
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlin.time.Clock

enum class Priority {
    LOW, MEDIUM, HIGH
}

@Serializable
data class Todo(
    val id: String = generateId(),
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val createdAt: LocalDateTime = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()),
    val dueDate: LocalDateTime? = null
)

private fun generateId(): String =
    Clock.System.now().toEpochMilliseconds().toString()

fun Todo.toTodoModel() : TodoModel {
    return TodoModel(
        id = id,
        title = title,
        description = description,
        priority = priority.name,
    )
}