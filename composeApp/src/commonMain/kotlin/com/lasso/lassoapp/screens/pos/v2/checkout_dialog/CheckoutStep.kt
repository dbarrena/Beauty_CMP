package com.lasso.lassoapp.screens.pos.v2.checkout_dialog

/**
 * Steps inside [CheckoutFlowDialog]. Single dialog host; content swaps with [androidx.compose.animation.AnimatedContent].
 */
internal sealed class CheckoutStep {
    data object MethodPicker : CheckoutStep()

    data class SplitPayment(
        val checkoutPaymentMethod: CheckoutPaymentMethod
    ) : CheckoutStep()

    data class Success(
        val collectedAmount: Double,
    ) : CheckoutStep()
}
