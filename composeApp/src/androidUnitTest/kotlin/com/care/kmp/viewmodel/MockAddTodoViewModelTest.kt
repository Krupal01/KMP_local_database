package com.care.kmp.viewmodel

import app.cash.turbine.test
import com.care.kmp.domain.model.Priority
import com.care.kmp.domain.model.Todo
import com.care.kmp.domain.usecase.InsertTodoUseCase
import com.care.kmp.domain.usecase.TodoUseCases
import com.care.kmp.domain.usecase.UpdateTodoUseCase
import com.care.kmp.presentation.contract.AddTodoEffects
import com.care.kmp.presentation.contract.AddTodoEvents
import com.care.kmp.presentation.contract.AddTodoUiState
import com.care.kmp.presentation.viewmodel.AddTodoViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Test
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

@OptIn(ExperimentalCoroutinesApi::class)
class MockAddTodoViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    // ─── Mocks ────────────────────────────────────────────────────────
    private val insertTodo     = mockk<InsertTodoUseCase>(relaxed = true)
    private val updateTodo     = mockk<UpdateTodoUseCase>(relaxed = true)

    private val useCases = TodoUseCases(
        getAllTodos = mockk(relaxed = true),
        getTodoById = mockk(relaxed = true),
        insertTodo = insertTodo,
        updateTodo = updateTodo,
        toggleComplete = mockk(relaxed = true),
        deleteTodo = mockk(relaxed = true),
        deleteAllTodos = mockk(relaxed = true)
    )

    private lateinit var viewModel: AddTodoViewModel

    // ─── Test Data ────────────────────────────────────────────────────
    private val sampleTodo = Todo(
        id = "1",
        title = "Buy milk",
        description = "From the store",
        priority = Priority.MEDIUM,
        isCompleted = false
    )

    // ─── Setup / Teardown ─────────────────────────────────────────────
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AddTodoViewModel(useCases)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    // ─── Initial State ────────────────────────────────────────────────

    @Test
    fun `initial uiState is default AddTodoUiState`() = runTest {
        assertEquals(AddTodoUiState(), viewModel.uiState.value)
        assertNull(viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    // ─── AddTodo Event ────────────────────────────────────────────────

    @Test
    fun `sendEvent AddTodo - success emits NavigateBack effect`() = runTest {
        coEvery { insertTodo(any()) } returns Unit

        viewModel.effects.test {
            viewModel.sendEvent(
                AddTodoEvents.AddTodo(
                    title       = "Buy milk",
                    description = "From the store",
                    priority    = Priority.MEDIUM
                )
            )
            advanceUntilIdle()

            assertEquals(AddTodoEffects.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `sendEvent AddTodo - success does not update error in uiState`() = runTest {
        coEvery { insertTodo(any()) } returns Unit

        viewModel.sendEvent(
            AddTodoEvents.AddTodo(
                title       = "Buy milk",
                description = "From the store",
                priority    = Priority.MEDIUM
            )
        )
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `sendEvent AddTodo - failure updates error in uiState`() = runTest {
        coEvery { insertTodo(any()) } throws Exception("Insert failed")

        viewModel.uiState.test {
            assertEquals(AddTodoUiState(), awaitItem())      // initial

            viewModel.sendEvent(
                AddTodoEvents.AddTodo(
                    title       = "Buy milk",
                    description = "From the store",
                    priority    = Priority.MEDIUM
                )
            )
            advanceUntilIdle()

            assertEquals("Insert failed", awaitItem().error) // error state

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `sendEvent AddTodo - failure does NOT emit NavigateBack`() = runTest {
        coEvery { insertTodo(any()) } throws Exception("Insert failed")

        viewModel.effects.test {
            viewModel.sendEvent(
                AddTodoEvents.AddTodo(
                    title       = "Buy milk",
                    description = "From the store",
                    priority    = Priority.HIGH
                )
            )
            advanceUntilIdle()

            expectNoEvents() // ✅ No NavigateBack on failure
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `sendEvent AddTodo - calls insertTodo with correct data`() = runTest {
        coEvery { insertTodo(any()) } returns Unit

        viewModel.sendEvent(
            AddTodoEvents.AddTodo(
                title       = "Buy milk",
                description = "From the store",
                priority    = Priority.HIGH
            )
        )
        advanceUntilIdle()

        // Verify correct fields passed (id will be auto-generated)
        coVerify(exactly = 1) {
            insertTodo(
                match {
                    it.title == "Buy milk" &&
                            it.description == "From the store" &&
                            it.priority == Priority.HIGH
                }
            )
        }
    }

    // ─── UpdateTodo Event ─────────────────────────────────────────────

    @Test
    fun `sendEvent UpdateTodo - success emits NavigateBack effect`() = runTest {
        coEvery { updateTodo(any()) } returns Unit

        viewModel.effects.test {
            viewModel.sendEvent(AddTodoEvents.UpdateTodo(sampleTodo))
            advanceUntilIdle()

            assertEquals(AddTodoEffects.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `sendEvent UpdateTodo - failure updates error in uiState`() = runTest {
        coEvery { updateTodo(any()) } throws Exception("Update failed")

        viewModel.uiState.test {
            assertEquals(AddTodoUiState(), awaitItem())      // initial

            viewModel.sendEvent(AddTodoEvents.UpdateTodo(sampleTodo))
            advanceUntilIdle()

            assertEquals("Update failed", awaitItem().error) // error state

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `sendEvent UpdateTodo - calls updateTodo with exact todo`() = runTest {
        coEvery { updateTodo(any()) } returns Unit

        viewModel.sendEvent(AddTodoEvents.UpdateTodo(sampleTodo))
        advanceUntilIdle()

        coVerify(exactly = 1) { updateTodo(sampleTodo) }
    }

    @Test
    fun `sendEvent UpdateTodo - failure does NOT emit NavigateBack`() = runTest {
        coEvery { updateTodo(any()) } throws Exception("Update failed")

        viewModel.effects.test {
            viewModel.sendEvent(AddTodoEvents.UpdateTodo(sampleTodo))
            advanceUntilIdle()

            expectNoEvents() // ✅ No NavigateBack on failure
            cancelAndIgnoreRemainingEvents()
        }
    }
}