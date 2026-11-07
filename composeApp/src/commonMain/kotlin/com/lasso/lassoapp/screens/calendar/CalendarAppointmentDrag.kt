package com.lasso.lassoapp.screens.calendar

import com.lasso.lassoapp.model.Event
import com.lasso.lassoapp.model.minutesBetween
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.math.roundToInt

internal data class CalendarAppointmentDrag(val event: Event, val startMinutes: Int) {
    val preview: Event get() = event.atStartMinutes(startMinutes)
}

internal fun snappedAppointmentStart(originalMinutes: Int, durationMinutes: Int, deltaPixels: Float, hourPixels: Float): Int {
    require(hourPixels > 0 && durationMinutes in 1..840)
    val snapped = ((originalMinutes + deltaPixels * 60f / hourPixels) / 15f).roundToInt() * 15
    val latestStart = ((21 * 60 - durationMinutes) / 15) * 15
    return snapped.coerceIn(7 * 60, latestStart)
}

internal fun Event.atStartMinutes(startMinutes: Int, timeZone: TimeZone = TimeZone.currentSystemDefault()): Event {
    if (startMinutes == start.hour * 60 + start.minute) return this
    val newStart = LocalDateTime(start.date, LocalTime(startMinutes / 60, startMinutes % 60))
    val duration = end.toInstant(timeZone) - start.toInstant(timeZone)
    return copy(start = newStart, end = (newStart.toInstant(timeZone) + duration).toLocalDateTime(timeZone))
}

internal fun Event.canDragAppointment(): Boolean =
    appointmentId != null && employeeId != null && minutesBetween(start, end) in 1..840
