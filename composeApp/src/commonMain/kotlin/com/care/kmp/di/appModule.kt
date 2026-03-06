package com.care.kmp.di

import app.cash.sqldelight.db.SqlDriver
import com.care.kmp.AppDatabase
import com.care.kmp.data.local.LocalDatabase
import com.care.kmp.data.network.ApiClient
import com.care.kmp.data.network.ApiService
import com.care.kmp.data.network.MockEngineProvider
import com.care.kmp.data.network.provideHttpClientEngine
import com.care.kmp.data.repository.TodoRepositoryImpl
import com.care.kmp.data.repository.UserRepositoryImpl
import com.care.kmp.database.DriverFactory
import com.care.kmp.domain.repository.TodoRepository
import com.care.kmp.domain.repository.UserRepository
import com.care.kmp.domain.usecase.*
import com.care.kmp.domain.usecase.TodoUseCases
import com.care.kmp.presentation.viewmodel.AddTodoViewModel
import com.care.kmp.presentation.viewmodel.TodoViewModel
import com.care.kmp.presentation.viewmodel.UserViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun appModule(driver: SqlDriver): Module = module {
    single { driver}
    single { LocalDatabase(get()) }

    single {
        if (true) {
            HttpClient(MockEngineProvider.engine) {
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = true })
                }
            }
        } else {
            HttpClient(provideHttpClientEngine()) {
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = true })
                }
            }
        }
    }

    single { ApiClient(httpClient = get()) }

    single { ApiService(get()) }


    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    single<TodoRepository> { TodoRepositoryImpl(get(), get()) }


    single { UserUseCase(get()) }
    single {
        TodoUseCases(
            getAllTodos = GetAllTodosUseCase(get()),
            getTodoById = GetTodoByIdUseCase(get()),
            insertTodo = InsertTodoUseCase(get()),
            updateTodo = UpdateTodoUseCase(get()),
            toggleComplete = ToggleCompleteTodoUseCase(get()),
            deleteTodo = DeleteTodoUseCase(get()),
            deleteAllTodos = DeleteAllTodosUseCase(get())
        )
    }

    viewModel { UserViewModel(get()) }
    viewModelOf(::TodoViewModel)
    viewModelOf(::AddTodoViewModel)
}



