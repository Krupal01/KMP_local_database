package com.care.kmp.presentation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.care.kmp.data.ApiService
import com.care.kmp.data.UserRepositoryImpl
import com.care.kmp.database.LocalDatabase
import com.care.kmp.domain.User
import com.care.kmp.domain.UserUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.reflect.KClass

// INTENT
sealed interface UserIntent {
    data class AddUser(val name: String) : UserIntent
    data object LoadUsers : UserIntent

    data class UpdateUser(val id: Long, val name: String) : UserIntent
    data class DeleteUser(val id: Long) : UserIntent
}

// STATE
data class UserState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false
)

class UserViewModel(
    private val userUseCase: UserUseCase,
) : ViewModel() {

    private val scope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _state = MutableStateFlow(UserState())
    val state: StateFlow<UserState> = _state.asStateFlow()

    fun onIntent(intent: UserIntent) {
        when (intent) {
            is UserIntent.AddUser -> addUser(intent.name)
            UserIntent.LoadUsers -> loadUsers()
            is UserIntent.DeleteUser -> deleteUser(intent.id)
            is UserIntent.UpdateUser -> updateUser(intent.id, intent.name)
        }
    }

    private fun loadUsers() {
        scope.launch {
            _state.update { it.copy(isLoading = true) }

            userUseCase.getUsers().collect { users ->
                _state.update {
                    it.copy(
                        users = users,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun addUser(name: String) {
        scope.launch {
            userUseCase.insertUser(name)
            loadUsers()
        }
    }

    private fun updateUser(id: Long, name: String) {
        scope.launch {

            _state.update { it.copy(isLoading = true) }

            try {
                userUseCase.update(id, name)
                loadUsers()
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun deleteUser(id: Long) {
        scope.launch {

            _state.update { it.copy(isLoading = true) }

            try {
                userUseCase.delete(id)
                loadUsers()
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}

//object UserViewModelFactory {
//
//    fun create(database: LocalDatabase): UserViewModel {
//        val repository = UserRepositoryImpl(database)
//
//        return UserViewModel(
//            insertUser = { InsertUserUseCase(repository).invoke(it) },
//            getUsers = { GetUsersUseCase(repository).invoke() }
//        )
//    }
//}

class UserViewModelFactory(
    private val localDatabase: LocalDatabase,
    private val apiService: ApiService
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        return UserViewModel(
            userUseCase = UserUseCase(UserRepositoryImpl(localDatabase, apiService))
        ) as T
    }
}
