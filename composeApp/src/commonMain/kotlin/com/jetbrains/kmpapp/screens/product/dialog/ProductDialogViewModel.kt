package com.jetbrains.kmpapp.screens.product.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.kmpapp.data.BeautyApi
import com.jetbrains.kmpapp.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductDialogViewModel(private val beautyApi: BeautyApi): ViewModel() {
    private val _state = MutableStateFlow(ProductDialogState())
    val state: StateFlow<ProductDialogState> = _state.asStateFlow()

    fun registerProduct(product: Product) {
        viewModelScope.launch {
            val registeredProduct = beautyApi.registerProduct(product)
            _state.value = _state.value.copy(registeredProduct = registeredProduct)
        }
    }
}

data class ProductDialogState(
    val registeredProduct: Product? = null,
)