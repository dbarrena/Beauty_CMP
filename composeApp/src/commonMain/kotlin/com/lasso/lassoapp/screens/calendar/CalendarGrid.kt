package com.lasso.lassoapp.screens.calendar

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
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
    onEditAppointment: (Event) -> Unit = {},
    onEmptySlotClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
    arrangeOverlaps: Boolean = false,
) {
    val visibleHours = 14 // de 7 a 20 inclusive
    val hourStart = 7
    val placements = remember(events, arrangeOverlaps) {
        if (arrangeOverlaps) placeCalendarEvents(events)
        else events.sortedBy(Event::start).map { CalendarEventPlacement(it, 0, 1) }
    }

    Layout(
        modifier = modifier
            .pointerInput(events, hourHeight, verticalScrollState.value) {
                detectTapGestures { position ->
                    val contentY = position.y + verticalScrollState.value
                    val halfHourHeight = hourHeight.toPx() / 2f
                    val slotIndex = (contentY / halfHourHeight).toInt()
                        .coerceIn(0, visibleHours * 2 - 1)
                    val slotStart = hourStart * 60 + slotIndex * 30
                    val slotEnd = slotStart + 30
                    val isOccupied = events.any { event ->
                        val eventStart = event.start.hour * 60 + event.start.minute
                        val eventEnd = event.end.hour * 60 + event.end.minute
                        slotStart < eventEnd && slotEnd > eventStart
                    }
                    if (!isOccupied) {
                        onEmptySlotClick(slotStart)
                    }
                }
            }
            .verticalScroll(
                state = verticalScrollState,
                overscrollEffect = null,
            )
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
            placements.forEach { placement ->
                val event = placement.event
                Box(modifier = Modifier.eventData(placement)) {
                    CalendarEventItem(
                        event = event,
                        onEdit = { onEditAppointment(event) },
                    )
                }
            }
        },
    ) { measurables, constraints ->
        val height = hourHeight.roundToPx() * visibleHours
        val placeablesWithEvents = measurables.map { measurable ->
            val placement = measurable.parentData as CalendarEventPlacement
            val event = placement.event
            val left = constraints.maxWidth * placement.lane / placement.laneCount
            val right = constraints.maxWidth * (placement.lane + 1) / placement.laneCount
            val eventHeight =
                ((minutesBetween(event.start, event.end) / 60f) * hourHeight.toPx()).roundToInt()
            val placeable = measurable.measure(
                constraints.copy(
                    minWidth = right - left,
                    maxWidth = right - left,
                    minHeight = eventHeight,
                    maxHeight = eventHeight,
                ),
            )
            Pair(placeable, placement)
        }

        layout(constraints.maxWidth, height) {
            placeablesWithEvents.forEach { (placeable, placement) ->
                val event = placement.event
                val minutesFromMidnightToEventStart = event.start.hour * 60 + event.start.minute
                val minutesOffsetFromStartHour = minutesFromMidnightToEventStart - hourStart * 60

                val eventY = ((minutesOffsetFromStartHour / 60f) * hourHeight.toPx()).roundToInt()

                val eventX = constraints.maxWidth * placement.lane / placement.laneCount
                placeable.placeRelative(eventX, eventY)
            }
        }
    }
}

private class EventDataModifier(
    val placement: CalendarEventPlacement,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = placement
}

private fun Modifier.eventData(placement: CalendarEventPlacement) = this.then(EventDataModifier(placement))
