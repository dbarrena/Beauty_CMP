package com.lasso.lassoapp.screens.pos.v2.checkout_dialog

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.lasso.lassoapp.screens.pos.SelectedPosItem
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.payment_amount.CheckoutSplitPaymentContent
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.payment_method.CheckoutPaymentMethodPickerContent
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.register_discount.RegisterDiscountContent
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.success.CheckoutSaleSuccessContent
import com.lasso.lassoapp.screens.utils.LassoDialogFrame
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CheckoutFlowDialog(
    totalPrice: Double,
    items: List<SelectedPosItem>,
    onDismiss: (Boolean) -> Unit,
) {
    val viewModel = koinViewModel<CheckoutDialogViewModelV2>()
    val state by viewModel.state.collectAsState()
    val originalTotal = state.checkoutTotal ?: totalPrice
    val appliedDiscount =
        state.discountAmount?.coerceIn(0.0, originalTotal.coerceAtLeast(0.0)) ?: 0.0
    val payableTotalPrice = (originalTotal - appliedDiscount).coerceAtLeast(0.0)

    LaunchedEffect(Unit) { viewModel.beginCheckout() }

    LassoDialogFrame(
        onDismiss = {
            viewModel.dismissCheckout()
            onDismiss(state.step is CheckoutStep.Success)
        }
    ) {
        Box(
            modifier =
                Modifier.fillMaxWidth()
                    .animateContentSize(
                        animationSpec = tween(durationMillis = 70, easing = LinearEasing)
                    )
                    .background(Color.Transparent)
        ) {
            when (val step = state.step) {
                is CheckoutStep.MethodPicker -> {
                    CheckoutPaymentMethodPickerContent(
                        totalPrice = originalTotal,
                        discountAmount = state.discountAmount,
                        onClose = { if (!state.isLoading) onDismiss(false) },
                        onMethodClicked = viewModel::navigateToSplitPayment,
                        onRegisterDiscountClicked = viewModel::navigateToRegisterDiscount,
                    )
                }

                is CheckoutStep.SplitPayment -> {
                    CheckoutSplitPaymentContent(
                        checkoutPaymentMethod = step.checkoutPaymentMethod,
                        totalPrice = payableTotalPrice,
                        state = state,
                        initialPayments = state.draftPayments,
                        onBack = viewModel::navigateToMethodPicker,
                        onClose = { if (!state.isLoading) onDismiss(false) },
                        onSelectEmployee = viewModel::setSelectedEmployee,
                        onRegisterSale = { payments ->
                            viewModel.registerSale(items = items, unprocessedPayments = payments)
                        },
                    )
                }

                is CheckoutStep.Success -> {
                    CheckoutSaleSuccessContent(
                        collectedAmount = step.collectedAmount,
                        onClose = {
                            viewModel.dismissCheckout()
                            onDismiss(true)
                        },
                    )
                }

                CheckoutStep.Discount -> {
                    RegisterDiscountContent(
                        totalPrice = originalTotal,
                        discountAmount = state.discountAmount,
                        onBack = viewModel::navigateToMethodPicker,
                        onClose = { if (!state.isLoading) onDismiss(false) },
                        onRegisterDiscount = viewModel::registerDiscount,
                    )
                }
            }
        }
    }
}
