package com.lasso.lassoapp

import com.lasso.lassoapp.model.Home
import com.lasso.lassoapp.screens.home.v2.formatHomeAppointmentTime
import kotlinx.datetime.TimeZone
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Instant

class HomeAgendaTest {
    private val salesFields = """
        "numberOfSales": 0, "totalEarnings": "$0.00", "totalItemsSold": 0,
        "productsSold": 0, "servicesSold": 0
    """.trimIndent()

    @Test
    fun decodesDashboardAppointmentsIncludingUnassignedClientsAndMultipleServices() {
        val home = Json.decodeFromString<Home>("""
            { $salesFields, "numberOfAppointments": 2, "appointments": [
                { "id": 1, "client": null,
                  "employee": { "id": 2, "name": "Ana", "calendar_color": null },
                  "services": [], "startsAt": 1794326400000, "endsAt": 1794330000000, "notes": null },
                { "id": 2, "client": { "id": 4, "name": "Amariany Mateos" },
                  "employee": { "id": 2, "name": "Ana", "calendar_color": "#00D1AD" },
                  "services": [{ "id": 3, "name": "Corte" }, { "id": 4, "name": "Peinado" }],
                  "startsAt": 1794330000000, "endsAt": 1794333600000, "notes": "Nota" }
            ] }
        """)
        assertEquals(2, home.numberOfAppointments)
        assertNull(home.appointments.first().client)
        assertEquals(emptyList(), home.appointments.first().services)
        assertEquals("Amariany Mateos", home.appointments.last().client?.name)
        assertEquals(listOf("Corte", "Peinado"), home.appointments.last().services.map { it.name })
    }

    @Test
    fun olderDashboardResponsesDefaultToAnEmptyAgenda() {
        val home = Json.decodeFromString<Home>("{ $salesFields }")
        assertEquals(0, home.numberOfAppointments)
        assertEquals(emptyList(), home.appointments)
    }

    @Test
    fun appointmentTimeUsesLocalTimeAndPadsHoursAndMinutes() {
        val instant = Instant.parse("2026-11-10T15:05:00Z").toEpochMilliseconds()
        assertEquals("09:05", formatHomeAppointmentTime(instant, TimeZone.of("America/Mexico_City")))
        assertEquals("15:05", formatHomeAppointmentTime(instant, TimeZone.UTC))
    }
}
