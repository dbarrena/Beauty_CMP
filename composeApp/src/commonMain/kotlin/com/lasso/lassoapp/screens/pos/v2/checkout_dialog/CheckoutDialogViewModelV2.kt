package com.lasso.lassoapp.screens.pos.v2.checkout_dialog

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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CheckoutDialogViewModelV2(private val lassoApi: LassoApi) : ViewModel() {
    private val _state = MutableStateFlow(CheckoutDialogState())
    val state: StateFlow<CheckoutDialogState> = _state.asStateFlow()

    fun navigateToMethodPicker() {
        _state.update {
            it.copy(step = CheckoutStep.MethodPicker, error = null)
        }
    }

    fun navigateToSplitPayment(checkoutPaymentMethod: CheckoutPaymentMethod) {
        _state.update {
            it.copy(
                step = CheckoutStep.SplitPayment(checkoutPaymentMethod),
                error = null,
            )
        }
    }

    /**
     * Restores the dialog to the method picker. Call when the checkout dialog is shown so a reused
     * ViewModel does not resume a completed or stale step.
     */
    fun resetCheckoutFlow() {
        _state.value = CheckoutDialogState()
    }

    fun registerSale(items: List<SelectedPosItem>, unprocessedPayments: List<PosPayment>) {
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
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val payments = unprocessedPayments.map {
                    Payment(
                        paymentType = it.paymentType.key,
                        total = it.total
                    )
                }
                lassoApi.registerSale(
                    Sale(
                        saleDetails = saleDetails,
                        payments = payments
                    )
                )
                val collectedAmount = unprocessedPayments.sumOf { it.total }
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = null,
                        step = CheckoutStep.Success(collectedAmount = collectedAmount),
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, error = e.message)
                }
            }
        }
    }

    fun registerPayments(payments: List<PosPayment>) {
        _state.update { it.copy(payments = it.payments + payments) }
    }

    data class CheckoutDialogState(
        val step: CheckoutStep = CheckoutStep.MethodPicker,
        val total: Double = 0.0,
        val remainingTotal: Double = 0.0,
        val payments: List<PosPayment> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
    )

    data class PosPayment(
        val paymentType: CheckoutPaymentMethod,
        val total: Double
    )
}