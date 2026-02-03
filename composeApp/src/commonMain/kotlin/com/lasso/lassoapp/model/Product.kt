package com.lasso.lassoapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    override val id: Int? = null,
    override val name: String,
    override val type: String = "product",
    val description: String? = null,
    override val price: String,
    val cost: String? = null,
    val stock: Int? = null,
    val category: String? = null,
    @SerialName("partner_id") val partnerId: Int? = null,
    @SerialName("created_at") val createdAt: Long? = null,
    @SerialName("updated_at") val updatedAt: Long? = null,
    @SerialName("category_id") val categoryId: Int? = null,
) : LassoItem

@Serializable
data class ProductCategory(
    val id: Int? = null,
    @SerialName("partner_id") val partnerId: Int? = null,
    val name: String,
    @SerialName("created_at") val createdAt: Long? = null,
    @SerialName("updated_at") val updatedAt: Long? = null,
)