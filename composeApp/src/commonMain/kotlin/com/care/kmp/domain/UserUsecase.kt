package com.care.kmp.domain

class UserUseCase(
    private val repository: UserRepository
) {

    suspend fun insertUser(name: String) {
        repository.insert(name)
    }

    fun getUsers() = repository.getUsers()

    suspend fun update(id: Long, name: String) {
        repository.updateUser(id, name)
    }

    suspend fun delete(id: Long) {
        repository.deleteUser(id)
    }
}