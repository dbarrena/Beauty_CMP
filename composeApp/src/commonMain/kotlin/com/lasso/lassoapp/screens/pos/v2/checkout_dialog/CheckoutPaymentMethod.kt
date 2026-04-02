package com.lasso.lassoapp.screens.pos.v2.checkout_dialog

internal sealed class CheckoutPaymentMethod {
    data object Cash : CheckoutPaymentMethod()

    data object Card : CheckoutPaymentMethod()

    data object Transfer : CheckoutPaymentMethod()
    data object Multiple : CheckoutPaymentMethod()
}