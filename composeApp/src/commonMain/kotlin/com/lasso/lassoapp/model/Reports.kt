package com.lasso.lassoapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SalesByProductCategoryApiResponse(
    val totalQuantity: Int,
    val totalRevenue: String,
    @SerialName("products") val products: List<SalesByProductCategoryItemApiResponse>
)

@Serializable
data class SalesByProductCategoryItemApiResponse(
    val id: Int? = null,
    val name: String,
    val description: String? = null,
    val price: String,
    val totalRevenue: String? = null,
    val totalQuantitySold: Int? = null
)