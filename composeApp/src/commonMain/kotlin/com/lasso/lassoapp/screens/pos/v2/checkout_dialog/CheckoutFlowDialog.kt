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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@Composable
fun CheckoutFlowDialog(
    totalPrice: Double,
    @Suppress("UNUSED_PARAMETER")
    items: List<SelectedPosItem>,
    onDismiss: (Boolean) -> Unit,
) {
    var step by remember { mutableStateOf<CheckoutStep>(CheckoutStep.MethodPicker) }

    Dialog(
        onDismissRequest = {
            when (step) {
                is CheckoutStep.MethodPicker -> onDismiss(false)
                is CheckoutStep.SplitPayment -> {
                    step = CheckoutStep.MethodPicker
                }

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
                when (step) {
                    is CheckoutStep.MethodPicker -> {
                        CheckoutPaymentMethodPickerContent(
                            totalPrice = totalPrice,
                            onClose = { onDismiss(false) },
                            onMethodClicked = { checkoutMethod ->
                                step = CheckoutStep.SplitPayment(checkoutMethod)
                            },
                        )
                    }

                    is CheckoutStep.SplitPayment -> {
                        val splitStep = step as CheckoutStep.SplitPayment
                        CheckoutSplitPaymentContent(
                            checkoutPaymentMethod = splitStep.checkoutPaymentMethod,
                            totalPrice = totalPrice,
                            onBack = { step = CheckoutStep.MethodPicker },
                            onClose = { onDismiss(false) },
                            onCompleteSale = {
                                step = CheckoutStep.Success(collectedAmount = totalPrice)
                            },
                        )
                    }

                    is CheckoutStep.Success -> {
                        val successStep = step as CheckoutStep.Success
                        CheckoutSaleSuccessContent(
                            collectedAmount = successStep.collectedAmount,
                            onClose = { onDismiss(true) },
                        )
                    }
                }
            }
        }
    }
}
