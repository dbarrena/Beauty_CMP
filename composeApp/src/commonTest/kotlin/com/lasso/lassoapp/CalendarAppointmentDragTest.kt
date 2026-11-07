package com.lasso.lassoapp

import androidx.compose.ui.graphics.Color
import com.lasso.lassoapp.model.Event
import com.lasso.lassoapp.model.minutesBetween
import com.lasso.lassoapp.screens.calendar.atStartMinutes
import com.lasso.lassoapp.screens.calendar.snappedAppointmentStart
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

class CalendarAppointmentDragTest {
    @Test
    fun verticalMovementSnapsToQuarterHoursInBothDirections() {
        assertEquals(600, snappedAppointmentStart(600, 60, 10f, 90f))
        assertEquals(615, snappedAppointmentStart(600, 60, 23f, 90f))
        assertEquals(570, snappedAppointmentStart(600, 60, -45f, 90f))
        assertEquals(615, snappedAppointmentStart(607, 60, 10f, 90f))
    }

    @Test
    fun movementKeepsWholeAppointmentWithinVisibleHours() {
        assertEquals(420, snappedAppointmentStart(600, 60, -2000f, 90f))
        assertEquals(1200, snappedAppointmentStart(600, 60, 2000f, 90f))
        assertEquals(1170, snappedAppointmentStart(600, 90, 2000f, 90f))
    }

    @Test
    fun previewPreservesDateDurationIdentityAndEmployee() {
        val original = Event(10, "Ana", Color.Red, LocalDateTime(2026, 11, 6, 10, 0),
            LocalDateTime(2026, 11, 6, 11, 30), "Manicure", 1)
        val preview = original.atStartMinutes(645, TimeZone.UTC)
        assertEquals(original.copy(start = LocalDateTime(2026, 11, 6, 10, 45),
            end = LocalDateTime(2026, 11, 6, 12, 15)), preview)
        assertEquals(90L, minutesBetween(preview.start, preview.end, TimeZone.UTC))
        assertEquals(original, original.atStartMinutes(600, TimeZone.UTC))
    }
}
