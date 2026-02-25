package com.care.kmp.presentation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.care.kmp.data.ApiService
import com.care.kmp.data.UserRepositoryImpl
import com.care.kmp.database.LocalDatabase
import com.care.kmp.domain.GetUsersUseCase
import com.care.kmp.domain.InsertUserUseCase
import com.care.kmp.domain.User
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.reflect.KClass

// INTENT
sealed interface UserIntent {
    data class AddUser(val name: String) : UserIntent
    data object LoadUsers : UserIntent
}

// STATE
data class UserState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false
)

class UserViewModel(
    private val insertUser: suspend (String) -> Unit,
    private val getUsers: () -> Flow<List<User>>
) : ViewModel() {

    private val scope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _state = MutableStateFlow(UserState())
    val state: StateFlow<UserState> = _state.asStateFlow()

    fun onIntent(intent: UserIntent) {
        when (intent) {
            is UserIntent.AddUser -> addUser(intent.name)
            UserIntent.LoadUsers -> loadUsers()
        }
    }

    private fun loadUsers() {
        scope.launch {
            _state.update { it.copy(isLoading = true) }

            getUsers().collect { users ->
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
            insertUser(name)
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
            insertUser = { InsertUserUseCase(UserRepositoryImpl(localDatabase, apiService)).invoke(it) },
            getUsers = { GetUsersUseCase(UserRepositoryImpl(localDatabase, apiService)).invoke() }
        ) as T
    }
}
