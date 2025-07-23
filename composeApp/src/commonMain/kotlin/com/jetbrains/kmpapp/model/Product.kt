package com.jetbrains.kmpapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    override val name: String,
    val description: String? = null,
    override val price: String,
    val cost: String? = null,
    val stock: Int? = null,
    @SerialName("partner_id") val partnerId: Int,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    val category: String? = null
) : BeautyItem
