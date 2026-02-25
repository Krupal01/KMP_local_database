package com.care.kmp.domain

class InsertUserUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(name: String) {
        repository.insert(name)
    }
}

class GetUsersUseCase(
    private val repository: UserRepository
) {
    operator fun invoke() = repository.getUsers()
}