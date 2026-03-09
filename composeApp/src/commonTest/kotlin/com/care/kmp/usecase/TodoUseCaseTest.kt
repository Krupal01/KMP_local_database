package com.care.kmp.usecase

import com.care.kmp.base.BaseTest
import com.care.kmp.domain.model.Priority
import com.care.kmp.domain.model.Todo
import com.care.kmp.domain.usecase.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class TodoUseCaseTest : BaseTest(), KoinTest {

    private val useCases: TodoUseCases by inject()

    private val todo1 = Todo(id = "1", title = "Buy milk",  description = "2 liters",  priority = Priority.LOW,    isCompleted = false)
    private val todo2 = Todo(id = "2", title = "Go to gym", description = "leg day",   priority = Priority.HIGH,   isCompleted = true)
    private val todo3 = Todo(id = "3", title = "Read book", description = "chapter 5", priority = Priority.MEDIUM, isCompleted = false)

    // ─── GetAllTodosUseCase ───────────────────────────────────────

    @Test
    fun `getAllTodos returns empty list when no todos`() = runTest {
        val result = useCases.getAllTodos()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getAllTodos returns all seeded todos`() = runTest {
        todoRepo.seedTodos(todo1, todo2, todo3)
        val result = useCases.getAllTodos()
        assertEquals(3, result.size)
        assertEquals(listOf(todo1, todo2, todo3), result)
    }

    @Test
    fun `getAllTodos throws when repo throws`() = runTest {
        todoRepo.shouldThrow = true
        assertFailsWith<Exception>("DB error") {
            useCases.getAllTodos()
        }
    }

    // ─── GetTodoByIdUseCase ───────────────────────────────────────

    @Test
    fun `getTodoById returns correct todo`() = runTest {
        todoRepo.seedTodos(todo1, todo2)
        val result = useCases.getTodoById("1")
        assertNotNull(result)
        assertEquals(todo1, result)
    }

    @Test
    fun `getTodoById returns null for non-existent id`() = runTest {
        todoRepo.seedTodos(todo1)
        val result = useCases.getTodoById("999")
        assertNull(result)
    }

    @Test
    fun `getTodoById throws when repo throws`() = runTest {
        todoRepo.shouldThrow = true
        assertFailsWith<Exception> { useCases.getTodoById("1") }
    }

    // ─── InsertTodoUseCase ────────────────────────────────────────

    @Test
    fun `insertTodo adds todo to repository`() = runTest {
        useCases.insertTodo(todo1)
        assertEquals(1, todoRepo.allTodos().size)
        assertEquals(todo1, todoRepo.allTodos().first())
    }

    @Test
    fun `insertTodo adds multiple todos independently`() = runTest {
        useCases.insertTodo(todo1)
        useCases.insertTodo(todo2)
        assertEquals(2, todoRepo.allTodos().size)
    }

    @Test
    fun `insertTodo throws when repo throws`() = runTest {
        todoRepo.shouldThrow = true
        assertFailsWith<Exception> { useCases.insertTodo(todo1) }
    }

    // ─── UpdateTodoUseCase ────────────────────────────────────────

    @Test
    fun `updateTodo modifies existing todo title`() = runTest {
        todoRepo.seedTodos(todo1)
        val updated = todo1.copy(title = "Buy oat milk")
        useCases.updateTodo(updated)
        assertEquals("Buy oat milk", todoRepo.allTodos().first().title)
    }

    @Test
    fun `updateTodo modifies priority`() = runTest {
        todoRepo.seedTodos(todo1)
        val updated = todo1.copy(priority = Priority.HIGH)
        useCases.updateTodo(updated)
        assertEquals(Priority.HIGH, todoRepo.allTodos().first().priority)
    }

    @Test
    fun `updateTodo does not affect other todos`() = runTest {
        todoRepo.seedTodos(todo1, todo2)
        useCases.updateTodo(todo1.copy(title = "Changed"))
        assertEquals("Go to gym", todoRepo.allTodos().find { it.id == "2" }?.title)
    }

    @Test
    fun `updateTodo throws when repo throws`() = runTest {
        todoRepo.seedTodos(todo1)
        todoRepo.shouldThrow = true
        assertFailsWith<Exception> { useCases.updateTodo(todo1.copy(title = "X")) }
    }

    // ─── ToggleCompleteTodoUseCase ────────────────────────────────

    @Test
    fun `toggleComplete marks incomplete todo as complete`() = runTest {
        todoRepo.seedTodos(todo1) // isCompleted = false
        useCases.toggleComplete("1", true)
        assertTrue(todoRepo.allTodos().first().isCompleted)
    }

    @Test
    fun `toggleComplete marks complete todo as incomplete`() = runTest {
        todoRepo.seedTodos(todo2) // isCompleted = true
        useCases.toggleComplete("2", false)
        assertFalse(todoRepo.allTodos().first().isCompleted)
    }

    @Test
    fun `toggleComplete does not affect other todos`() = runTest {
        todoRepo.seedTodos(todo1, todo2)
        useCases.toggleComplete("1", true)
        // todo2 should remain unchanged
        assertTrue(todoRepo.allTodos().find { it.id == "2" }!!.isCompleted)
    }

    @Test
    fun `toggleComplete throws when repo throws`() = runTest {
        todoRepo.shouldThrow = true
        assertFailsWith<Exception> { useCases.toggleComplete("1", true) }
    }

    // ─── DeleteTodoUseCase ────────────────────────────────────────

    @Test
    fun `deleteTodo removes correct todo`() = runTest {
        todoRepo.seedTodos(todo1, todo2)
        useCases.deleteTodo("1")
        val remaining = todoRepo.allTodos()
        assertEquals(1, remaining.size)
        assertEquals("2", remaining.first().id)
    }

    @Test
    fun `deleteTodo on non-existent id does not throw`() = runTest {
        todoRepo.seedTodos(todo1)
        useCases.deleteTodo("999") // should silently do nothing
        assertEquals(1, todoRepo.allTodos().size)
    }

    @Test
    fun `deleteTodo throws when repo throws`() = runTest {
        todoRepo.shouldThrow = true
        assertFailsWith<Exception> { useCases.deleteTodo("1") }
    }

    // ─── DeleteAllTodosUseCase ────────────────────────────────────

    @Test
    fun `deleteAllTodos clears all todos`() = runTest {
        todoRepo.seedTodos(todo1, todo2, todo3)
        useCases.deleteAllTodos()
        assertTrue(todoRepo.allTodos().isEmpty())
    }

    @Test
    fun `deleteAllTodos on empty repo does not throw`() = runTest {
        useCases.deleteAllTodos() // no todos seeded
        assertTrue(todoRepo.allTodos().isEmpty())
    }

    @Test
    fun `deleteAllTodos throws when repo throws`() = runTest {
        todoRepo.shouldThrow = true
        assertFailsWith<Exception> { useCases.deleteAllTodos() }
    }
}