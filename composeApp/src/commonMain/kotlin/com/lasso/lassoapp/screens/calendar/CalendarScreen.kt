package com.lasso.lassoapp.screens.calendar

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.model.Event
import com.lasso.lassoapp.model.minutesBetween
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@Composable
fun CalendarScreen(modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<CalendarScreenViewModel>()

    CalendarScreenContent(modifier, viewModel)
}

@Composable
private fun CalendarScreenContent(
    modifier: Modifier = Modifier,
    viewModel: CalendarScreenViewModel
) {
    val state = viewModel.state.collectAsState()
    val hourHeight = 75.dp
    val verticalScrollState = rememberScrollState()

    Row(modifier = modifier) {
        CalendarHourSidebar(hourHeight, modifier.verticalScroll(verticalScrollState))

        CalendarEvents(hourHeight, verticalScrollState, state.value.events)
    }
}

@Composable
private fun CalendarEvents(hourHeight: Dp, verticalScrollState: ScrollState, events: List<Event>) {
    val visibleHours = 14 // de 7 a 20 inclusive
    val hourStart = 7
    val hourEnd = 20

    Layout(
        modifier = Modifier
            .verticalScroll(verticalScrollState)
            .drawBehind {
                for (i in 1 until visibleHours) {
                    val y = i * hourHeight.toPx()
                    drawLine(
                        Color.LightGray,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }
        ,
        content = {
            events.sortedBy(Event::start).forEach { event ->
                Box(modifier = Modifier.eventData(event)) {
                    CalendarEventItem(event)
                }
            }
        }
    ) { measurables, constraints ->
        val height = hourHeight.roundToPx() * visibleHours
        val placeablesWithEvents = measurables.map { measurable ->
            val event = measurable.parentData as Event
            val eventHeight =
                ((minutesBetween(event.start, event.end) / 60f) * hourHeight.toPx()).roundToInt()
            val placeable = measurable.measure(
                constraints.copy(
                    minHeight = eventHeight,
                    maxHeight = eventHeight
                )
            )
            Pair(placeable, event)
        }

        layout(constraints.maxWidth, height) {
            placeablesWithEvents.forEach { (placeable, event) ->
                val minutesFromMidnightToEventStart = event.start.hour * 60 + event.start.minute
                val minutesOffsetFromStartHour = minutesFromMidnightToEventStart - hourStart * 60

                val eventY = ((minutesOffsetFromStartHour / 60f) * hourHeight.toPx()).roundToInt()

                placeable.place(0, eventY)
            }
        }
    }
}