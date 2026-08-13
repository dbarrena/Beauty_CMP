package com.lasso.lassoapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Client(
    val id: Int,
    val name: String,
    val phone: String? = null,
    val email: String? = null,
    val notes: String? = null,
    @SerialName("partner_id") val partnerId: Int,
    @SerialName("created_at") val createdAt: Long? = null,
    @SerialName("updated_at") val updatedAt: Long? = null,
)

@Serializable
data class ClientWriteRequest(
    val id: Int? = null,
    val name: String,
    val phone: String? = null,
    val email: String? = null,
    val notes: String? = null,
    @SerialName("partner_id") val partnerId: Int? = null,
)

fun ClientWriteRequest.normalized(): ClientWriteRequest = copy(
    name = name.trim(),
    phone = phone?.trim()?.ifBlank { null },
    email = email?.trim()?.ifBlank { null },
    notes = notes?.trim()?.ifBlank { null },
)
