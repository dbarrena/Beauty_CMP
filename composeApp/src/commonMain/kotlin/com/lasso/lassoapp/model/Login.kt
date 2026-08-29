package com.lasso.lassoapp.model

import kotlinx.serialization.Serializable

@Serializable
data class Login(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val success: Boolean? = null,
    val message: String? = null,
    val token: String? = null,
    val employee: Employee? = null,
    val error: String? = null
)
