package com.lasso.lassoapp.screens.configuration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lasso.lassoapp.data.local.session.SessionRepository
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.model.room.Session
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConfigurationViewModel(private val lassoApi: LassoApi, private val sessionRepository: SessionRepository) : ViewModel() {
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
            lassoApi.getEmployeeById(employeeId)?.let {
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