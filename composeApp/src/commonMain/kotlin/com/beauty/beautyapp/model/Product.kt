package com.beauty.beautyapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class Product(
    override val id: Int? = null,
    override val name: String,
    override val type: String = "product",
    val description: String? = null,
    override val price: String,
    val cost: String? = null,
    val stock: Int? = null,
    @SerialName("partner_id") val partnerId: Int? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    val category: String? = null,
) : BeautyItem
