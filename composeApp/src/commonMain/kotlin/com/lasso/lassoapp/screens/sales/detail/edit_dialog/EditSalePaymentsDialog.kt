package com.lasso.lassoapp.screens.sales.detail.edit_dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.lasso.lassoapp.model.Payment
import com.lasso.lassoapp.model.PaymentApiResponse
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.CheckoutDialogViewModelV2
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.CheckoutPayment
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.CheckoutPaymentMethod
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.payment_amount.CheckoutSplitPaymentContent
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.payment_method.CheckoutPaymentMethodPickerContent
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.payment_method.CheckoutPaymentMethodTokens
import com.lasso.lassoapp.ui.theme.LassoOutlineHairline

@Composable
fun EditSalePaymentsDialog(
    payments: List<PaymentApiResponse>,
    total: Double,
    isSaving: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onSave: (List<Payment>) -> Unit,
) {
    var selectedMethod by remember { mutableStateOf<CheckoutPaymentMethod?>(null) }
    val initialPayments = remember(payments) { payments.toCheckoutPayments() }
    val flowState = CheckoutDialogViewModelV2.CheckoutDialogState(
        isLoading = isSaving,
        error = error,
        canSelectEmployee = false,
    )

    Dialog(onDismissRequest = { if (!isSaving) onDismiss() }) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(CheckoutPaymentMethodTokens.cardCornerRadius),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, LassoOutlineHairline),
        ) {
            Box(modifier = Modifier.fillMaxWidth().background(Color.Transparent)) {
                val method = selectedMethod
                if (method == null) {
                    CheckoutPaymentMethodPickerContent(
                        totalPrice = total,
                        title = "Editar métodos de pago",
                        showDiscountAction = false,
                        onClose = onDismiss,
                        onMethodClicked = { selectedMethod = it },
                        onRegisterDiscountClicked = {},
                    )
                } else {
                    val paymentsForMethod = if (method == CheckoutPaymentMethod.Multiple) initialPayments else emptyList()
                    CheckoutSplitPaymentContent(
                        checkoutPaymentMethod = method,
                        totalPrice = total,
                        initialPayments = paymentsForMethod,
                        title = "Editar métodos de pago",
                        showEmployeeSelector = false,
                        state = flowState,
                        onBack = { selectedMethod = null },
                        onClose = onDismiss,
                        onSelectEmployee = {},
                        onRegisterSale = { updatedPayments ->
                            onSave(updatedPayments.map { Payment(it.total, it.paymentType.key) })
                        },
                    )
                }
            }
        }
    }
}

private fun List<PaymentApiResponse>.toCheckoutPayments(): List<CheckoutPayment> = mapNotNull { payment ->
    payment.method()?.let { CheckoutPayment(it, payment.total.filter { char -> char.isDigit() || char == '.' }.toDoubleOrNull() ?: 0.0) }
}

private fun PaymentApiResponse.method(): CheckoutPaymentMethod? = when (paymentType) {
    "cash" -> CheckoutPaymentMethod.Cash
    "card" -> CheckoutPaymentMethod.Card
    "transfer" -> CheckoutPaymentMethod.Transfer
    "other" -> CheckoutPaymentMethod.Other
    "advance" -> CheckoutPaymentMethod.Advance
    else -> null
}
