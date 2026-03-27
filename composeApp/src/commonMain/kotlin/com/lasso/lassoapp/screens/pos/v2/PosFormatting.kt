package com.lasso.lassoapp.screens.pos.v2

import kotlin.math.abs
import kotlin.math.roundToLong

fun Double.toPosMoneyString(): String {
    val cents = (this * 100.0).roundToLong().toInt()
    val negative = cents < 0
    val absCents = abs(cents)
    val dollars = absCents / 100
    val frac = absCents % 100
    val sign = if (negative) "-" else ""
    return "$sign$dollars.${frac.toString().padStart(2, '0')}"
}
