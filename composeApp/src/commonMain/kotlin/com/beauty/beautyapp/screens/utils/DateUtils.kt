package com.beauty.beautyapp.screens.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Long.toLocalDateTimeString(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return localDateTime.formatDdMmYyyyHhMm()
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
