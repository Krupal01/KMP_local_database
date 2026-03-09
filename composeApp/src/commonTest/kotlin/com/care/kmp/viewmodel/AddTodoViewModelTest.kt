package com.care.kmp.viewmodel

import com.care.kmp.base.BaseTest
import com.care.kmp.domain.model.Priority
import com.care.kmp.domain.model.Todo
import com.care.kmp.domain.usecase.TodoUseCases
import com.care.kmp.presentation.contract.AddTodoEffects
import com.care.kmp.presentation.contract.AddTodoEvents
import com.care.kmp.presentation.viewmodel.AddTodoViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class AddTodoViewModelTest : BaseTest(), KoinTest {

    private val useCases: TodoUseCases by inject()
    private lateinit var viewModel: AddTodoViewModel

    private val existingTodo = Todo(
        id = "1", title = "Existing", description = "desc",
        priority = Priority.MEDIUM, isCompleted = false
    )

    @BeforeTest
    fun setup() {
        viewModel = AddTodoViewModel(useCases)
    }

    // ─── Initial State ────────────────────────────────────────────

    @Test
    fun `initial state has no error and not loading`() = runTest {
        val state = viewModel.uiState.value
        assertNull(state.error)
        assertFalse(state.isLoading)
    }

    // ─── AddTodo ──────────────────────────────────────────────────

    @Test
    fun `AddTodo inserts todo into repo`() = runTest {
        viewModel.sendEvent(
            AddTodoEvents.AddTodo(
                title = "New Task",
                description = "details",
                priority = Priority.HIGH
            )
        )
        advanceUntilIdle()

        val saved = todoRepo.allTodos()
        assertEquals(1, saved.size)
        assertEquals("New Task", saved.first().title)
        assertEquals(Priority.HIGH, saved.first().priority)
    }

    @Test
    fun `AddTodo emits NavigateBack on success`() = runTest {
        val effects = mutableListOf<AddTodoEffects>()
        val job = launch { viewModel.effects.collect { effects.add(it) } }

        viewModel.sendEvent(
            AddTodoEvents.AddTodo(title = "Task", description = "", priority = Priority.LOW)
        )
        advanceUntilIdle()

        assertTrue(effects.any { it is AddTodoEffects.NavigateBack })
        job.cancel()
    }

    @Test
    fun `AddTodo sets error on failure`() = runTest {
        todoRepo.shouldThrow = true
        viewModel.sendEvent(
            AddTodoEvents.AddTodo(title = "Task", description = "", priority = Priority.LOW)
        )
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.error)
        assertEquals("DB error", viewModel.uiState.value.error)
    }

    @Test
    fun `AddTodo does not emit NavigateBack on failure`() = runTest {
        todoRepo.shouldThrow = true
        val effects = mutableListOf<AddTodoEffects>()
        val job = launch { viewModel.effects.collect { effects.add(it) } }

        viewModel.sendEvent(
            AddTodoEvents.AddTodo(title = "Task", description = "", priority = Priority.LOW)
        )
        advanceUntilIdle()

        assertFalse(effects.any { it is AddTodoEffects.NavigateBack })
        job.cancel()
    }

    // ─── UpdateTodo ───────────────────────────────────────────────

    @Test
    fun `UpdateTodo modifies todo in repo`() = runTest {
        todoRepo.seedTodos(existingTodo)
        val updated = existingTodo.copy(title = "Updated Title", priority = Priority.HIGH)

        viewModel.sendEvent(AddTodoEvents.UpdateTodo(todo = updated))
        advanceUntilIdle()

        val saved = todoRepo.allTodos().find { it.id == "1" }
        assertNotNull(saved)
        assertEquals("Updated Title", saved.title)
        assertEquals(Priority.HIGH, saved.priority)
    }

    @Test
    fun `UpdateTodo emits NavigateBack on success`() = runTest {
        todoRepo.seedTodos(existingTodo)
        val effects = mutableListOf<AddTodoEffects>()
        val job = launch { viewModel.effects.collect { effects.add(it) } }

        viewModel.sendEvent(AddTodoEvents.UpdateTodo(todo = existingTodo.copy(title = "Updated")))
        advanceUntilIdle()

        assertTrue(effects.any { it is AddTodoEffects.NavigateBack })
        job.cancel()
    }

    @Test
    fun `UpdateTodo sets error on failure`() = runTest {
        todoRepo.seedTodos(existingTodo)
        todoRepo.shouldThrow = true

        viewModel.sendEvent(AddTodoEvents.UpdateTodo(todo = existingTodo))
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.error)
    }

    @Test
    fun `UpdateTodo does not emit NavigateBack on failure`() = runTest {
        todoRepo.shouldThrow = true
        val effects = mutableListOf<AddTodoEffects>()
        val job = launch { viewModel.effects.collect { effects.add(it) } }

        viewModel.sendEvent(AddTodoEvents.UpdateTodo(todo = existingTodo))
        advanceUntilIdle()

        assertFalse(effects.any { it is AddTodoEffects.NavigateBack })
        job.cancel()
    }
}