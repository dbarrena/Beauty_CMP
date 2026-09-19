package com.lasso.lassoapp.data.remote.endpoints

import com.lasso.lassoapp.data.remote.LASSO_API_URL
import com.lasso.lassoapp.data.remote.bodyOrThrow
import com.lasso.lassoapp.model.AppointmentCalendarResponse
import com.lasso.lassoapp.model.AppointmentWriteRequest
import com.lasso.lassoapp.model.MessageResponse
import com.lasso.lassoapp.model.SavedAppointment
import com.lasso.lassoapp.model.normalized
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

interface AppointmentsApi {
    suspend fun getAppointmentCalendar(startEpoch: Long, endEpoch: Long): AppointmentCalendarResponse
    suspend fun createAppointment(request: AppointmentWriteRequest): SavedAppointment
    suspend fun editAppointment(id: Int, request: AppointmentWriteRequest): SavedAppointment
    suspend fun deleteAppointment(id: Int): MessageResponse
}

internal class KtorAppointmentsApi(private val client: HttpClient) : AppointmentsApi {
    override suspend fun getAppointmentCalendar(
        startEpoch: Long,
        endEpoch: Long,
    ): AppointmentCalendarResponse = client.get(
        LASSO_API_URL + "appointments/calendar?startEpoch=$startEpoch&endEpoch=$endEpoch",
    ).bodyOrThrow()

    override suspend fun createAppointment(request: AppointmentWriteRequest): SavedAppointment =
        client.post(LASSO_API_URL + "appointments/new") {
            contentType(ContentType.Application.Json)
            setBody(request.normalized().copy(partnerId = null))
        }.bodyOrThrow()

    override suspend fun editAppointment(
        id: Int,
        request: AppointmentWriteRequest,
    ): SavedAppointment = client.post(LASSO_API_URL + "appointments/edit/$id") {
        contentType(ContentType.Application.Json)
        setBody(request.normalized().copy(partnerId = null))
    }.bodyOrThrow()

    override suspend fun deleteAppointment(id: Int): MessageResponse =
        client.delete(LASSO_API_URL + "appointments/delete/$id").bodyOrThrow()
}
