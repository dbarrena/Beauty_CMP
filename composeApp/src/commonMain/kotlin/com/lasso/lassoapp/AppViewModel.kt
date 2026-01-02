package com.lasso.lassoapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lasso.lassoapp.data.local.session.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppViewModel(
    private val sessionRepository: SessionRepository
) : ViewModel() {
    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = AppState(isLoggedIn = sessionRepository.isLoggedIn())
        }
    }

    fun setLoggedIn() {
        _state.value = AppState(isLoggedIn = true)
    }

    fun setLoggedOut() {
        _state.value = AppState(isLoggedIn = false)
    }
}

data class AppState(
    val isLoggedIn: Boolean? = null
)