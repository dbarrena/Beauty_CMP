package com.lasso.lassoapp

import com.lasso.lassoapp.model.CalendarBlock
import com.lasso.lassoapp.model.EmployeeAppointmentSchedule
import com.lasso.lassoapp.screens.calendar.blockEvents
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CalendarBlockEventsTest {
    private fun epoch(value: String) = Instant.parse(value).toEpochMilliseconds()
    private val block = CalendarBlock(5, 1, epoch("2026-01-01T10:00:00Z"), epoch("2026-01-01T11:00:00Z"))
    private fun events(rule: CalendarBlock) = EmployeeAppointmentSchedule(1, "Ana", blockedHours = listOf(rule))
        .blockEvents(epoch("2026-11-07T00:00:00Z"), epoch("2026-11-10T00:00:00Z"), TimeZone.UTC)

    @Test
    fun unboundedBlockRepeatsAtItsClockTimesOnEveryVisibleDay() {
        val events = events(block)
        assertEquals(listOf(7, 8, 9), events.map { it.start.day })
        assertTrue(events.all { it.start.hour == 10 && it.end.hour == 11 && it.calendarBlockId == 5 && it.appointmentId == null })
    }
    @Test fun namesArePreservedOnRepeatingBlocksAndUnnamedBlocksHaveFallback() {
        assertTrue(events(block.copy(title = "Comida")).all { it.name == "Comida" })
        assertTrue(events(block).all { it.name == "Bloqueo de calendario" })
        assertTrue(events(block.copy(title = "   ")).all { it.name == "Bloqueo de calendario" })
    }

    @Test
    fun optionalBoundsAreIndependentAndInclusive() {
        val eighth = epoch("2026-11-08T00:00:00Z")
        val ninth = epoch("2026-11-09T00:00:00Z")
        assertEquals(listOf(8, 9), events(block.copy(startDate = eighth)).map { it.start.day })
        assertEquals(listOf(7, 8), events(block.copy(endDate = eighth)).map { it.start.day })
        assertEquals(listOf(8, 9), events(block.copy(startDate = eighth, endDate = ninth)).map { it.start.day })
        assertTrue(events(block.copy(endDate = epoch("2026-11-06T00:00:00Z"))).isEmpty())
    }

    @Test
    fun overnightRulesAreSplitAndClippedToTheDisplayedGrid() {
        val overnight = block.copy(startTime = epoch("2026-01-01T20:00:00Z"), endTime = epoch("2026-01-02T08:00:00Z"))
        val events = events(overnight)
        assertEquals(6, events.size)
        assertEquals(listOf(7, 20, 7, 20, 7, 20), events.map { it.start.hour })
        assertEquals(listOf(8, 21, 8, 21, 8, 21), events.map { it.end.hour })
        assertTrue(events.all { it.start.date == it.end.date })
    }

    @Test
    fun fullDayRuleProducesOneBlockPerDayWithoutDuplicateSegments() {
        val events = events(block.copy(endTime = epoch("2026-01-02T10:00:00Z")))
        assertEquals(3, events.size)
        assertTrue(events.all { it.start.hour == 7 && it.end.hour == 21 })
    }
}
