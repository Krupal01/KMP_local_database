package com.care.kmp.database

import app.cash.sqldelight.async.coroutines.await
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import com.care.kmp.AppDatabase
import com.care.kmp.AppDatabaseQueries
import com.care.kmp.domain.User

interface DriverFactory {
    fun createDriver(): SqlDriver

    suspend fun provideDbDriver(
        schema: SqlSchema<QueryResult.AsyncValue<Unit>>
    ): SqlDriver
}

class LocalDatabase(
    driver: SqlDriver
){
    private val  database = AppDatabase(driver)
    private val queries = AppDatabaseQueries(driver)

    fun getAll() : List<User> {
        return queries.selectAll().executeAsList().map {
            User(
                id = it.id,
                name = it.name
            )
        }
    }

    suspend fun insert(name: String) {
        queries.insertUser(name)
    }
}