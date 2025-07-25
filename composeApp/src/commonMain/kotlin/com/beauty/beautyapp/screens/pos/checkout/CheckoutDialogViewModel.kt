package com.beauty.beautyapp.screens.pos.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beauty.beautyapp.data.BeautyApi
import com.beauty.beautyapp.model.BeautyItem
import com.beauty.beautyapp.model.Sale
import com.beauty.beautyapp.model.SaleDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CheckoutDialogViewModel(private val beautyApi: BeautyApi): ViewModel() {
    private val _state = MutableStateFlow(CheckoutDialogState())
    val state: StateFlow<CheckoutDialogState> = _state.asStateFlow()

    fun registerSale(items: List<BeautyItem>) {
        val saleDetails = items.map { item ->
            SaleDetail(
                itemType = item.type,
                itemId = item.id ?: 0,
                quantity = 1,
                price = item.price.toDouble()
            )
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                beautyApi.registerSale(Sale(partnerId = 1, saleDetails = saleDetails))
                _state.value = _state.value.copy(isLoading = false, success = true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun restartCheckout() {
        _state.value = CheckoutDialogState()
    }
}

data class CheckoutDialogState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)