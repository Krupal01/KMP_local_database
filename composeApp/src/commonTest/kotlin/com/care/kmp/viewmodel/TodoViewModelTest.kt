package com.care.kmp.viewmodel

import com.care.kmp.base.BaseTest
import com.care.kmp.domain.model.Priority
import com.care.kmp.domain.model.Todo
import com.care.kmp.domain.usecase.TodoUseCases
import com.care.kmp.presentation.contract.TodoEffects
import com.care.kmp.presentation.contract.TodoEvents
import com.care.kmp.presentation.viewmodel.TodoViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class TodoViewModelTest : BaseTest(), KoinTest {

    private val useCases: TodoUseCases by inject()
    private lateinit var viewModel: TodoViewModel

    private val todo1 = Todo(id = "1", title = "Buy milk",  priority = Priority.LOW,    isCompleted = false)
    private val todo2 = Todo(id = "2", title = "Go to gym", priority = Priority.HIGH,   isCompleted = true)
    private val todo3 = Todo(id = "3", title = "Read book", priority = Priority.MEDIUM, isCompleted = false)

    @BeforeTest
    fun setup() {
        todoRepo.seedTodos(todo1, todo2, todo3)
        viewModel = TodoViewModel(useCases)
    }

    // ─── Init / LoadTodos ─────────────────────────────────────────

    @Test
    fun `init loads todos into state`() = runTest {
        advanceUntilIdle()
        assertEquals(3, viewModel.uiState.value.todos.size)
    }

    @Test
    fun `init sets isLoading true then false`() = runTest {
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `init clears error on load`() = runTest {
        advanceUntilIdle()
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `LoadTodos event reloads todos`() = runTest {
        advanceUntilIdle()
        todoRepo.seedTodos(Todo(id = "4", title = "New task", priority = Priority.LOW, isCompleted = false))

        viewModel.sendEvent(TodoEvents.LoadTodos)
        advanceUntilIdle()

        assertEquals(4, viewModel.uiState.value.todos.size)
    }

    @Test
    fun `loadTodos sets error state on failure`() = runTest {
        todoRepo.shouldThrow = true
        val vm = TodoViewModel(useCases)
        advanceUntilIdle()

        assertNotNull(vm.uiState.value.error)
        assertEquals("DB error", vm.uiState.value.error)
        assertFalse(vm.uiState.value.isLoading)
    }

    // ─── visibleTodos (Filter) ────────────────────────────────────

    @Test
    fun `visibleTodos returns all when filter is null`() = runTest {
        advanceUntilIdle()
        assertEquals(3, viewModel.visibleTodos.size)
    }

    @Test
    fun `FilterTodos true shows only completed`() = runTest {
        advanceUntilIdle()
        viewModel.sendEvent(TodoEvents.FilterTodos(filterCompleted = true))
        val visible = viewModel.visibleTodos
        assertTrue(visible.all { it.isCompleted })
        assertEquals(1, visible.size)
    }

    @Test
    fun `FilterTodos false shows only incomplete`() = runTest {
        advanceUntilIdle()
        viewModel.sendEvent(TodoEvents.FilterTodos(filterCompleted = false))
        val visible = viewModel.visibleTodos
        assertTrue(visible.none { it.isCompleted })
        assertEquals(2, visible.size)
    }

    @Test
    fun `FilterTodos null resets to all todos`() = runTest {
        advanceUntilIdle()
        viewModel.sendEvent(TodoEvents.FilterTodos(filterCompleted = true))
        viewModel.sendEvent(TodoEvents.FilterTodos(filterCompleted = null))
        assertEquals(3, viewModel.visibleTodos.size)
    }

    // ─── ToggleTodo ───────────────────────────────────────────────

    @Test
    fun `ToggleTodo updates isCompleted in repo`() = runTest {
        advanceUntilIdle()
        viewModel.sendEvent(TodoEvents.ToggleTodo(id = "1", isCompleted = true))
        advanceUntilIdle()
        assertTrue(todoRepo.allTodos().find { it.id == "1" }!!.isCompleted)
    }

    @Test
    fun `ToggleTodo reloads todos after success`() = runTest {
        advanceUntilIdle()
        val countBefore = viewModel.uiState.value.todos.size

        viewModel.sendEvent(TodoEvents.ToggleTodo(id = "1", isCompleted = true))
        advanceUntilIdle()

        assertEquals(countBefore, viewModel.uiState.value.todos.size)
    }

    @Test
    fun `ToggleTodo emits ShowToast effect on success`() = runTest {
        advanceUntilIdle()
        val effects = mutableListOf<TodoEffects>()
        val job = launch { viewModel.effects.collect { effects.add(it) } }
        advanceUntilIdle()
        viewModel.sendEvent(TodoEvents.ToggleTodo(id = "1", isCompleted = true))
        advanceUntilIdle()

        assertTrue(effects.any { it is TodoEffects.ShowToast })
        job.cancel()
    }

    @Test
    fun `ToggleTodo sets error on failure`() = runTest {
        advanceUntilIdle()
        todoRepo.shouldThrow = true

        viewModel.sendEvent(TodoEvents.ToggleTodo(id = "1", isCompleted = true))
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.error)
    }

    // ─── DeleteTodo ───────────────────────────────────────────────

    @Test
    fun `DeleteTodo removes todo from repo`() = runTest {
        advanceUntilIdle()
        viewModel.sendEvent(TodoEvents.DeleteTodo(id = "1"))
        advanceUntilIdle()
        assertNull(todoRepo.allTodos().find { it.id == "1" })
    }

    @Test
    fun `DeleteTodo reloads todos after success`() = runTest {
        advanceUntilIdle()
        viewModel.sendEvent(TodoEvents.DeleteTodo(id = "1"))
        advanceUntilIdle()
        assertEquals(2, viewModel.uiState.value.todos.size)
    }

    @Test
    fun `DeleteTodo sets error on failure`() = runTest {
        advanceUntilIdle()
        todoRepo.shouldThrow = true

        viewModel.sendEvent(TodoEvents.DeleteTodo(id = "1"))
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.error)
    }

    // ─── Navigation Effects ───────────────────────────────────────

    @Test
    fun `AddTodo event emits NavigateToAddTodo`() = runTest {
        advanceUntilIdle()
        val effects = mutableListOf<TodoEffects>()
        val job = launch { viewModel.effects.collect { effects.add(it) } }
        advanceUntilIdle()
        viewModel.sendEvent(TodoEvents.AddTodo)
        advanceUntilIdle()

        assertTrue(effects.any { it is TodoEffects.NavigateToAddTodo })
        job.cancel()
    }

    @Test
    fun `UpdateTodo event emits NavigateToUpdateTodo with correct todo`() = runTest {
        advanceUntilIdle()
        val effects = mutableListOf<TodoEffects>()
        val job = launch { viewModel.effects.collect { effects.add(it) } }
        advanceUntilIdle()
        viewModel.sendEvent(TodoEvents.UpdateTodo(todo = todo1))
        advanceUntilIdle()

        val navEffect = effects.filterIsInstance<TodoEffects.NavigateToUpdateTodo>().firstOrNull()
        assertNotNull(navEffect)
        assertEquals(todo1, navEffect.todo)
        job.cancel()
    }

    @Test
    fun `OnClickSettings emits NavigateToSettings`() = runTest {
        advanceUntilIdle()
        val effects = mutableListOf<TodoEffects>()
        val job = launch { viewModel.effects.collect { effects.add(it) } }
        advanceUntilIdle()
        viewModel.sendEvent(TodoEvents.OnClickSettings)
        advanceUntilIdle()

        assertTrue(effects.any { it is TodoEffects.NavigateToSettings })
        job.cancel()
    }
}