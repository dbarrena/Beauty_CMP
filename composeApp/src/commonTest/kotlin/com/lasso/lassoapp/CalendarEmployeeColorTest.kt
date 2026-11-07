package com.lasso.lassoapp

import androidx.compose.ui.graphics.Color
import com.lasso.lassoapp.model.AppointmentCalendarResponse
import com.lasso.lassoapp.model.CalendarAppointment
import com.lasso.lassoapp.model.EmployeeAppointmentSchedule
import com.lasso.lassoapp.screens.calendar.toEvents
import com.lasso.lassoapp.ui.theme.LassoPrimary
import com.lasso.lassoapp.ui.theme.calendarColorOrPrimary
import kotlinx.datetime.TimeZone
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import kotlin.test.Test
import kotlin.test.assertEquals

class CalendarEmployeeColorTest {
    @Test
    fun calendarResponseDecodesEmployeeColorsAndDefaultsMissingColorToNull() {
        val response = Json.decodeFromString<AppointmentCalendarResponse>(
            """{"startEpoch":0,"endEpoch":300,"employees":[{"id":1,"name":"Ana","calendar_color":"#A1b2C3"},{"id":2,"name":"Bea","calendar_color":null},{"id":3,"name":"Camila"}]}"""
        )
        assertEquals(listOf("#A1b2C3", null, null), response.employees.map { it.calendarColor })
    }

    @Test
    fun eventsUseEmployeeColorsRegardlessOfEmployeeOrder() {
        val appointment = CalendarAppointment(1, startsAt = 100, endsAt = 200)
        val employees = listOf(
            EmployeeAppointmentSchedule(1, "Ana", listOf(appointment), "#A1b2C3"),
            EmployeeAppointmentSchedule(2, "Bea", listOf(appointment.copy(id = 2))),
        )
        fun colors(schedules: List<EmployeeAppointmentSchedule>) =
            AppointmentCalendarResponse(0, 300, schedules).toEvents(TimeZone.UTC)
                .associate { it.employeeId to it.color }
        assertEquals(mapOf<Int?, Color>(1 to Color(0xFFA1B2C3), 2 to LassoPrimary), colors(employees))
        assertEquals(colors(employees), colors(employees.reversed()))
    }

    @Test
    fun invalidOrMissingColorsFallBackToPrimary() {
        for (hex in listOf(null, "", "red", "#123", "#GGGGGG", "112233")) {
            assertEquals(LassoPrimary, calendarColorOrPrimary(hex))
        }
        assertEquals(Color.Black, calendarColorOrPrimary("#000000"))
    }
}
