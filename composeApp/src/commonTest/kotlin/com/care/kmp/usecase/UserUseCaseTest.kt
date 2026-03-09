package com.care.kmp.usecase

import com.care.kmp.base.BaseTest
import com.care.kmp.domain.model.User
import com.care.kmp.domain.usecase.UserUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class UserUseCaseTest : BaseTest(), KoinTest {

    private val userUseCase: UserUseCase by inject()

    // ─── getUsers ─────────────────────────────────────────────────

    @Test
    fun `getUsers returns empty flow initially`() = runTest {
        val result = userUseCase.getUsers().first()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getUsers returns seeded users`() = runTest {
        userRepo.seedUsers(
            User(id = 1, name = "Alice"),
            User(id = 2, name = "Bob")
        )
        val result = userUseCase.getUsers().first()
        assertEquals(2, result.size)
        assertEquals("Alice", result[0].name)
    }

    @Test
    fun `getUsers reflects live updates`() = runTest {
        userRepo.seedUsers(User(id = 1, name = "Alice"))
        val before = userUseCase.getUsers().first()
        assertEquals(1, before.size)

        userUseCase.insertUser("Bob")

        val after = userUseCase.getUsers().first()
        assertEquals(2, after.size)
    }

    // ─── insertUser ───────────────────────────────────────────────

    @Test
    fun `insertUser adds user to repository`() = runTest {
        userUseCase.insertUser("Alice")
        val users = userUseCase.getUsers().first()
        assertEquals(1, users.size)
        assertEquals("Alice", users.first().name)
    }

    @Test
    fun `insertUser adds multiple users`() = runTest {
        userUseCase.insertUser("Alice")
        userUseCase.insertUser("Bob")
        userUseCase.insertUser("Charlie")
        assertEquals(3, userUseCase.getUsers().first().size)
    }

    @Test
    fun `insertUser throws when repo throws`() = runTest {
        userRepo.shouldThrow = true
        assertFailsWith<Exception> { userUseCase.insertUser("Alice") }
    }

    // ─── updateUser ───────────────────────────────────────────────

    @Test
    fun `update changes user name`() = runTest {
        userRepo.seedUsers(User(id = 1, name = "Alice"))
        userUseCase.update(id = 1, name = "Alice Updated")
        val result = userUseCase.getUsers().first()
        assertEquals("Alice Updated", result.first().name)
    }

    @Test
    fun `update does not affect other users`() = runTest {
        userRepo.seedUsers(
            User(id = 1, name = "Alice"),
            User(id = 2, name = "Bob")
        )
        userUseCase.update(id = 1, name = "Alice Updated")
        val bob = userUseCase.getUsers().first().find { it.id == 2L }
        assertEquals("Bob", bob?.name)
    }

    @Test
    fun `update throws when repo throws`() = runTest {
        userRepo.shouldThrow = true
        assertFailsWith<Exception> { userUseCase.update(1L, "X") }
    }

    // ─── deleteUser ───────────────────────────────────────────────

    @Test
    fun `delete removes correct user`() = runTest {
        userRepo.seedUsers(
            User(id = 1, name = "Alice"),
            User(id = 2, name = "Bob")
        )
        userUseCase.delete(1L)
        val remaining = userUseCase.getUsers().first()
        assertEquals(1, remaining.size)
        assertEquals("Bob", remaining.first().name)
    }

    @Test
    fun `delete on non-existent id does not throw`() = runTest {
        userRepo.seedUsers(User(id = 1, name = "Alice"))
        userUseCase.delete(999L)
        assertEquals(1, userUseCase.getUsers().first().size)
    }

    @Test
    fun `delete throws when repo throws`() = runTest {
        userRepo.shouldThrow = true
        assertFailsWith<Exception> { userUseCase.delete(1L) }
    }
}