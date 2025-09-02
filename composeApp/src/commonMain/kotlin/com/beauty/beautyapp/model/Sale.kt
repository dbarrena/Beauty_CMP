package com.beauty.beautyapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Sale(
    val id: Int? = null,
    @SerialName("client_id") val clientId: Int? = null,
    @SerialName("partner_id") val partnerId: Int? = null,
    @SerialName("sale_details") val saleDetails: List<SaleDetail>,
    @SerialName("payments") val payments: List<Payment>
)

@Serializable
data class SaleDetail(
    val id: Int? = null,
    @SerialName("item_id") val itemId: Int,
    @SerialName("item_type") val itemType: String,
    val quantity: Int,
    val price: Double
)

@Serializable
data class SaleApiResponse(
    val id: Int,
    val total: String,
    val formattedDate: String? = null,
    @SerialName("client_id") val clientId: Int? = null,
    @SerialName("partner_id") val partnerId: Int,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("updated_at") val updatedAt: Long?,
    @SerialName("sale_details") val saleDetails: List<SaleDetailApiResponse>,
    @SerialName("payments") val payments: List<PaymentApiResponse>
)

@Serializable
data class SaleDetailApiResponse(
    val id: Int,
    @SerialName("sale_id") val saleId: Int,
    @SerialName("item_type") val itemType: String,
    @SerialName("item_id") val itemId: Int,
    val quantity: Int,
    val price: String,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("updated_at") val updatedAt: Long?,
    val product: Product? = null,
    val service: Service? = null
)

@Serializable
data class SaleDetailEditApiRequest(
    val id: Int,
    val quantity: Int,
    val price: Double,
)