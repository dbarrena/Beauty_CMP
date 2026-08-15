package com.lasso.lassoapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppointmentClientSummary(
    val id: Int,
    val name: String,
)

@Serializable
data class CalendarAppointment(
    val id: Int,
    val client: AppointmentClientSummary? = null,
    val serviceName: String? = null,
    val startsAt: Long,
    val endsAt: Long,
    val notes: String? = null,
)

@Serializable
data class EmployeeAppointmentSchedule(
    val id: Int,
    val name: String,
    val appointments: List<CalendarAppointment> = emptyList(),
)

@Serializable
data class AppointmentCalendarResponse(
    val startEpoch: Long,
    val endEpoch: Long,
    val employees: List<EmployeeAppointmentSchedule> = emptyList(),
)

@Serializable
data class AppointmentWriteRequest(
    @SerialName("employee_id") val employeeId: Int,
    @SerialName("client_id") val clientId: Int? = null,
    @SerialName("service_name") val serviceName: String? = null,
    @SerialName("starts_at") val startsAt: Long,
    @SerialName("ends_at") val endsAt: Long,
    val notes: String? = null,
    @SerialName("partner_id") val partnerId: Int? = null,
)

fun AppointmentWriteRequest.normalized(): AppointmentWriteRequest = copy(
    serviceName = serviceName?.trim(),
    notes = notes?.trim()?.ifBlank { null },
)

@Serializable
data class AppointmentPartySummary(
    val id: Int,
    val name: String,
)

@Serializable
data class SavedAppointment(
    val id: Int,
    @SerialName("partner_id") val partnerId: Int,
    @SerialName("employee_id") val employeeId: Int,
    @SerialName("client_id") val clientId: Int? = null,
    @SerialName("service_name") val serviceName: String? = null,
    @SerialName("starts_at") val startsAt: Long,
    @SerialName("ends_at") val endsAt: Long,
    val notes: String? = null,
    @SerialName("created_at") val createdAt: Long? = null,
    @SerialName("updated_at") val updatedAt: Long? = null,
    val clients: AppointmentPartySummary? = null,
    val employees: AppointmentPartySummary? = null,
)

@Serializable
data class MessageResponse(val message: String)

