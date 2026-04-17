package com.lasso.lassoapp.screens.sales.v2

import com.lasso.lassoapp.model.PaymentApiResponse
import com.lasso.lassoapp.model.SaleApiResponse
import com.lasso.lassoapp.utils.formatCurrency
import com.lasso.lassoapp.utils.parseCurrency
import kotlin.math.roundToLong

internal data class PaymentBreakdown(val cash: Double, val card: Double, val transfer: Double)

internal fun List<SaleApiResponse>.paymentBreakdown(): PaymentBreakdown {
    var cash = 0.0
    var card = 0.0
    var transfer = 0.0
    for (sale in this) {
        for (p in sale.payments) {
            val v = parsePaymentAmount(p)
            when (p.paymentType) {
                "cash" -> cash += v
                "transfer" -> transfer += v
                else -> card += v
            }
        }
    }
    fun r(x: Double) = (x * 100).roundToLong() / 100.0
    return PaymentBreakdown(r(cash), r(card), r(transfer))
}

internal fun parsePaymentAmount(p: PaymentApiResponse): Double =
    p.total.parseCurrency()

internal fun formatMoney(amount: Double): String =
    amount.formatCurrency(includeSymbol = true)

internal fun formatMoney(amount: String): String {
    val amountStripped = amount.replace("$", "").toDoubleOrNull() ?: 0.0
    return amountStripped.formatCurrency(includeSymbol = true)
}

