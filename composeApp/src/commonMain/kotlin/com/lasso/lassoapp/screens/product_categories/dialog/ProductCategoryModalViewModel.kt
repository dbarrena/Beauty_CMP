package com.lasso.lassoapp.screens.product_categories.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.model.Product
import com.lasso.lassoapp.model.ProductCategory
import com.lasso.lassoapp.model.Service
import kotlinx.coroutines.CancellationException
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
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val registeredProduct = lassoApi.registerProductCategory(productCategory)
                _state.value = _state.value.copy(registeredProductCategory = registeredProduct, isLoading = false)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = error.message ?: "No se pudo guardar la categoría")
            }
        }
    }

    fun resetState() {
        _state.value = _state.value.copy(
            isLoading = false,
            registeredProductCategory = null,
            error = null,
        )
    }
}

data class ProductDialogState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val registeredProductCategory: ProductCategory? = null
)
