package database

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import com.care.kmp.database.DriverFactory
import app.cash.sqldelight.driver.worker.expected.Worker
import kotlin.js.js

//class WebDriverFactory : DriverFactory {
//    override fun createDriver(): SqlDriver {
//        return WebWorkerDriver(
//            Worker(
//                """new URL("@cashapp/sqldelight-sqljs-worker/sqljs.worker.js", import.meta.url)"""
//            )
//        )
//    }
//
//    override suspend fun provideDbDriver(schema: SqlSchema<QueryResult.AsyncValue<Unit>>): SqlDriver {
//        return WebWorkerDriver(
//            Worker(
//                js("""new URL("@cashapp/sqldelight-sqljs-worker/sqljs.worker.js", import.meta.url)""")
//            )
//        ).also { schema.create(it).await() }
//    }
//}