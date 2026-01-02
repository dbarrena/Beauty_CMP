package com.lasso.lassoapp.screens.product_service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.model.LassoItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductServiceViewModel(private val lassoApi: LassoApi) : ViewModel() {
    private val _state = MutableStateFlow(ProductServiceModelState())
    val state: StateFlow<ProductServiceModelState> = _state.asStateFlow()

    init {
        _state.value = _state.value.copy(isLoading = true)
        getAvailableItems()
    }

    fun getAvailableItems() {
        viewModelScope.launch {
            val products = lassoApi.getProducts()
            val services = lassoApi.getServices()
            val items: List<LassoItem> = products + services
            _state.value = _state.value.copy(availableItems = items, isLoading = false)
        }
    }

    fun showDialog(item: LassoItem? = null) {
        _state.value = _state.value.copy(isDialogDisplayed = true, selectedItem = item)
    }

    fun hideDialog() {
        _state.value = _state.value.copy(isDialogDisplayed = false, selectedItem = null)
    }

    fun updateItemsList(item: LassoItem) {
        _state.value = state.value.copy(
            availableItems = state.value.availableItems + item
        )
    }
}

data class ProductServiceModelState(
    val availableItems: List<LassoItem> = emptyList(),
    val selectedItem: LassoItem? = null,
    val isDialogDisplayed: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)