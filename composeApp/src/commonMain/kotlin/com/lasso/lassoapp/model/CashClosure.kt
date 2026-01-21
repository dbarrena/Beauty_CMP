package com.lasso.lassoapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CashClosure(
    @SerialName("open_payments") val openPayments: List<CashClosureItem>,
    @SerialName("grand_total") val total: String,
    @SerialName("total_payments") val numberOfPayments: Int
)

@Serializable
data class CashClosureItem(
    @SerialName("payment_type") val paymentType: String,
    val total: String,
    val count: Int
)

@Serializable
data class CreateCashClosureRequest(
    val partnerId: Int,
    val notes: String
)

@Serializable
data class CashClosureRecordsResponse(
    val id: Int,
    @SerialName("closed_at") val closedAt: Long,
    @SerialName("total_amount") val total: String,
    @SerialName("total_payments") val totalPayments: Int,
    @SerialName("cash_closures") val cashClosures: List<CashClosureItem>,
    val notes: String?,
    val formattedDate: String? = null
)