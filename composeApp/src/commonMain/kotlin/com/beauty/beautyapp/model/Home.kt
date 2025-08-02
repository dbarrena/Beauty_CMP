package com.beauty.beautyapp.model

import kotlinx.serialization.Serializable

@Serializable
data class Home(
    val numberOfSales: Int,
    val totalEarnings: String,
    val totalItemsSold: Int,
    val productsSold: Int,
    val servicesSold: Int,
)
