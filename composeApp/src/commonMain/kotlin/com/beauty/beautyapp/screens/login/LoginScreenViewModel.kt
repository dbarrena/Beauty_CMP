package com.beauty.beautyapp.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beauty.beautyapp.data.local.session.SessionRepository
import com.beauty.beautyapp.data.remote.BeautyApi
import com.beauty.beautyapp.model.Login
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginScreenViewModel(
    private val beautyApi: BeautyApi,
    private val sessionRepository: SessionRepository
) : ViewModel() {
    private val _state = MutableStateFlow(LoginScreenState())
    val state: StateFlow<LoginScreenState> = _state.asStateFlow()

    fun login(username: String, password: String, onLoginSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val loginResult = beautyApi.login(Login(username, password))

            loginResult.employee?.let {
                sessionRepository.saveSession(it)
                onLoginSuccess()
            } ?: run {
                _state.value = _state.value.copy(
                    error = loginResult.error ?: "Unknown error",
                    isLoading = false
                )
            }
        }
    }
}

data class LoginScreenState(
    val error: String? = null,
    val isLoading: Boolean = false
)