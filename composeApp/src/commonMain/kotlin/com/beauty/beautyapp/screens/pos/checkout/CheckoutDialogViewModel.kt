package com.beauty.beautyapp.screens.pos.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beauty.beautyapp.data.remote.BeautyApi
import com.beauty.beautyapp.model.Payment
import com.beauty.beautyapp.model.Sale
import com.beauty.beautyapp.model.SaleDetail
import com.beauty.beautyapp.screens.pos.SelectedPosItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CheckoutDialogViewModel(private val beautyApi: BeautyApi) : ViewModel() {
    private val _state = MutableStateFlow(CheckoutDialogState())
    val state: StateFlow<CheckoutDialogState> = _state.asStateFlow()

    fun registerSale(items: List<SelectedPosItem>) {
        val saleDetails = items.map { selectedPosItem ->
            val item = selectedPosItem.beautyItem

            SaleDetail(
                itemType = item.type,
                itemId = item.id ?: 0,
                quantity = selectedPosItem.quantity,
                price = selectedPosItem.price.toDouble()
            )
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val payments = listOf(
                    Payment(
                        paymentType = state.value.paymentType,
                        total = state.value.total
                    )
                )
                beautyApi.registerSale(
                    Sale(
                        saleDetails = saleDetails,
                        payments = payments
                    )
                )
                _state.value = _state.value.copy(isLoading = false, success = true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun restartCheckout() {
        _state.value = CheckoutDialogState()
    }

    fun updatePaymentType(paymentType: String) {
        _state.value = _state.value.copy(paymentType = paymentType)
    }

    fun setTotal(total: Double) {
        _state.value = _state.value.copy(total = total)
    }
}

data class CheckoutDialogState(
    val total: Double = 0.0,
    val paymentType: String = "card",
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)