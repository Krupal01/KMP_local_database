package com.care.kmp.repository

import com.care.kmp.domain.model.User
import com.care.kmp.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeUserRepository : UserRepository {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    var shouldThrow = false

    override suspend fun insert(name: String) {
        if (shouldThrow) throw Exception("DB error")
        val updated = _users.value + User(id = _users.value.size.toLong() + 1, name = name)
        _users.value = updated
    }

    override fun getUsers(): Flow<List<User>> {
        if (shouldThrow) throw Exception("DB error")
        return _users.asStateFlow()
    }

    override suspend fun updateUser(id: Long, name: String) {
        if (shouldThrow) throw Exception("DB error")
        _users.value = _users.value.map {
            if (it.id == id) it.copy(name = name) else it
        }
    }

    override suspend fun deleteUser(id: Long) {
        if (shouldThrow) throw Exception("DB error")
        _users.value = _users.value.filter { it.id != id }
    }

    // ─── Test Helpers ─────────────────────────────────────────────
    fun seedUsers(vararg user: User) { _users.value = user.toList() }
    fun allUsers() = _users.value
    fun reset() { _users.value = emptyList(); shouldThrow = false }
}