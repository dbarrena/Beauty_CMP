package com.jetbrains.kmpapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Service(
    val name: String,
    val description: String,
    val price: String,
    @SerialName("duration_minutes")
    val durationMinutes: Int
)
