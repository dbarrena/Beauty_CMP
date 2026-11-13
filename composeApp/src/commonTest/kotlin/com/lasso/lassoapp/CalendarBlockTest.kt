package com.lasso.lassoapp

import com.lasso.lassoapp.model.AppointmentCalendarResponse
import com.lasso.lassoapp.model.CalendarBlock
import com.lasso.lassoapp.model.CalendarBlockWriteRequest
import com.lasso.lassoapp.model.validationError
import com.lasso.lassoapp.screens.calendar.dialog.CalendarBlockFormState
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CalendarBlockTest {
    @Test fun renamingFullDayBlockPreservesItsExactTimestamps() {
        val start = Instant.parse("2026-11-12T10:00:23Z").toEpochMilliseconds()
        val block = CalendarBlock(5, 1, start, start + 86_400_000, title = "Descanso")
        val form = CalendarBlockFormState(LocalDate(2026, 11, 12), 1, block, TimeZone.UTC)
        form.title = "Vacaciones"
        val request = form.toRequest()
        assertEquals(block.startTime, request.startTime)
        assertEquals(block.endTime, request.endTime)
        assertNull(request.validationError())
    }
    @Test fun blockNameIsRestoredTrimmedAndCanBeCleared() {
        val block = CalendarBlock(5, 1, 100, 200, title = "Comida")
        val form = CalendarBlockFormState(LocalDate(2026, 11, 7), 1, block)
        assertEquals("Comida", form.title)
        form.title = "  Reunión del equipo  "
        assertEquals("Reunión del equipo", form.toRequest().title)
        form.title = "   "
        val request = form.toRequest()
        assertNull(request.title)
        assertEquals(JsonNull, Json.parseToJsonElement(Json.encodeToString(request)).jsonObject["title"])
        assertEquals("Comida", Json.decodeFromString<CalendarBlock>(Json.encodeToString(block)).title)
        assertNotNull(request.copy(title = " ").validationError())
    }
    @Test
    fun formBuildsEpochMillisecondsAndLeavesDateBoundsEmpty() {
        val form = CalendarBlockFormState(LocalDate(2026, 11, 7), 1)
        val request = form.toRequest(TimeZone.UTC)
        assertEquals(1, request.employeeId)
        assertEquals(Instant.parse("2026-11-07T10:00:00Z").toEpochMilliseconds(), request.startTime)
        assertEquals(Instant.parse("2026-11-07T11:00:00Z").toEpochMilliseconds(), request.endTime)
        assertNull(request.startDate)
        assertNull(request.endDate)
        assertNull(request.validationError())
    }

    @Test
    fun lateNightStartCarriesSuggestedEndToFollowingDay() {
        val form = CalendarBlockFormState(LocalDate(2026, 11, 7), 1)
        form.selectStartMinutes(23 * 60 + 45)
        assertNull(form.startDate)
        assertNull(form.endDate)
        assertEquals(45, form.endMinutes)
        val request = form.toRequest(TimeZone.UTC)
        assertEquals(3_600_000L, request.endTime - request.startTime)
        assertNull(request.validationError())
    }

    @Test
    fun reversedDatesAndTimesCannotBeSubmitted() {
        val form = CalendarBlockFormState(LocalDate(2026, 11, 7), 1)
        form.selectEndMinutes(10 * 60)
        assertNotNull(form.toRequest(TimeZone.UTC).validationError())
        form.selectEndMinutes(11 * 60)
        form.selectStartDate(LocalDate(2026, 11, 7))
        form.selectEndDate(LocalDate(2026, 11, 6))
        assertNotNull(form.toRequest(TimeZone.UTC).validationError())
    }

    @Test
    fun datesCanBeSelectedAndClearedIndependentlyWithoutChangingClockTimes() {
        val form = CalendarBlockFormState(LocalDate(2026, 11, 7), 1)
        val original = form.toRequest(TimeZone.UTC)
        form.selectStartDate(LocalDate(2026, 11, 8))
        val bounded = form.toRequest(TimeZone.UTC)
        assertEquals(Instant.parse("2026-11-08T00:00:00Z").toEpochMilliseconds(), bounded.startDate)
        assertNull(bounded.endDate)
        assertEquals(original.startTime, bounded.startTime)
        assertEquals(original.endTime, bounded.endTime)
        form.selectStartDate(null)
        form.selectEndDate(LocalDate(2026, 11, 9))
        assertNull(form.toRequest(TimeZone.UTC).startDate)
        form.selectEndDate(null)
        assertEquals(original, form.toRequest(TimeZone.UTC))
    }

    @Test
    fun savedBlockRestoresItsTimesAndOptionalDates() {
        val block = CalendarBlock(5, 2, Instant.parse("2026-11-07T14:15:00Z").toEpochMilliseconds(),
            Instant.parse("2026-11-07T15:30:00Z").toEpochMilliseconds(),
            endDate = Instant.parse("2026-11-10T00:00:00Z").toEpochMilliseconds())
        val form = CalendarBlockFormState(LocalDate(2026, 12, 1), null, block, TimeZone.UTC)
        assertEquals(2, form.employeeId)
        assertEquals(14 * 60 + 15, form.startMinutes)
        assertEquals(15 * 60 + 30, form.endMinutes)
        assertNull(form.startDate)
        assertEquals(LocalDate(2026, 11, 10), form.endDate)
    }

    @Test
    fun requestUsesBackendFieldNamesNumericMillisecondsAndExplicitNullableDates() {
        val request = CalendarBlockWriteRequest(1, 1_793_786_400_000, 1_793_790_000_000)
        val json = Json.parseToJsonElement(Json.encodeToString(request)).jsonObject
        assertEquals("1", json["employee_id"]?.jsonPrimitive?.content)
        assertEquals("1793786400000", json["start_time"]?.jsonPrimitive?.content)
        assertFalse(json.getValue("start_time").jsonPrimitive.isString)
        assertEquals(JsonNull, json["start_date"])
        assertEquals(JsonNull, json["end_date"])
        assertFalse("partner_id" in json)
    }

    @Test
    fun optionalDateBoundsAreValidatedIndependently() {
        val valid = CalendarBlockWriteRequest(1, 100, 200)
        assertNull(valid.validationError())
        assertNull(valid.copy(startDate = 0).validationError())
        assertNull(valid.copy(endDate = 0).validationError())
        for (invalid in listOf(valid.copy(employeeId = 0), valid.copy(startTime = -1),
            valid.copy(endTime = 100), valid.copy(startDate = -1), valid.copy(endDate = -1),
            valid.copy(startDate = 200, endDate = 100))) assertNotNull(invalid.validationError())
    }

    @Test
    fun createAndCalendarResponsesDecodeBlocksWithoutAffectingExistingAppointments() {
        val block = Json.decodeFromString<CalendarBlock>(
            """{"id":10,"partner_id":7,"employee_id":1,"start_time":100,"end_time":200,"start_date":null,"end_date":null}""")
        assertEquals(100L, block.startTime)
        assertEquals(7, block.partnerId)
        val calendar = Json.decodeFromString<AppointmentCalendarResponse>(
            """{"startEpoch":0,"endEpoch":300,"employees":[{"id":1,"name":"Ana","blockedHours":[{"id":10,"employee_id":1,"start_time":100,"end_time":200}]}]}""")
        assertEquals(10, calendar.employees.single().blockedHours.single().id)
        assertEquals(emptyList(), calendar.employees.single().appointments)
    }
}
