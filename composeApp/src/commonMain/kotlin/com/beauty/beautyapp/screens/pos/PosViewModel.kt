package com.beauty.beautyapp.screens.pos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beauty.beautyapp.data.remote.BeautyApi
import com.beauty.beautyapp.model.BeautyItem
import com.beauty.beautyapp.model.Product
import com.beauty.beautyapp.model.Service
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class PosViewModel(private val beautyApi: BeautyApi) : ViewModel() {
    private val _state = MutableStateFlow(PosModelState())
    val state: StateFlow<PosModelState> = _state.asStateFlow()

    init {
        getAvailableItems()
    }

    fun getAvailableItems() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isAvailableItemsLoading = true)
            val products = beautyApi.getProducts()
            val services = beautyApi.getServices()
            val items: List<BeautyItem> = products + services
            _state.value = _state.value.copy(availableItems = items, isAvailableItemsLoading = false)
        }
    }

    fun updateItemsList(item: BeautyItem) {
        _state.value = state.value.copy(
            selectedPosItems = state.value.selectedPosItems + SelectedPosItem(beautyItem = item),
            totalPrice = state.value.totalPrice + (when (item) {
                is Service -> item.price.toDoubleOrNull() ?: 0.0
                is Product -> item.price.toDoubleOrNull() ?: 0.0
            })
        )
    }

    fun restartPos() {
        _state.value = state.value.copy(
            selectedPosItems = emptyList(),
            totalPrice = 0.0
        )
    }

    fun removeItem(beautyItem: SelectedPosItem) {
        println("Removing item: $beautyItem")
        val updatedItems = state.value.selectedPosItems.filter {
            it.instanceId != beautyItem.instanceId
        }

        _state.value = state.value.copy(
            selectedPosItems = updatedItems,
            totalPrice = updatedItems.sumOf { it.beautyItem.price.toDoubleOrNull() ?: 0.0 }
        )
    }
}

data class PosModelState(
    val isAvailableItemsLoading: Boolean = false,
    val availableItems: List<BeautyItem> = emptyList(),
    val selectedPosItems: List<SelectedPosItem> = emptyList(),
    val totalPrice: Double = 0.0
)

@OptIn(ExperimentalUuidApi::class)
data class SelectedPosItem(
    val instanceId: String = Uuid.random().toString(),
    val beautyItem: BeautyItem
)