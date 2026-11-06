package com.lasso.lassoapp

import androidx.compose.ui.graphics.Color
import com.lasso.lassoapp.model.Event
import com.lasso.lassoapp.screens.calendar.CalendarViewMode
import com.lasso.lassoapp.screens.calendar.appointmentDayRange
import com.lasso.lassoapp.screens.calendar.placeCalendarEvents
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class CalendarLayoutTest {
    private fun event(id: Int, start: String, end: String) = Event(
        appointmentId = id,
        color = Color.White,
        start = LocalDateTime.parse("2026-11-06T$start"),
        end = LocalDateTime.parse("2026-11-06T$end"),
    )

    @Test
    fun threeDayRangeStartsOnSelectedDateAndCrossesYearBoundary() {
        val zone = TimeZone.UTC
        val range = appointmentDayRange(LocalDate(2026, 12, 31), zone, CalendarViewMode.ThreeDays.dayCount)
        assertEquals(LocalDate(2026, 12, 31).atStartOfDayIn(zone).toEpochMilliseconds(), range.first)
        assertEquals(LocalDate(2027, 1, 3).atStartOfDayIn(zone).toEpochMilliseconds(), range.second)
        val dayRange = appointmentDayRange(LocalDate(2026, 12, 31), zone)
        assertEquals(LocalDate(2027, 1, 1).atStartOfDayIn(zone).toEpochMilliseconds(), dayRange.second)
    }

    @Test
    fun threeDayRangeUsesLocalMidnightsAcrossDaylightSaving() {
        val zone = TimeZone.of("America/Los_Angeles")
        val range = appointmentDayRange(LocalDate(2026, 3, 7), zone, 3)
        assertEquals(LocalDate(2026, 3, 10).atStartOfDayIn(zone).toEpochMilliseconds(), range.second)
        assertEquals(71L * 60 * 60 * 1000, range.second - range.first)
    }

    @Test
    fun simultaneousAppointmentsOccupySeparateLanes() {
        val placements = placeCalendarEvents(listOf(
            event(1, "10:00", "11:00"),
            event(2, "10:00", "10:30"),
            event(3, "10:00", "12:00"),
        ))
        assertEquals(setOf(0, 1, 2), placements.map { it.lane }.toSet())
        assertTrue(placements.all { it.laneCount == 3 })
    }

    @Test
    fun touchingAppointmentsKeepFullWidth() {
        val placements = placeCalendarEvents(listOf(
            event(2, "11:00", "12:00"),
            event(1, "10:00", "11:00"),
        ))
        assertEquals(listOf(1, 2), placements.map { it.event.appointmentId })
        assertTrue(placements.all { it.lane == 0 && it.laneCount == 1 })
    }

    @Test
    fun nestedAndChainedOverlapsReuseFreeLanesWithoutCollisions() {
        val placements = placeCalendarEvents(listOf(
            event(1, "09:00", "12:00"),
            event(2, "09:30", "10:00"),
            event(3, "10:00", "11:00"),
            event(4, "11:30", "12:30"),
            event(5, "13:00", "14:00"),
        ))
        assertTrue(placements.take(4).all { it.laneCount == 2 })
        assertEquals(1, placements.last().laneCount)
        placements.forEach { a ->
            placements.filter { b -> a.event.start < b.event.end && b.event.start < a.event.end && a != b }
                .forEach { b -> assertNotEquals(a.lane, b.lane) }
        }
    }

    @Test
    fun emptyDayNeedsNoEventPlacements() {
        assertTrue(placeCalendarEvents(emptyList()).isEmpty())
    }
}
