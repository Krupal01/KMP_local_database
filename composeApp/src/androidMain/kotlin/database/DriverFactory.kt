package database

import android.content.Context
import app.cash.sqldelight.async.coroutines.await
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.care.kmp.AppDatabase
import com.care.kmp.database.DriverFactory

class AndroidDriverFactory(private val context: Context) : DriverFactory {
     override fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = AppDatabase.Schema.synchronous(), context = context, name = "test.db"
        )
    }

    override suspend fun provideDbDriver(schema: SqlSchema<QueryResult.AsyncValue<Unit>>): SqlDriver {
        return AndroidSqliteDriver(schema.synchronous(), context, "test.db")
    }
}