package com.beauty.beautyapp.screens.configuration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beauty.beautyapp.data.local.session.SessionRepository
import com.beauty.beautyapp.data.remote.BeautyApi
import com.beauty.beautyapp.model.room.Session
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConfigurationViewModel(private val beautyApi: BeautyApi, private val sessionRepository: SessionRepository) : ViewModel() {
    private val _state = MutableStateFlow(ConfigurationScreenState())
    val state: StateFlow<ConfigurationScreenState> = _state.asStateFlow()

    init {
        getSession()
    }

    private fun getSession() {
        viewModelScope.launch {
            sessionRepository.getSession()?.let {
                _state.value = _state.value.copy(session = it)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionRepository.removeSession()
            _state.value = _state.value.copy(logoutEventCompleted = true)
        }
    }

    fun getSessionByEmployeeId(employeeId: Int) {
        viewModelScope.launch {
            beautyApi.getEmployeeById(employeeId)?.let {
                sessionRepository.saveSession(it)
            }

            _state.value = _state.value.copy(session = sessionRepository.getSession())
        }
    }

    data class ConfigurationScreenState(
        val session: Session? = null,
        val logoutEventCompleted: Boolean = false
    )
}