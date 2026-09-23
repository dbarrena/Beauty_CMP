package com.lasso.lassoapp.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lasso.lassoapp.data.local.session.SessionRepository
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.model.Login
import kotlinx.coroutines.CancellationException
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
        if (_state.value.isLoading) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val loginResult = lassoApi.login(Login(username, password))
                val employee = loginResult.employee
                val token = loginResult.token

                if (employee != null && !token.isNullOrBlank()) {
                    sessionRepository.saveSession(employee, token)
                    _state.value = _state.value.copy(isLoading = false)
                    onLoginSuccess()
                } else {
                    _state.value = _state.value.copy(
                        error = loginResult.error ?: "No se recibió un token de sesión válido",
                        isLoading = false,
                    )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _state.value = _state.value.copy(
                    error = exception.message ?: "No se pudo iniciar sesión. Inténtalo de nuevo.",
                    isLoading = false,
                )
            }
        }
    }
}

data class LoginScreenState(
    val error: String? = null,
    val isLoading: Boolean = false
)
