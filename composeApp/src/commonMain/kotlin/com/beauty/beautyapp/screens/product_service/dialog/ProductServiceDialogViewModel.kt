package com.beauty.beautyapp.screens.product_service.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beauty.beautyapp.data.remote.BeautyApi
import com.beauty.beautyapp.model.Product
import com.beauty.beautyapp.model.Service
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductServiceDialogViewModel(
    private val beautyApi: BeautyApi
) : ViewModel() {
    private val _state = MutableStateFlow(ProductDialogState())
    val state: StateFlow<ProductDialogState> = _state.asStateFlow()

    fun registerProduct(product: Product) {
        viewModelScope.launch {
            val registeredProduct = beautyApi.registerProduct(product)
            _state.value = _state.value.copy(registeredProduct = registeredProduct)
        }
    }

    fun registerService(service: Service) {
        viewModelScope.launch {
            val registeredService = beautyApi.registerService(service)
            _state.value = _state.value.copy(registeredService = registeredService)
        }
    }

    fun resetState() {
        _state.value = _state.value.copy(
            registeredProduct = null,
            registeredService = null,
            dialogType = DialogType.SERVICE
        )
    }

    fun updateDialogType(type: DialogType) {
        _state.value = _state.value.copy(
            dialogType = type
        )
    }
}

data class ProductDialogState(
    val registeredProduct: Product? = null,
    val registeredService: Service? = null,
    val dialogType: DialogType = DialogType.SERVICE
)

enum class DialogType {
    SERVICE, PRODUCT
}