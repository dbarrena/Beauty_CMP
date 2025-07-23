package com.jetbrains.kmpapp.screens.pos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.kmpapp.data.BeautyApi
import com.jetbrains.kmpapp.model.Service
import com.jetbrains.kmpapp.model.Product
import com.jetbrains.kmpapp.model.BeautyItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PosViewModel(private val beautyApi: BeautyApi): ViewModel() {
    private val _state = MutableStateFlow(PosModelState())
    val state: StateFlow<PosModelState> = _state.asStateFlow()

    init {
        getAvailableItems()
    }

    fun getAvailableItems() {
        viewModelScope.launch {
            val products = beautyApi.getProducts()
            val services = beautyApi.getServices()
            val items: List<BeautyItem> = products + services
            _state.value = _state.value.copy(availableItems = items)
        }
    }

    fun updateItemsList(item: BeautyItem) {
        _state.value = state.value.copy(
            selectedItems = state.value.selectedItems + item,
            totalPrice = state.value.totalPrice + (when (item) {
                is Service -> item.price.toDoubleOrNull() ?: 0.0
                is Product -> item.price.toDoubleOrNull() ?: 0.0
            })
        )
    }
}

data class PosModelState(
    val availableItems: List<BeautyItem> = emptyList(),
    val selectedItems: List<BeautyItem> = emptyList(),
    val totalPrice: Double = 0.0
)