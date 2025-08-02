package com.beauty.beautyapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Sale(
    val id: Int? = null,
    @SerialName("client_id") val clientId: Int? = null,
    @SerialName("partner_id") val partnerId: Int? = null,
    @SerialName("sale_details") val saleDetails: List<SaleDetail>,
    @SerialName("payment_type") val paymentType: String
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
    @SerialName("client_id") val clientId: Int? = null,
    @SerialName("partner_id") val partnerId: Int,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String?,
    @SerialName("sale_details") val saleDetails: List<SaleDetailApiResponse>,
    @SerialName("payment_type") val paymentType: String
)

@Serializable
data class SaleDetailApiResponse(
    val id: Int,
    @SerialName("sale_id") val saleId: Int,
    @SerialName("item_type") val itemType: String,
    @SerialName("item_id") val itemId: Int,
    val quantity: Int,
    val price: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String?,
    val product: Product? = null,
    val service: Service? = null
)