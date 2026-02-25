package com.care.kmp.data

import com.care.kmp.AppDatabase
import com.care.kmp.database.LocalDatabase
import com.care.kmp.domain.User
import com.care.kmp.domain.UserRepository
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
            database.insert(name)
        }
    }

    override fun getUsers(): Flow<List<User>> = flow {
        if(getPlatform().name.equals("Web with Kotlin/JS")){
            emit(
                apiService.getData()
            )
        }else {
            emit(
                database.getAll()
            )
        }
    }
}