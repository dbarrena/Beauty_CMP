package com.beauty.beautyapp.model

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
    val createdAt: String,

    @SerialName("updated_at")
    val updatedAt: String?,

    val partners: Partner
)

@Serializable
data class Partner(
    val id: Int,
    val name: String,
    val email: String
)