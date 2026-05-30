package com.lasso.lassoapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommissionCalculationResponse(
    val employee: CommissionEmployee,
    @SerialName("product_sales_total")
    val productSalesTotal: Double,
    @SerialName("service_sales_total")
    val serviceSalesTotal: Double,
    @SerialName("product_commission")
    val productCommission: Double,
    @SerialName("service_commission")
    val serviceCommission: Double,
    @SerialName("total_commission")
    val totalCommission: Double,
    val products: List<CommissionProduct>,
    val services: List<CommissionService>
)

@Serializable
data class CommissionEmployee(
    val id: Int,
    val name: String,
    val email: String
)

@Serializable
data class CommissionProduct(
    val id: Int,
    val name: String,
    val quantity: Int,
    @SerialName("total_sales")
    val totalSales: Double,
    val commission: Double
)

@Serializable
data class CommissionService(
    val id: Int,
    val name: String,
    val quantity: Int,
    @SerialName("total_sales")
    val totalSales: Double,
    val commission: Double
)
