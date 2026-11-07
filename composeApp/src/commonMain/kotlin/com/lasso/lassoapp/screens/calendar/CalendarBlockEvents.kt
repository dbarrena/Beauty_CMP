package com.lasso.lassoapp.screens.calendar

import com.lasso.lassoapp.model.EmployeeAppointmentSchedule
import com.lasso.lassoapp.model.Event
import com.lasso.lassoapp.ui.theme.LassoGray
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

internal fun EmployeeAppointmentSchedule.blockEvents(startEpoch: Long, endEpoch: Long, timeZone: TimeZone): List<Event> {
    if (endEpoch <= startEpoch) return emptyList()
    val firstDate = Instant.fromEpochMilliseconds(startEpoch).toLocalDateTime(timeZone).date
    val lastDate = Instant.fromEpochMilliseconds(endEpoch - 1).toLocalDateTime(timeZone).date
    return blockedHours.flatMap { block ->
        val startTime = Instant.fromEpochMilliseconds(block.startTime).toLocalDateTime(timeZone).time
        val endTime = Instant.fromEpochMilliseconds(block.endTime).toLocalDateTime(timeZone).time
        val overnight = endTime <= startTime
        val fromDate = block.startDate?.let { Instant.fromEpochMilliseconds(it).toLocalDateTime(timeZone).date }
        val untilDate = block.endDate?.let { Instant.fromEpochMilliseconds(it).toLocalDateTime(timeZone).date }
        val segments = mutableListOf<Event>()
        var day = firstDate
        while (day <= lastDate) {
            val origins = if (overnight) listOf(day.minus(1, DateTimeUnit.DAY), day) else listOf(day)
            origins.forEach { origin ->
                if ((fromDate == null || origin >= fromDate) && (untilDate == null || origin <= untilDate)) {
                    val start = maxOf(LocalDateTime(origin, startTime), LocalDateTime(day, LocalTime(7, 0)))
                    val end = minOf(LocalDateTime(if (overnight) origin.plus(1, DateTimeUnit.DAY) else origin, endTime),
                        LocalDateTime(day, LocalTime(21, 0)))
                    if (end > start) {
                        val event = Event(name = "Bloqueo de calendario", color = LassoGray,
                            start = start, end = end, employeeId = id, calendarBlockId = block.id)
                        val previous = segments.lastOrNull()
                        if (previous != null && previous.end == start && previous.start.date == day) {
                            segments[segments.lastIndex] = previous.copy(end = end)
                        } else segments += event
                    }
                }
            }
            day = day.plus(1, DateTimeUnit.DAY)
        }
        segments
    }
}
