package com.care.kmp.data

import com.care.kmp.AppDatabase
import com.care.kmp.database.LocalDatabase
import com.care.kmp.domain.User
import com.care.kmp.domain.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRepositoryImpl(
    private val database: LocalDatabase
) : UserRepository {

    override suspend fun insert(name: String) {
        database.insert(name)
    }

    override fun getUsers(): Flow<List<User>> = flow {
        emit(
            database.getAll()
        )
    }
}