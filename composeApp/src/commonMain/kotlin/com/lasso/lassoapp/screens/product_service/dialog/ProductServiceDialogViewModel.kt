package com.lasso.lassoapp.screens.product_service.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.model.Product
import com.lasso.lassoapp.model.ProductCategory
import com.lasso.lassoapp.model.Service
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductServiceDialogViewModel(
    private val lassoApi: LassoApi
) : ViewModel() {
    private val _state = MutableStateFlow(ProductDialogState())
    val state: StateFlow<ProductDialogState> = _state.asStateFlow()

    init {
        getProductCategories()
    }

    fun registerProduct(product: Product) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val registeredProduct = lassoApi.registerProduct(product)
            _state.value = _state.value.copy(registeredProduct = registeredProduct, isLoading = false)
        }
    }

    fun registerService(service: Service) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val registeredService = lassoApi.registerService(service)
            _state.value = _state.value.copy(registeredService = registeredService, isLoading = false)
        }
    }

    fun resetState() {
        _state.value = _state.value.copy(
            isLoading = false,
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

    fun getProductCategories() {
        viewModelScope.launch {
            try {
                val categories = lassoApi.getProductCategories()
                _state.value = _state.value.copy(productCategories = categories)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }
}

data class ProductDialogState(
    val isLoading: Boolean = false,
    val productCategories: List<ProductCategory> = emptyList(),
    val registeredProduct: Product? = null,
    val registeredService: Service? = null,
    val dialogType: DialogType = DialogType.SERVICE
)

enum class DialogType {
    SERVICE, PRODUCT
}