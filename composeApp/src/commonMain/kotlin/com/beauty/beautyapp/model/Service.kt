package com.beauty.beautyapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed interface BeautyItem {
    val id: Int?
    val name: String
    val price: String
    val type: String
}

@Serializable
data class Service(
    override val name: String,
    override val id: Int,
    override val type: String = "service",
    val description: String,
    override val price: String,
    @SerialName("duration_minutes")
    val durationMinutes: Int
) : BeautyItem
