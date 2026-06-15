package com.lasso.lassoapp.utils

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

fun LocalDate.formatMonthYearSpanish(): String {
    val monthName = when (month) {
        Month.JANUARY -> "enero"
        Month.FEBRUARY -> "febrero"
        Month.MARCH -> "marzo"
        Month.APRIL -> "abril"
        Month.MAY -> "mayo"
        Month.JUNE -> "junio"
        Month.JULY -> "julio"
        Month.AUGUST -> "agosto"
        Month.SEPTEMBER -> "septiembre"
        Month.OCTOBER -> "octubre"
        Month.NOVEMBER -> "noviembre"
        Month.DECEMBER -> "diciembre"
        else -> month.name.lowercase()
    }
    return "$monthName de $year"
}

fun LocalDate.formatFullDateSpanish(): String {
    val dayName = when (dayOfWeek.name) {
        "MONDAY" -> "lunes"
        "TUESDAY" -> "martes"
        "WEDNESDAY" -> "miércoles"
        "THURSDAY" -> "jueves"
        "FRIDAY" -> "viernes"
        "SATURDAY" -> "sábado"
        "SUNDAY" -> "domingo"
        else -> dayOfWeek.name.lowercase()
    }
    val monthName = when (month) {
        Month.JANUARY -> "enero"
        Month.FEBRUARY -> "febrero"
        Month.MARCH -> "marzo"
        Month.APRIL -> "abril"
        Month.MAY -> "mayo"
        Month.JUNE -> "junio"
        Month.JULY -> "julio"
        Month.AUGUST -> "agosto"
        Month.SEPTEMBER -> "septiembre"
        Month.OCTOBER -> "octubre"
        Month.NOVEMBER -> "noviembre"
        Month.DECEMBER -> "diciembre"
        else -> month.name.lowercase()
    }
    return "$dayName, $dayOfMonth de $monthName"
}

fun Long.toLocalDate(): LocalDate {
    return kotlinx.datetime.Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.UTC).date
}

fun LocalDate.toEpochMilliseconds(timeZone: TimeZone = TimeZone.UTC): Long {
    return this.atStartOfDayIn(timeZone).toEpochMilliseconds()
}

fun LocalDate.atEndOfDayEpochMilliseconds(timeZone: TimeZone = TimeZone.UTC): Long {
    return this.plus(1, DateTimeUnit.DAY).atStartOfDayIn(timeZone).toEpochMilliseconds() - 1
}

fun LocalDate.atStartOfMonth(): LocalDate {
    return LocalDate(year, month, 1)
}

fun LocalDate.atEndOfMonth(): LocalDate {
    val nextMonth = if (monthNumber == 12) 1 else monthNumber + 1
    val nextMonthYear = if (monthNumber == 12) year + 1 else year
    val firstDayOfNextMonth = LocalDate(nextMonthYear, nextMonth, 1)
    return kotlinx.datetime.Instant.fromEpochMilliseconds(
        firstDayOfNextMonth.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds() - 1
    ).toLocalDateTime(TimeZone.UTC).date
}

fun LocalDate.atStartOfWeek(): LocalDate {
    val daysToSubtract = when (dayOfWeek) {
        kotlinx.datetime.DayOfWeek.MONDAY -> 0
        kotlinx.datetime.DayOfWeek.TUESDAY -> 1
        kotlinx.datetime.DayOfWeek.WEDNESDAY -> 2
        kotlinx.datetime.DayOfWeek.THURSDAY -> 3
        kotlinx.datetime.DayOfWeek.FRIDAY -> 4
        kotlinx.datetime.DayOfWeek.SATURDAY -> 5
        kotlinx.datetime.DayOfWeek.SUNDAY -> 6
        else -> 0
    }
    return this.minus(daysToSubtract, DateTimeUnit.DAY)
}

fun LocalDate.atEndOfWeek(): LocalDate {
    val daysToAdd = when (dayOfWeek) {
        kotlinx.datetime.DayOfWeek.MONDAY -> 6
        kotlinx.datetime.DayOfWeek.TUESDAY -> 5
        kotlinx.datetime.DayOfWeek.WEDNESDAY -> 4
        kotlinx.datetime.DayOfWeek.THURSDAY -> 3
        kotlinx.datetime.DayOfWeek.FRIDAY -> 2
        kotlinx.datetime.DayOfWeek.SATURDAY -> 1
        kotlinx.datetime.DayOfWeek.SUNDAY -> 0
        else -> 0
    }
    return this.plus(daysToAdd, DateTimeUnit.DAY)
}

private fun LocalDate.atStartOfDayIn(timeZone: TimeZone): kotlinx.datetime.Instant {
    return LocalDateTime(year, monthNumber, dayOfMonth, 0, 0).toInstant(timeZone)
}
