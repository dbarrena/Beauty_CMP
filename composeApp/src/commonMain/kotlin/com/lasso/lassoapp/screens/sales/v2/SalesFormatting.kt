package com.lasso.lassoapp.screens.sales.v2

import com.lasso.lassoapp.model.PaymentApiResponse
import com.lasso.lassoapp.model.SaleApiResponse
import com.lasso.lassoapp.utils.formatCurrency
import com.lasso.lassoapp.utils.parseCurrency
import kotlin.math.roundToLong

internal data class PaymentBreakdown(
    val cash: Double,
    val card: Double,
    val transfer: Double,
    val other: Double,
    val advance: Double,
)

internal fun List<SaleApiResponse>.paymentBreakdown(): PaymentBreakdown {
    var cash = 0.0
    var card = 0.0
    var transfer = 0.0
    var other = 0.0
    var advance = 0.0

    for (sale in this) {
        for (payment in sale.payments) {
            val paymentAmount = parsePaymentAmount(payment)
            when (payment.paymentType) {
                "cash" -> cash += paymentAmount
                "card" -> card += paymentAmount
                "transfer" -> transfer += paymentAmount
                "other" -> other += paymentAmount
                "advance" -> advance += paymentAmount
                else -> {}
            }
        }
    }

    fun round(x: Double) = (x * 100).roundToLong() / 100.0

    return PaymentBreakdown(
        round(cash),
        round(card),
        round(transfer),
        round(other),
        round(advance)
    )
}

internal fun parsePaymentAmount(p: PaymentApiResponse): Double =
    p.total.parseCurrency()

internal fun formatMoney(amount: Double): String =
    amount.formatCurrency(includeSymbol = true)

internal fun SaleApiResponse.discountAmountValue(): Double =
    discountAmount?.parseCurrency() ?: 0.0

internal fun SaleApiResponse.netTotalValue(): Double =
    total.parseCurrency() - discountAmountValue()
