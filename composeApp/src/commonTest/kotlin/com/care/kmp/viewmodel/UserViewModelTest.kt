package com.care.kmp.viewmodel

import com.care.kmp.base.BaseTest
import com.care.kmp.domain.model.User
import com.care.kmp.domain.usecase.UserUseCase
import com.care.kmp.presentation.viewmodel.UserIntent
import com.care.kmp.presentation.viewmodel.UserViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelTest : BaseTest(), KoinTest {

    private val userUseCase: UserUseCase by inject()
    private lateinit var viewModel: UserViewModel

    @BeforeTest
    fun setup() {
        viewModel = UserViewModel(userUseCase)
    }

    // ─── LoadUsers ────────────────────────────────────────────────

    @Test
    fun `LoadUsers populates state with users`() = runTest {
        userRepo.seedUsers(User(1, "Alice"), User(2, "Bob"))

        viewModel.onIntent(UserIntent.LoadUsers)
        advanceUntilIdle()

        assertEquals(2, viewModel.state.value.users.size)
    }

    @Test
    fun `LoadUsers sets isLoading true then false`() = runTest {
        userRepo.seedUsers(User(1, "Alice"))

        viewModel.onIntent(UserIntent.LoadUsers)
        assertTrue(viewModel.state.value.isLoading)

        advanceUntilIdle()
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `initial state has empty users and not loading`() = runTest {
        val state = viewModel.state.value
        assertTrue(state.users.isEmpty())
        assertFalse(state.isLoading)
    }

    // ─── AddUser ──────────────────────────────────────────────────

    @Test
    fun `AddUser inserts and reloads users`() = runTest {
        viewModel.onIntent(UserIntent.AddUser("Alice"))
        advanceUntilIdle()

        assertEquals(1, viewModel.state.value.users.size)
        assertEquals("Alice", viewModel.state.value.users.first().name)
    }

    @Test
    fun `AddUser multiple times accumulates users`() = runTest {
        viewModel.onIntent(UserIntent.AddUser("Alice"))
        viewModel.onIntent(UserIntent.AddUser("Bob"))
        advanceUntilIdle()

        assertEquals(2, viewModel.state.value.users.size)
    }

    // ─── UpdateUser ───────────────────────────────────────────────

    @Test
    fun `UpdateUser changes user name in state`() = runTest {
        userRepo.seedUsers(User(1, "Alice"))
        viewModel.onIntent(UserIntent.LoadUsers)
        advanceUntilIdle()

        viewModel.onIntent(UserIntent.UpdateUser(id = 1, name = "Alice Updated"))
        advanceUntilIdle()

        assertEquals("Alice Updated", viewModel.state.value.users.find { it.id == 1L }?.name)
    }

    @Test
    fun `UpdateUser does not affect other users`() = runTest {
        userRepo.seedUsers(User(1, "Alice"), User(2, "Bob"))
        viewModel.onIntent(UserIntent.LoadUsers)
        advanceUntilIdle()

        viewModel.onIntent(UserIntent.UpdateUser(id = 1, name = "Alice Updated"))
        advanceUntilIdle()

        assertEquals("Bob", viewModel.state.value.users.find { it.id == 2L }?.name)
    }

    // ─── DeleteUser ───────────────────────────────────────────────

    @Test
    fun `DeleteUser removes user from state`() = runTest {
        userRepo.seedUsers(User(1, "Alice"), User(2, "Bob"))
        viewModel.onIntent(UserIntent.LoadUsers)
        advanceUntilIdle()

        viewModel.onIntent(UserIntent.DeleteUser(id = 1))
        advanceUntilIdle()

        assertNull(viewModel.state.value.users.find { it.id == 1L })
        assertEquals(1, viewModel.state.value.users.size)
    }

    @Test
    fun `DeleteUser on non-existent id does not crash`() = runTest {
        userRepo.seedUsers(User(1, "Alice"))
        viewModel.onIntent(UserIntent.LoadUsers)
        advanceUntilIdle()

        viewModel.onIntent(UserIntent.DeleteUser(id = 999))
        advanceUntilIdle()

        assertEquals(1, viewModel.state.value.users.size)
    }
}