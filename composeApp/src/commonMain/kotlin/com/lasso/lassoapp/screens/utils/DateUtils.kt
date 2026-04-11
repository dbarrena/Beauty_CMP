package com.lasso.lassoapp.screens.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

fun Long.toLocalDateTimeString(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return localDateTime.formatDdMmYyyyHhMm()
}

private val MONTHS_ES_SHORT =
    listOf("ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic")

/** e.g. "24 feb - 10:30" for sales list cards */
fun Long.toSaleCardDateTimeString(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val ldt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val mon = MONTHS_ES_SHORT[ldt.monthNumber - 1]
    val h = ldt.hour.toString().padStart(2, '0')
    val m = ldt.minute.toString().padStart(2, '0')
    return "${ldt.dayOfMonth} $mon - $h:$m"
}

fun Long.toLocalDateString(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return localDateTime.date.formatDdMmYyyy()
}

fun Long.toPickerDateString(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val localDateTime = instant.toLocalDateTime(TimeZone.UTC)
    return localDateTime.date.formatDdMmYyyy()
}


fun Long.toEpochMillisToLocalDateTime(): LocalDateTime {
    val instant = Instant.fromEpochMilliseconds(this)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return localDateTime
}

fun LocalDateTime.formatDdMmYyyyHhMm(): String {
    val day = dayOfMonth.toString().padStart(2, '0')
    val month = monthNumber.toString().padStart(2, '0')
    val year = year.toString()
    val hour = hour.toString().padStart(2, '0')
    val minute = minute.toString().padStart(2, '0')
    return "$day/$month/$year $hour:$minute"
}

fun LocalDate.formatDdMmYyyy(): String {
    val day = dayOfMonth.toString().padStart(2, '0')
    val month = monthNumber.toString().padStart(2, '0')
    val year = year.toString()
    return "$day/$month/$year"
}

fun LocalDateTime.toEpochMillis(timeZone: TimeZone = TimeZone.currentSystemDefault()): Long =
    this.toInstant(timeZone).toEpochMilliseconds()

