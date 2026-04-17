package com.lasso.lassoapp.utils

import kotlin.math.abs
import kotlin.math.roundToLong

object CurrencyUtils {
    /**
     * Formats a double value as a currency string with commas for thousands and 2 decimal places.
     * Example: 14249.0 -> "14,249.00"
     */
    fun format(amount: Double, includeSymbol: Boolean = false): String {
        val cents = (amount * 100.0).roundToLong()
        val negative = cents < 0
        val absCents = abs(cents)
        val dollars = absCents / 100
        val frac = absCents % 100
        val sign = if (negative) "-" else ""

        val dollarsStr = dollars.toString()
            .reversed()
            .chunked(3)
            .joinToString(",")
            .reversed()

        val formatted = "$dollarsStr.${frac.toString().padStart(2, '0')}"
        return if (includeSymbol) {
            if (negative) "-$$formatted" else "$$formatted"
        } else {
            "$sign$formatted"
        }
    }

    /**
     * Parses a string into a double, removing currency symbols and commas.
     */
    fun parse(s: String): Double =
        s.replace("$", "").replace(",", "").toDoubleOrNull() ?: 0.0
}

fun Double.formatCurrency(includeSymbol: Boolean = false): String =
    CurrencyUtils.format(this, includeSymbol)

fun String.parseCurrency(): Double =
    CurrencyUtils.parse(this)
