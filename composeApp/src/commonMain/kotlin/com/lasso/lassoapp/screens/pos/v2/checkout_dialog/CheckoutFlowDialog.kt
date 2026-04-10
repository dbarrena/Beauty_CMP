package com.lasso.lassoapp.screens.pos.v2.checkout_dialog

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.lasso.lassoapp.screens.pos.SelectedPosItem
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.payment_amount.CheckoutSplitPaymentContent
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.payment_method.CheckoutPaymentMethodPickerContent
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.payment_method.CheckoutPaymentMethodTokens
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.success.CheckoutSaleSuccessContent
import com.lasso.lassoapp.ui.theme.LassoOutlineHairline
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CheckoutFlowDialog(
    totalPrice: Double,
    items: List<SelectedPosItem>,
    onDismiss: (Boolean) -> Unit,
) {
    val viewModel = koinViewModel<CheckoutDialogViewModelV2>()
    val state by viewModel.state.collectAsState()

    remember {
        viewModel.resetCheckoutFlow()
    }

    Dialog(
        onDismissRequest = {
            when (state.step) {
                is CheckoutStep.MethodPicker -> onDismiss(false)
                is CheckoutStep.SplitPayment -> viewModel.navigateToMethodPicker()
                is CheckoutStep.Success -> onDismiss(true)
            }
        },
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(CheckoutPaymentMethodTokens.cardCornerRadius),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, LassoOutlineHairline),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(
                        animationSpec = tween(
                            durationMillis = 70,
                            easing = LinearEasing,
                        ),
                    )
                    .background(Color.Transparent),
            ) {
                when (val step = state.step) {
                    is CheckoutStep.MethodPicker -> {
                        CheckoutPaymentMethodPickerContent(
                            totalPrice = totalPrice,
                            onClose = { onDismiss(false) },
                            onMethodClicked = viewModel::navigateToSplitPayment,
                        )
                    }

                    is CheckoutStep.SplitPayment -> {
                        CheckoutSplitPaymentContent(
                            checkoutPaymentMethod = step.checkoutPaymentMethod,
                            totalPrice = totalPrice,
                            isRegisteringSale = state.isLoading,
                            registerSaleError = state.error,
                            onBack = viewModel::navigateToMethodPicker,
                            onClose = { onDismiss(false) },
                            onRegisterSale = { payments ->
                                viewModel.registerSale(
                                    items = items,
                                    unprocessedPayments = payments,
                                )
                            },
                        )
                    }

                    is CheckoutStep.Success -> {
                        CheckoutSaleSuccessContent(
                            collectedAmount = step.collectedAmount,
                            onClose = { onDismiss(true) },
                        )
                    }
                }
            }
        }
    }
}
