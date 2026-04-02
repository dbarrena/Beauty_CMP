package com.lasso.lassoapp.screens.pos.v2.checkout_dialog

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(
                        animationSpec = tween(180),
                    ) using SizeTransform(
                        clip = false,
                        sizeAnimationSpec = { _, _ -> tween(300) },
                    )
                },
                label = "checkoutFlow",
            ) { current ->
                when (current) {
                    is CheckoutStep.MethodPicker -> {
                        CheckoutPaymentMethodPickerContent(
                            //modifier = Modifier.height(600.dp),
                            totalPrice = totalPrice,
                            onClose = { onDismiss(false) },
                            onMethodClicked = { checkoutMethod ->
                                step = CheckoutStep.SplitPayment(checkoutMethod)
                            },
                        )
                    }

                    is CheckoutStep.SplitPayment -> {
                        CheckoutSplitPaymentContent(
                            //modifier = Modifier.height(600.dp),
                            checkoutPaymentMethod = current.checkoutPaymentMethod,
                            totalPrice = totalPrice,
                            onBack = { step = CheckoutStep.MethodPicker },
                            onClose = { onDismiss(false) },
                            onCompleteSale = {
                                step = CheckoutStep.Success(collectedAmount = totalPrice)
                            },
                        )
                    }

                    is CheckoutStep.Success -> {
                        CheckoutSaleSuccessContent(
                            collectedAmount = current.collectedAmount,
                            onClose = { onDismiss(true) },
                        )
                    }
                }
            }
        }
    }
}
