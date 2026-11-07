package com.lasso.lassoapp.screens.calendar.dialog

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.lasso.lassoapp.model.CalendarBlockWriteRequest
import com.lasso.lassoapp.model.CalendarBlock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

internal class CalendarBlockFormState(
    initialDate: LocalDate,
    initialEmployeeId: Int?,
    block: CalendarBlock? = null,
    private val timeZone: TimeZone = TimeZone.currentSystemDefault(),
) {
    private val anchorDate = block?.startTime?.let { Instant.fromEpochMilliseconds(it).toLocalDateTime(timeZone).date } ?: initialDate
    var employeeId by mutableStateOf(block?.employeeId ?: initialEmployeeId)
    var startDate by mutableStateOf(block?.startDate?.let { Instant.fromEpochMilliseconds(it).toLocalDateTime(timeZone).date })
        private set
    var endDate by mutableStateOf(block?.endDate?.let { Instant.fromEpochMilliseconds(it).toLocalDateTime(timeZone).date })
        private set
    var startMinutes by mutableStateOf(block?.startTime?.minutesOfDay() ?: (10 * 60))
        private set
    var endMinutes by mutableStateOf(block?.endTime?.minutesOfDay() ?: (11 * 60))
        private set

    fun selectStartDate(date: LocalDate?) { startDate = date }

    fun selectEndDate(date: LocalDate?) { endDate = date }

    fun selectStartMinutes(minutes: Int) {
        startMinutes = minutes
        if (endMinutes <= minutes) {
            endMinutes = (minutes + 60) % (24 * 60)
        }
    }

    fun selectEndMinutes(minutes: Int) { endMinutes = minutes }

    fun toRequest(timeZone: TimeZone = this.timeZone): CalendarBlockWriteRequest =
        CalendarBlockWriteRequest(
            employeeId = employeeId ?: 0,
            startTime = LocalDateTime(anchorDate, LocalTime(startMinutes / 60, startMinutes % 60))
                .toInstant(timeZone).toEpochMilliseconds(),
            endTime = LocalDateTime(if (endMinutes < startMinutes) anchorDate.plus(1, DateTimeUnit.DAY) else anchorDate,
                LocalTime(endMinutes / 60, endMinutes % 60))
                .toInstant(timeZone).toEpochMilliseconds(),
            startDate = startDate?.atStartOfDayIn(timeZone)?.toEpochMilliseconds(),
            endDate = endDate?.atStartOfDayIn(timeZone)?.toEpochMilliseconds(),
        )

    private fun Long.minutesOfDay(): Int = Instant.fromEpochMilliseconds(this).toLocalDateTime(timeZone).let {
        it.hour * 60 + it.minute
    }
}
