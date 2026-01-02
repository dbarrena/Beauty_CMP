package com.lasso.lassoapp.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lasso.lassoapp.data.local.session.SessionRepository
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.model.Login
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginScreenViewModel(
    private val lassoApi: LassoApi,
    private val sessionRepository: SessionRepository
) : ViewModel() {
    private val _state = MutableStateFlow(LoginScreenState())
    val state: StateFlow<LoginScreenState> = _state.asStateFlow()

    fun login(username: String, password: String, onLoginSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val loginResult = lassoApi.login(Login(username, password))

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