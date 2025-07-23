package com.jetbrains.kmpapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed interface BeautyItem {
    val name: String
    val price: String
}

@Serializable
data class Service(
    override val name: String,
    val description: String,
    override val price: String,
    @SerialName("duration_minutes")
    val durationMinutes: Int
) : BeautyItem
