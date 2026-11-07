package com.lasso.lassoapp.model

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
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
    val createdAt: Long? = null,

    @SerialName("updated_at")
    val updatedAt: Long?,

    val partners: Partner? = null,

    @SerialName("product_commission_percentage")
    val productCommissionPercentage: String? = "0",

    @SerialName("service_commission_percentage")
    val serviceCommissionPercentage: String? = "0",

    @SerialName("calendar_color")
    val calendarColor: String? = null,

    @SerialName("is_displayed_on_calendar")
    val isDisplayedOnCalendar: Boolean = true,

    @SerialName("can_see_sales")
    val canSeeSales: Boolean = false
)

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class EmployeeRegistrationRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String,

    @SerialName("partner_id")
    val partnerId: Int? = null,

    @SerialName("product_commission_percentage")
    @EncodeDefault
    val productCommissionPercentage: String = "0",

    @SerialName("service_commission_percentage")
    @EncodeDefault
    val serviceCommissionPercentage: String = "0",

    @SerialName("calendar_color")
    @EncodeDefault
    val calendarColor: String? = null,

    @SerialName("is_displayed_on_calendar")
    @EncodeDefault
    val isDisplayedOnCalendar: Boolean = true,

    @SerialName("can_see_sales")
    @EncodeDefault
    val canSeeSales: Boolean = false
)

@Serializable
data class Partner(
    val id: Int,
    val name: String,
    val email: String
)
