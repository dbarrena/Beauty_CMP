package com.lasso.lassoapp.screens.pos.v2.checkout_dialog

sealed class CheckoutPaymentMethod(val display: String, val key: String) {
    data object Cash : CheckoutPaymentMethod("Efectivo", "cash")

    data object Card : CheckoutPaymentMethod("Tarjeta", "card")

    data object Transfer : CheckoutPaymentMethod("Transferencia", "transfer")
    data object Multiple : CheckoutPaymentMethod("Multiples Pagos", "")
}