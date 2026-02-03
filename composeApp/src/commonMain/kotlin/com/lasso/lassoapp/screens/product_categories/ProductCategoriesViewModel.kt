package com.lasso.lassoapp.screens.product_categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.model.LassoItem
import com.lasso.lassoapp.model.ProductCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductCategoriesViewModel(private val lassoApi: LassoApi) : ViewModel() {
    private val _state = MutableStateFlow(ProductCategoriesModelState())
    val state: StateFlow<ProductCategoriesModelState> = _state.asStateFlow()

    init {
        _state.value = _state.value.copy(isLoading = true)
        getAvailableItems()
    }

    fun getAvailableItems() {
        viewModelScope.launch {
            val productCategories = lassoApi.getProductCategories()
            val items: List<ProductCategory> = productCategories
            _state.value = _state.value.copy(availableItems = items, isLoading = false)
        }
    }

    fun showDialog(item: ProductCategory? = null) {
        _state.value = _state.value.copy(isDialogDisplayed = true, selectedItem = item)
    }

    fun hideDialog() {
        _state.value = _state.value.copy(isDialogDisplayed = false, selectedItem = null)
    }

    fun updateItemsList(item: ProductCategory) {
        _state.value = state.value.copy(
            availableItems = state.value.availableItems + item
        )
    }
}

data class ProductCategoriesModelState(
    val availableItems: List<ProductCategory> = emptyList(),
    val selectedItem: ProductCategory? = null,
    val isDialogDisplayed: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)