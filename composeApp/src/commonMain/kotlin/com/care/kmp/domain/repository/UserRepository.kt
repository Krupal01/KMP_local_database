package com.care.kmp.domain.repository

import com.care.kmp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun insert(name: String)
    fun getUsers(): Flow<List<User>>

    suspend fun updateUser(id: Long, name: String)

    suspend fun deleteUser(id: Long)
}