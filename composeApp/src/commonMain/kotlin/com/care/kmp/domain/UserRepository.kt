package com.care.kmp.domain


import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun insert(name: String)
    fun getUsers(): Flow<List<User>>
}