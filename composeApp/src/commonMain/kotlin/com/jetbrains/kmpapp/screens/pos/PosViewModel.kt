package com.jetbrains.kmpapp.screens.pos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.kmpapp.data.BeautyApi
import com.jetbrains.kmpapp.model.Service
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PosViewModel(private val beautyApi: BeautyApi): ViewModel() {
    private val _state = MutableStateFlow(PosModelState())
    val state: StateFlow<PosModelState> = _state.asStateFlow()

    init {
        getAvailableServices()
    }

    fun getAvailableServices() {
        viewModelScope.launch {
            val availableServices = beautyApi.getServices()
            _state.value = _state.value.copy(availableServices = availableServices)
        }
    }

    fun updateServicesList(services: Service) {
        _state.value = state.value.copy(selectedServices = state.value.selectedServices + services, totalPrice = state.value.totalPrice + services.price.toDouble())
    }
}

data class PosModelState(
    val availableServices: List<Service> = emptyList(),
    val selectedServices: List<Service> = emptyList(),
    val totalPrice: Double = 0.0
)