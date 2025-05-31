package ru.otus.coroutineshomework.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.otus.coroutineshomework.ui.login.data.Credentials
import ru.otus.coroutineshomework.ui.login.data.User

class LoginViewModel(private val loginApi: LoginApi = LoginApi()) : ViewModel() {

    private val stateFlow = MutableStateFlow<LoginViewState>(LoginViewState.Login())
    val state: StateFlow<LoginViewState> = stateFlow

    private fun loginFlow(credentials: Credentials?): Flow<LoginViewState> {
        return flow {
            try {
                if (credentials != null) {
                    emit(LoginViewState.LoggingIn)
                    val user: User = withContext(Dispatchers.IO) {
                        loginApi.login(credentials)
                    }
                    emit(LoginViewState.Content(user))
                } else {
                    emit(LoginViewState.LoggingOut)
                    withContext(Dispatchers.IO) {
                        loginApi.logout()
                    }
                    emit(LoginViewState.Login())
                }
            }
             catch (exception: Exception) {
                emit(LoginViewState.Login(error = exception))
            }
        }
    }

    /**
     * Login to the network
     * @param name user name
     * @param password user password
     */
    fun login(name: String, password: String) {
        viewModelScope.launch {
            loginFlow(Credentials(name, password)).collect {
                stateFlow.value = it
            }
        }
    }

    /**
     * Logout from the network
     */
    fun logout() {
        viewModelScope.launch {
            loginFlow(null).collect {
                stateFlow.value = it
            }
        }
    }
}
