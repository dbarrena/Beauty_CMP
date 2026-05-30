package com.lasso.lassoapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Employee(
    val id: Int,
    val name: String,
    val email: String,
    val password: String? = null,
    val role: String,

    @SerialName("partner_id")
    val partnerId: Int,

    @SerialName("created_at")
    val createdAt: Long,

    @SerialName("updated_at")
    val updatedAt: Long?,

    val partners: Partner? = null,

    @SerialName("product_commission_percentage")
    val productCommissionPercentage: String? = "0",

    @SerialName("service_commission_percentage")
    val serviceCommissionPercentage: String? = "0"
)

@Serializable
data class Partner(
    val id: Int,
    val name: String,
    val email: String
)