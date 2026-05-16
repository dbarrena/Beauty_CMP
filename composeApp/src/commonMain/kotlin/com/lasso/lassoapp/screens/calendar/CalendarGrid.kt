package com.lasso.lassoapp.screens.calendar

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.model.Event
import com.lasso.lassoapp.model.minutesBetween
import kotlin.math.roundToInt

@Composable
fun CalendarEvents(
    hourHeight: Dp,
    verticalScrollState: ScrollState,
    events: List<Event>,
    modifier: Modifier = Modifier,
) {
    val visibleHours = 14 // de 7 a 20 inclusive
    val hourStart = 7

    Layout(
        modifier = modifier
            .verticalScroll(verticalScrollState)
            .drawBehind {
                val lineStroke = 1.dp.toPx()
                for (i in 0..visibleHours) {
                    val y = i * hourHeight.toPx()
                    // Hour line
                    drawLine(
                        Color.LightGray,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = lineStroke,
                    )
                    // 30 min line
                    if (i < visibleHours) {
                        val halfY = y + (hourHeight.toPx() / 2)
                        drawLine(
                            Color(0xFFEEEEEE),
                            start = Offset(0f, halfY),
                            end = Offset(size.width, halfY),
                            strokeWidth = lineStroke,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
                        )
                    }
                }
            },
        content = {
            events.sortedBy(Event::start).forEach { event ->
                Box(modifier = Modifier.eventData(event)) {
                    CalendarEventItem(event)
                }
            }
        },
    ) { measurables, constraints ->
        val height = hourHeight.roundToPx() * visibleHours
        val placeablesWithEvents = measurables.map { measurable ->
            val event = measurable.parentData as Event
            val eventHeight =
                ((minutesBetween(event.start, event.end) / 60f) * hourHeight.toPx()).roundToInt()
            val placeable = measurable.measure(
                constraints.copy(
                    minWidth = constraints.maxWidth,
                    minHeight = eventHeight,
                    maxHeight = eventHeight,
                ),
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

private class EventDataModifier(
    val event: Event,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = event
}

private fun Modifier.eventData(event: Event) = this.then(EventDataModifier(event))
