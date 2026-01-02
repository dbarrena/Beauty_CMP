package com.lasso.lassoapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Payment(
    val total: Double,
    @SerialName("payment_type") val paymentType: String,
)

@Serializable
data class PaymentApiResponse(
    var id: Int? = null,
    val total: String,
    @SerialName("payment_type") val paymentType: String,
    @SerialName("sale_id") val saleId: Int? = null,
    @SerialName("cash_closure_id") val cashClosureId: Int? = null,
    @SerialName("created_at") val createdAt: Long? = null,
    @SerialName("updated_at") val updatedAt: Long? = null
)