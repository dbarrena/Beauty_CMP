package com.lasso.lassoapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Home(
    val numberOfSales: Int,
    val totalEarnings: String,
    val totalItemsSold: Int,
    val productsSold: Int,
    val servicesSold: Int,
    val todayEarnings: String? = null,
    val weekEarnings: String? = null,
    val todayTransactionCount: Int? = null,
    val numberOfAppointments: Int = 0,
    val appointments: List<HomeAppointment> = emptyList(),
    @SerialName("dailyEarnings") val last7DaysEarnings: List<DailyEarning> = emptyList(),
)

@Serializable
data class HomeAppointment(
    val id: Int,
    val client: AppointmentClientSummary? = null,
    val employee: HomeAppointmentEmployee,
    val services: List<AppointmentServiceSummary> = emptyList(),
    val startsAt: Long,
    val endsAt: Long,
    val notes: String? = null,
)

@Serializable
data class HomeAppointmentEmployee(
    val id: Int,
    val name: String,
    @SerialName("calendar_color") val calendarColor: String? = null,
)

@Serializable
data class DailyEarning(
    val date: String,
    val earnings: String,
    val earningsNumber: Float
)

@Serializable
data class TopSellersResponse(
    @SerialName("topProducts") val topProducts: List<TopItem> = emptyList(),
    @SerialName("topServices") val topServices: List<TopItem> = emptyList(),
)

@Serializable
data class TopItem(
    val id: Long,
    val name: String,
    val totalSales: String, // e.g. "$4,490.00"
    val totalQuantity: Long,
) {
    fun totalSalesAsDouble(): Double = parseMoneyToDouble(totalSales)
}

private fun parseMoneyToDouble(raw: String): Double {
    val normalized = raw
        .trim()
        .replace("$", "")
        .replace(",", "")
        .replace(" ", "")
    return normalized.toDoubleOrNull() ?: 0.0
}
