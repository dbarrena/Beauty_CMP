package com.lasso.lassoapp.screens.pos.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.model.Payment
import com.lasso.lassoapp.model.Sale
import com.lasso.lassoapp.model.SaleDetail
import com.lasso.lassoapp.screens.pos.SelectedPosItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CheckoutDialogViewModel(private val lassoApi: LassoApi) : ViewModel() {
    private val _state = MutableStateFlow(CheckoutDialogState())
    val state: StateFlow<CheckoutDialogState> = _state.asStateFlow()

    fun registerSale(items: List<SelectedPosItem>) {
        val saleDetails = items.map { selectedPosItem ->
            val item = selectedPosItem.lassoItem

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
                val payments = state.value.payments.map {
                    Payment(
                        paymentType = it.paymentType.value,
                        total = it.total
                    )
                }
                lassoApi.registerSale(
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

    fun updatePayment(oldPaymentType: PaymentType, updatedPosPayment: PosPayment) {
        val updatedPayments = _state.value.payments.map { payment ->
            if (payment.paymentType.value == oldPaymentType.value) {
                updatedPosPayment
            } else payment
        }
        _state.value = _state.value.copy(payments = updatedPayments)

        setRemainingTotal()
    }

    fun addPayment(updatedPosPayment: PosPayment) {
        _state.value = _state.value.copy(payments = _state.value.payments + updatedPosPayment)
        setRemainingTotal()
    }

    fun removePayment(paymentType: PaymentType) {
        val updatedPayments = _state.value.payments.filter { it.paymentType.value != paymentType.value }
        _state.value = _state.value.copy(payments = updatedPayments)
        setRemainingTotal()
    }

    fun setTotal(total: Double) {
        _state.value = _state.value.copy(total = total, remainingTotal = total)
    }

    fun setRemainingTotal() {
        _state.value = _state.value.copy(
            remainingTotal = _state.value.total - _state.value.payments.sumOf { it.total }
        )
    }

    fun getPaymentTypes(): List<PaymentType> {
        val availablePaymentTypes = listOf(
            PaymentType("Efectivo", "cash"),
            PaymentType("Tarjeta", "card"),
            PaymentType("Transferencia", "transfer")
        )

        return availablePaymentTypes.filter {
            state.value.payments.none { payment -> payment.paymentType.value == it.value }
        }
    }
}

data class CheckoutDialogState(
    val total: Double = 0.0,
    val remainingTotal: Double = 0.0,
    val payments: List<PosPayment> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

data class PosPayment(
    val paymentType: PaymentType,
    val total: Double
)

data class PaymentType(
    val display: String,
    val value: String
)