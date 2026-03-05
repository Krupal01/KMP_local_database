package com.care.kmp.data.repository

import com.care.kmp.data.local.LocalDatabase
import com.care.kmp.data.network.ApiService
import com.care.kmp.domain.model.User
import com.care.kmp.domain.repository.UserRepository
import com.care.kmp.getPlatform
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRepositoryImpl(
    private val database: LocalDatabase,
    private val apiService: ApiService
) : UserRepository {

    override suspend fun insert(name: String) {
        if(getPlatform().name.equals("Web with Kotlin/JS")){
            apiService.addUser(name)
        }
        else {
            database.insertUser(name)
        }
    }

    override fun getUsers(): Flow<List<User>> = flow {
        if (getPlatform().name.equals("Web with Kotlin/JS")) {
            emit(
                apiService.getAllUsers()
            )
        } else {
            emit(
                database.getAllUser()
            )
        }
    }

    override suspend fun updateUser(id: Long, name: String) {
        if(getPlatform().name.equals("Web with Kotlin/JS")){
            apiService.updateUser(id, name)
        }
        else {
            database.updateUser(id,name)
        }
    }

    override suspend fun deleteUser(id: Long) {
        if(getPlatform().name.equals("Web with Kotlin/JS")){
            apiService.deleteUser(id)
        }
        else {
            database.deleteUser(id)
        }
    }
}