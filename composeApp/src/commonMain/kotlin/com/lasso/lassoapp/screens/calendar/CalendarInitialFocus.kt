package com.lasso.lassoapp.screens.calendar

import com.lasso.lassoapp.model.Event
import kotlinx.datetime.LocalDate

internal data class CalendarAppointmentFocus(val columnIndex: Int, val startMinutes: Int)

internal fun firstAppointmentFocus(
    events: List<Event>,
    dates: List<LocalDate>,
    employeeIds: List<Int>,
    viewMode: CalendarViewMode,
): CalendarAppointmentFocus? {
    val firstAppointment = events
        .filter { event ->
            event.appointmentId != null && event.calendarBlockId == null &&
                event.start.date in dates &&
                (viewMode == CalendarViewMode.ThreeDays || event.employeeId in employeeIds)
        }
        // All displayed days share the time axis. Focus the earliest time of day anywhere in it.
        .minWithOrNull(compareBy<Event> { it.start.hour * 60 + it.start.minute }.thenBy { it.start })
        ?: return null
    val column = if (viewMode == CalendarViewMode.ThreeDays) {
        dates.indexOf(firstAppointment.start.date)
    } else {
        employeeIds.indexOf(firstAppointment.employeeId)
    }
    return CalendarAppointmentFocus(column, firstAppointment.start.hour * 60 + firstAppointment.start.minute)
}
