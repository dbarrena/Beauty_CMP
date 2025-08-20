package com.beauty.beautyapp.model

import androidx.compose.ui.graphics.Color
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

data class Event(
    val name: String,
    val color: Color,
    val start: LocalDateTime,
    val end: LocalDateTime,
    val description: String? = null
)

fun minutesBetween(start: LocalDateTime, end: LocalDateTime, timeZone: TimeZone = TimeZone.currentSystemDefault()): Long {
    val startInstant = start.toInstant(timeZone)
    val endInstant = end.toInstant(timeZone)
    return (endInstant - startInstant).inWholeMinutes
}

fun LocalDateTime.formatEventTime(): String {
    val hour = this.hour.toString().padStart(2, '0')
    val minute = this.minute.toString().padStart(2, '0')
    return "$hour:$minute"
}

fun LocalDateTime.formatEventTimeAmPm(): String {
    val hour24 = this.hour
    val minute = this.minute.toString().padStart(2, '0')

    val amPm = if (hour24 < 12) "AM" else "PM"
    val hour12 = when {
        hour24 == 0 || hour24 == 12 -> 12
        hour24 > 12 -> hour24 - 12
        else -> hour24
    }

    val hourStr = hour12.toString().padStart(2, '0')
    return "$hourStr:$minute $amPm"
}


val sampleEvents = listOf(
    Event(
        name = "Google I/O Keynote",
        color = Color(0xFFAFBBF2),
        start = LocalDateTime.parse("2021-05-18T11:00:00"),
        end = LocalDateTime.parse("2021-05-18T12:00:00"),
        description = "Tune in to find out about how we're furthering our mission to organize the world’s information and make it universally accessible and useful.",
    ),
    Event(
        name = "Developer Keynote",
        color = Color(0xFFAFBBF2),
        start = LocalDateTime.parse("2021-05-18T13:00:00"),
        end = LocalDateTime.parse("2021-05-18T15:00:00"),
        description = "Learn about the latest updates to our developer products and platforms from Google Developers.",
    ),
    Event(
        name = "What's new in Android",
        color = Color(0xFF1B998B),
        start = LocalDateTime.parse("2021-05-18T15:00:00"),
        end = LocalDateTime.parse("2021-05-18T17:00:00"),
        description = "In this Keynote, Chet Haase, Dan Sandler, and Romain Guy discuss the latest Android features and enhancements for developers.",
    ),
)
