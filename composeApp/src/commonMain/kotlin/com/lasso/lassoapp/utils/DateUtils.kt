package com.lasso.lassoapp.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
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

fun LocalDate.toEpochMilliseconds(): Long {
    return this.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
}

private fun LocalDate.atStartOfDayIn(timeZone: TimeZone): kotlinx.datetime.Instant {
    return LocalDateTime(year, monthNumber, dayOfMonth, 0, 0).toInstant(timeZone)
}
