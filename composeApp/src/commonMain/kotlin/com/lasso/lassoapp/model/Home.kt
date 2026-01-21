package com.lasso.lassoapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Home(
    val numberOfSales: Int,
    val totalEarnings: String,
    val totalItemsSold: Int,
    val productsSold: Int,
    val servicesSold: Int,
)

@Serializable
data class TopSellersResponse(
    @SerialName("topProducts") val topProducts: List<TopItem> = emptyList(),
    @SerialName("topServices") val topServices: List<TopItem> = emptyList(),
)

@Serializable
data class TopItem(
    val id: Long,
    val name: String,
    val totalSales: String, // e.g. "$4,490.00"
    val totalQuantity: Long,
) {
    fun totalSalesAsDouble(): Double = parseMoneyToDouble(totalSales)
}

private fun parseMoneyToDouble(raw: String): Double {
    val normalized = raw
        .trim()
        .replace("$", "")
        .replace(",", "")
        .replace(" ", "")
    return normalized.toDoubleOrNull() ?: 0.0
}