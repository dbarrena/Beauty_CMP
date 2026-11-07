package com.lasso.lassoapp

import androidx.compose.ui.graphics.Color
import com.lasso.lassoapp.model.Event
import com.lasso.lassoapp.screens.calendar.CalendarViewMode
import com.lasso.lassoapp.screens.calendar.CalendarAppointmentFocus
import com.lasso.lassoapp.screens.calendar.firstAppointmentFocus
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CalendarInitialFocusTest {
    private val dates = listOf(LocalDate(2026, 11, 7), LocalDate(2026, 11, 8), LocalDate(2026, 11, 9))

    private fun appointment(id: Int, employee: Int, start: String) = Event(
        appointmentId = id,
        employeeId = employee,
        color = Color.White,
        start = LocalDateTime.parse(start),
        end = LocalDateTime.parse(start),
    )

    @Test
    fun dayViewTargetsEmployeeOfEarliestVisibleAppointment() {
        val events = listOf(
            appointment(1, 1, "2026-11-07T15:00"),
            appointment(2, 3, "2026-11-07T09:00"),
            appointment(3, 2, "2026-11-08T08:00"),
        )
        assertEquals(CalendarAppointmentFocus(2, 540), firstAppointmentFocus(events, dates.take(1), listOf(1, 2, 3), CalendarViewMode.Day))
        assertEquals(CalendarAppointmentFocus(0, 900), firstAppointmentFocus(events, dates.take(1), listOf(1), CalendarViewMode.Day))
        assertNull(firstAppointmentFocus(events, dates.take(1), listOf(2), CalendarViewMode.Day))
    }

    @Test
    fun threeDayViewComparesTimesAcrossAllDatesInsteadOfPreferringFirstColumn() {
        val events = listOf(
            appointment(1, 1, "2026-11-07T15:00"),
            appointment(2, 1, "2026-11-09T16:00"),
            appointment(3, 2, "2026-11-09T11:30"),
            appointment(4, 2, "2026-11-09T09:00").copy(appointmentId = null, calendarBlockId = 4),
        )
        assertEquals(CalendarAppointmentFocus(2, 690), firstAppointmentFocus(
            events, dates, listOf(1, 2), CalendarViewMode.ThreeDays,
        ))
        assertEquals(CalendarAppointmentFocus(0, 900), firstAppointmentFocus(
            events.take(1), dates, listOf(1, 2), CalendarViewMode.ThreeDays,
        ))
    }

    @Test
    fun dayViewComparesAllDisplayedEmployees() {
        val events = listOf(
            appointment(1, 1, "2026-11-07T08:00"),
            appointment(2, 2, "2026-11-07T15:00"),
            appointment(3, 2, "2026-11-07T12:00"),
        )
        assertEquals(CalendarAppointmentFocus(0, 480), firstAppointmentFocus(
            events, dates.take(1), listOf(1, 2), CalendarViewMode.Day,
        ))
        assertEquals(CalendarAppointmentFocus(0, 480), firstAppointmentFocus(
            events, dates.take(1), listOf(1, 2, 3), CalendarViewMode.Day,
        ))
    }

    @Test
    fun threeDayViewTargetsFirstDateWithAnAppointmentAndIgnoresBlocks() {
        val block = appointment(1, 1, "2026-11-07T08:00").copy(appointmentId = null, calendarBlockId = 1)
        val events = listOf(
            block,
            appointment(2, 2, "2026-11-09T08:00"),
            appointment(3, 1, "2026-11-08T16:00"),
        )
        assertEquals(CalendarAppointmentFocus(2, 480), firstAppointmentFocus(events, dates, emptyList(), CalendarViewMode.ThreeDays))
        assertNull(firstAppointmentFocus(listOf(block), dates, listOf(1), CalendarViewMode.Day))
        assertNull(firstAppointmentFocus(emptyList(), dates, listOf(1), CalendarViewMode.ThreeDays))
    }
}
