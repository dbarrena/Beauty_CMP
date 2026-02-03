package com.lasso.lassoapp.screens.product_categories.dialog

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

class ProductCategoryModalViewModel(
    private val lassoApi: LassoApi
) : ViewModel() {
    private val _state = MutableStateFlow(ProductDialogState())
    val state: StateFlow<ProductDialogState> = _state.asStateFlow()

    fun registerProductCategory(productCategory: ProductCategory) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val registeredProduct = lassoApi.registerProductCategory(productCategory)
            _state.value = _state.value.copy(registeredProductCategory = registeredProduct, isLoading = false)
        }
    }

    fun resetState() {
        _state.value = _state.value.copy(
            isLoading = false,
            registeredProductCategory = null
        )
    }
}

data class ProductDialogState(
    val isLoading: Boolean = false,
    val registeredProductCategory: ProductCategory? = null
)