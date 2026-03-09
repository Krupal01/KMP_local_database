package com.care.kmp.di

import com.care.kmp.domain.repository.TodoRepository
import com.care.kmp.domain.repository.UserRepository
import com.care.kmp.domain.usecase.*
import com.care.kmp.presentation.viewmodel.AddTodoViewModel
import com.care.kmp.presentation.viewmodel.TodoViewModel
import com.care.kmp.presentation.viewmodel.UserViewModel
import com.care.kmp.repository.FakeTodoRepository
import com.care.kmp.repository.FakeUserRepository
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

// Global instances so tests can access fakes directly
val fakeTodoRepo = FakeTodoRepository()
val fakeUserRepo = FakeUserRepository()

val testAppModule = module {

    // ── Fake Repositories ─────────────────────────────────────────
    single<TodoRepository> { fakeTodoRepo }
    single<UserRepository> { fakeUserRepo }

    // ── Use Cases (real implementation, fake dependencies) ────────
    single {
        TodoUseCases(
            getAllTodos     = GetAllTodosUseCase(get()),
            getTodoById    = GetTodoByIdUseCase(get()),
            insertTodo     = InsertTodoUseCase(get()),
            updateTodo     = UpdateTodoUseCase(get()),
            toggleComplete = ToggleCompleteTodoUseCase(get()),
            deleteTodo     = DeleteTodoUseCase(get()),
            deleteAllTodos = DeleteAllTodosUseCase(get())
        )
    }
    single { UserUseCase(get()) }

    // ── ViewModels ────────────────────────────────────────────────
    viewModelOf(::TodoViewModel)
    viewModelOf(::AddTodoViewModel)
    viewModel { UserViewModel(get()) }
}