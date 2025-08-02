package com.beauty.beautyapp.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beauty.beautyapp.data.local.session.SessionRepository
import com.beauty.beautyapp.data.remote.BeautyApi
import com.beauty.beautyapp.model.Home
import com.beauty.beautyapp.screens.product_service.dialog.ProductDialogState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val beautyApi: BeautyApi,
    private val sessionRepository: SessionRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HomeScreenState())
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            sessionRepository.getSession()?.let { session ->
                _state.value = _state.value.copy(
                    partnerName = session.partnerName,
                    employeeName = session.employeeName
                )
            }

            _state.value = _state.value.copy(isLoading = true)

            try {
                val home = beautyApi.getHome()
                _state.value = _state.value.copy(home = home)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }
}

data class HomeScreenState(
    val isLoading: Boolean = false,
    val partnerName: String = "",
    val employeeName: String = "",
    val home: Home? = null,
    val error: String? = null
)