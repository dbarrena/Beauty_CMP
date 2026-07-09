package com.lasso.lassoapp.screens.pos.v2.checkout_dialog

sealed class CheckoutPaymentMethod(val display: String, val key: String) {
    data object Cash : CheckoutPaymentMethod("Efectivo", "cash")

    data object Card : CheckoutPaymentMethod("Tarjeta", "card")

    data object Transfer : CheckoutPaymentMethod("Transferencia", "transfer")

    data object Other : CheckoutPaymentMethod("Otro", "other")

    data object Advance : CheckoutPaymentMethod("Anticipo", "advance")

    data object Multiple : CheckoutPaymentMethod("Multiples Pagos", "")
}
