package com.lasso.lassoapp.screens.calendar

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.model.Event
import com.lasso.lassoapp.utils.formatFullDateSpanish
import kotlinx.datetime.LocalDate

@Composable
internal fun CalendarDayColumns(
    dates: List<LocalDate>,
    events: List<Event>,
    availableWidth: Dp,
    hourHeight: Dp,
    headerHeight: Dp,
    verticalScrollState: ScrollState,
    onEditAppointment: (Event) -> Unit,
    onEmptySlotClick: (LocalDate, Int) -> Unit,
) {
    val eventsByDate = remember(events) { events.groupBy { it.start.date } }
    val columnWidth = remember(dates, events, availableWidth) {
        calendarDayColumnWidth(dates, events, availableWidth)
    }

    dates.forEach { date ->
        val dayEvents = eventsByDate[date].orEmpty()
        Column(
            modifier = Modifier.width(columnWidth).drawBehind {
                drawLine(
                    color = Color(0xFFEEEEEE),
                    start = Offset(size.width, 0f),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx(),
                )
            },
        ) {
            Column(
                modifier = Modifier.height(headerHeight).fillMaxWidth().padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = date.formatFullDateSpanish(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF353D3C),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                )
                Text(
                    text = if (dayEvents.size == 1) "1 cita" else "${dayEvents.size} citas",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF7E8585),
                )
            }
            CalendarEvents(
                hourHeight = hourHeight,
                verticalScrollState = verticalScrollState,
                events = dayEvents,
                onEditAppointment = onEditAppointment,
                onEmptySlotClick = { startMinutes -> onEmptySlotClick(date, startMinutes) },
                modifier = Modifier.fillMaxSize(),
                arrangeOverlaps = true,
            )
        }
    }
}

internal fun calendarDayColumnWidth(dates: List<LocalDate>, events: List<Event>, availableWidth: Dp): Dp {
    val eventsByDate = events.groupBy { it.start.date }
    val maxLanes = dates.maxOf { date ->
        placeCalendarEvents(eventsByDate[date].orEmpty()).maxOfOrNull { it.laneCount } ?: 1
    }
    // Leave a peek of the third day when space allows, with readable lanes for concurrent appointments.
    return maxOf(240.dp, availableWidth / 2.2f, 120.dp * maxLanes)
}
