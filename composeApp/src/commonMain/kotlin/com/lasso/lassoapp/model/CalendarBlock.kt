package com.lasso.lassoapp.model

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class CalendarBlockWriteRequest(
    @SerialName("employee_id") val employeeId: Int,
    @SerialName("start_time") val startTime: Long,
    @SerialName("end_time") val endTime: Long,
    @SerialName("start_date") @EncodeDefault val startDate: Long? = null,
    @SerialName("end_date") @EncodeDefault val endDate: Long? = null,
    @EncodeDefault val title: String? = null,
)

@Serializable
data class CalendarBlock(
    val id: Int,
    @SerialName("employee_id") val employeeId: Int,
    @SerialName("start_time") val startTime: Long,
    @SerialName("end_time") val endTime: Long,
    @SerialName("start_date") val startDate: Long? = null,
    @SerialName("end_date") val endDate: Long? = null,
    @SerialName("partner_id") val partnerId: Int? = null,
    val title: String? = null,
)

fun CalendarBlockWriteRequest.validationError(): String? = when {
    title != null && title.isBlank() -> "Ingresa un nombre o deja el campo vacío."
    employeeId <= 0 -> "Selecciona un empleado."
    startTime < 0 || endTime <= startTime -> "El fin del bloqueo debe ser posterior al inicio."
    (startDate != null && startDate < 0) || (endDate != null && endDate < 0) -> "Selecciona fechas válidas."
    startDate != null && endDate != null && endDate < startDate -> "La fecha final no puede ser anterior a la inicial."
    else -> null
}
