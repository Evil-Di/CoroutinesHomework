package ru.otus.coroutineshomework.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.otus.coroutineshomework.ui.login.data.Credentials
import ru.otus.coroutineshomework.ui.login.data.User

class LoginViewModel(private val loginApi: LoginApi = LoginApi()) : ViewModel() {

    private val _state = MutableLiveData<LoginViewState>(LoginViewState.Login())
    val state: LiveData<LoginViewState> = _state

    /**
     * Login to the network
     * @param name user name
     * @param password user password
     */
    fun login(name: String, password: String) {
        viewModelScope.launch {
            _state.value = LoginViewState.LoggingIn
            Log.i("login state", _state.value.toString())
            try {
                if (name == "main") {
                    loginApi.login(Credentials(name, password))
                }
                else {
                    val user: User = withContext(Dispatchers.IO) {
                        loginApi.login(Credentials(name, password))
                    }
                    _state.value = LoginViewState.Content(user)
                    Log.i("login state", _state.value.toString())
                }
            }
            catch (exception: Exception){
                _state.value = LoginViewState.Login(error = exception)
                Log.i("login state", _state.value.toString())
            }
        }
    }

    /**
     * Logout from the network
     */
    fun logout() {
        viewModelScope.launch {
            _state.value = LoginViewState.LoggingOut
            Log.i("login state", _state.value.toString())
            try {
                withContext(Dispatchers.IO) {
                    loginApi.logout()
                }
                _state.value =LoginViewState.Login()
                Log.i("login state", _state.value.toString())
            }
            catch (exception: Exception){
                _state.value = LoginViewState.Login(error = exception)
                Log.i("login state", _state.value.toString())
            }
        }
    }
}
