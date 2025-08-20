package com.beauty.beautyapp.screens.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalTime

@Composable
fun CalendarHourSidebar(
    hourHeight: Dp,
    modifier: Modifier = Modifier,
    label: @Composable (time: LocalTime) -> Unit = { BasicSidebarLabel(time = it) },
) {
    Column(modifier = modifier) {
        for (i in 7..20) {
            val time = LocalTime(hour = i, minute = 0)
            Box(modifier = Modifier.height(hourHeight)) {
                label(time)
            }
        }
    }
}

@Composable
fun BasicSidebarLabel(
    time: LocalTime,
    modifier: Modifier = Modifier,
) {
    Text(
        text = time.toHourAmPm(),
        modifier = modifier
            .fillMaxHeight()
            .padding(4.dp)
    )
}

private fun LocalTime.toHourAmPm(): String {
    val hour12 = when {
        hour == 0 || hour == 12 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    val period = if (hour < 12) "AM" else "PM"
    return "$hour12 $period"
}