package com.lasso.lassoapp.screens.pos.v2.checkout_dialog

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.graphics.Color
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.payment_method.CheckoutPaymentMethodColors
import com.lasso.lassoapp.screens.pos.v2.toPosMoneyString
import lassoapp.composeapp.generated.resources.Res
import lassoapp.composeapp.generated.resources.checkout_payment_efectivo
import lassoapp.composeapp.generated.resources.checkout_payment_multiples
import lassoapp.composeapp.generated.resources.checkout_payment_tarjeta_debito
import lassoapp.composeapp.generated.resources.checkout_payment_transferencia
import lassoapp.composeapp.generated.resources.sales_icon
import org.jetbrains.compose.resources.DrawableResource
import kotlin.math.roundToLong

internal val paymentMethods =
    listOf(
        CheckoutPaymentMethod.Cash,
        CheckoutPaymentMethod.Card,
        CheckoutPaymentMethod.Transfer,
        CheckoutPaymentMethod.Other,
        CheckoutPaymentMethod.Advance,
    )

/** Initialized synchronously: first layout already has the selected amount and correct balance. */
internal class PaymentFields(
    method: CheckoutPaymentMethod,
    private val total: Double,
    initial: List<CheckoutPayment>,
) {
    val methods = if (method == CheckoutPaymentMethod.Multiple) paymentMethods else listOf(method)
    private val values =
        mutableStateMapOf<CheckoutPaymentMethod, String>().apply {
            paymentMethods.forEach { field ->
                val amount = initial.filter { it.paymentType == field }.sumOf { it.total }
                this[field] =
                    when {
                        initial.isEmpty() && field == method -> total.toPosMoneyString()
                        amount > 0.0 -> amount.toPosMoneyString()
                        else -> ""
                    }
            }
        }

    operator fun get(method: CheckoutPaymentMethod): String = values[method].orEmpty()

    operator fun set(method: CheckoutPaymentMethod, value: String) {
        values[method] = value
    }

    val payments: List<CheckoutPayment>
        get() =
            paymentMethods
                .filter { get(it).isNotEmpty() }
                .map { CheckoutPayment(it, get(it).replace(",", "").toDoubleOrNull() ?: 0.0) }

    private val enteredCents: Long
        get() = payments.sumOf { (it.total * 100).roundToLong() }

    val remaining: Double
        get() = ((total * 100).roundToLong() - enteredCents).coerceAtLeast(0) / 100.0

    val balanced: Boolean
        get() = enteredCents == (total * 100).roundToLong()
}

internal data class PaymentFieldSpec(
    val label: String,
    val circleColor: Color,
    val icon: DrawableResource,
)

internal fun paymentFieldSpec(method: CheckoutPaymentMethod): PaymentFieldSpec =
    when (method) {
        CheckoutPaymentMethod.Cash ->
            PaymentFieldSpec(
                "Efectivo",
                CheckoutPaymentMethodColors.efectivoCircle,
                Res.drawable.checkout_payment_efectivo,
            )
        CheckoutPaymentMethod.Card ->
            PaymentFieldSpec(
                "Tarjeta de Débito",
                CheckoutPaymentMethodColors.tarjetaDebitoCircle,
                Res.drawable.checkout_payment_tarjeta_debito,
            )
        CheckoutPaymentMethod.Transfer ->
            PaymentFieldSpec(
                "Transferencia",
                CheckoutPaymentMethodColors.transferenciaCircle,
                Res.drawable.checkout_payment_transferencia,
            )
        CheckoutPaymentMethod.Other ->
            PaymentFieldSpec(
                "Otro",
                CheckoutPaymentMethodColors.multiplesCircle,
                Res.drawable.checkout_payment_multiples,
            )
        CheckoutPaymentMethod.Advance ->
            PaymentFieldSpec(
                "Anticipo",
                CheckoutPaymentMethodColors.tarjetaCreditoCircle,
                Res.drawable.sales_icon,
            )
        CheckoutPaymentMethod.Multiple -> error("Multiple is a collection of payment fields")
    }
