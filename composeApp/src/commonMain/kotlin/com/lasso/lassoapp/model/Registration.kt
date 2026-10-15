package com.lasso.lassoapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegistrationRequest(
    @SerialName("company_name") val companyName: String,
    val name: String,
    val role: String,
    val email: String,
    val password: String,
)
