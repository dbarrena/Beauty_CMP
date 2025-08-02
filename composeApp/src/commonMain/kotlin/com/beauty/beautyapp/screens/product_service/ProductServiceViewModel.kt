package com.beauty.beautyapp.screens.product_service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beauty.beautyapp.data.remote.BeautyApi
import com.beauty.beautyapp.model.BeautyItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductServiceViewModel(private val beautyApi: BeautyApi) : ViewModel() {
    private val _state = MutableStateFlow(ProductServiceModelState())
    val state: StateFlow<ProductServiceModelState> = _state.asStateFlow()

    init {
        _state.value = _state.value.copy(isLoading = true)
        getAvailableItems()
    }

    fun getAvailableItems() {
        viewModelScope.launch {
            val products = beautyApi.getProducts()
            val services = beautyApi.getServices()
            val items: List<BeautyItem> = products + services
            _state.value = _state.value.copy(availableItems = items, isLoading = false)
        }
    }

    fun showDialog(item: BeautyItem? = null) {
        _state.value = _state.value.copy(isDialogDisplayed = true, selectedItem = item)
    }

    fun hideDialog() {
        _state.value = _state.value.copy(isDialogDisplayed = false, selectedItem = null)
    }

    fun updateItemsList(item: BeautyItem) {
        _state.value = state.value.copy(
            availableItems = state.value.availableItems + item
        )
    }
}

data class ProductServiceModelState(
    val availableItems: List<BeautyItem> = emptyList(),
    val selectedItem: BeautyItem? = null,
    val isDialogDisplayed: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)