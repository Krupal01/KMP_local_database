package com.care.kmp.data.local

// commonMain — TodoAdapters.kt
import app.cash.sqldelight.ColumnAdapter
import com.care.kmp.domain.model.Priority
import kotlinx.datetime.LocalDateTime

val priorityAdapter = object : ColumnAdapter<Priority, String> {
    override fun decode(databaseValue: String) =
        Priority.valueOf(databaseValue)
    override fun encode(value: Priority) =
        value.name
}

val localDateTimeAdapter = object : ColumnAdapter<LocalDateTime, String> {
    override fun decode(databaseValue: String) =
        LocalDateTime.parse(databaseValue)
    override fun encode(value: LocalDateTime) =
        value.toString()
}

val booleanAdapter = object : ColumnAdapter<Boolean, Long> {
    override fun decode(databaseValue: Long) = databaseValue == 1L
    override fun encode(value: Boolean) = if (value) 1L else 0L
}