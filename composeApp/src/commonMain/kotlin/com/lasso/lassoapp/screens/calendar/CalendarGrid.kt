package com.lasso.lassoapp.screens.calendar

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.zIndex
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.model.Event
import com.lasso.lassoapp.model.minutesBetween
import androidx.compose.ui.input.pointer.PointerInputChange
import kotlin.math.roundToInt

@Composable
internal fun CalendarEvents(
    hourHeight: Dp,
    verticalScrollState: ScrollState,
    events: List<Event>,
    onEditAppointment: (Event) -> Unit = {},
    onEmptySlotClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
    arrangeOverlaps: Boolean = false,
    dragEnabled: Boolean = false,
    onDragChanged: (CalendarAppointmentDrag?) -> Unit = {},
    onAppointmentMoved: (Event, Int) -> Unit = { _, _ -> },
    dragAfterLongPress: Boolean = true,
    scrollInternally: Boolean = true,
    currentTimeMinutes: Int? = null,
    compactShortEvents: Boolean = false,
) {
    val visibleHours = 14 // de 7 a 20 inclusive
    val hourStart = 7
    val haptic = LocalHapticFeedback.current
    var drag by remember { mutableStateOf<CalendarAppointmentDrag?>(null) }
    var dragPixels by remember { mutableFloatStateOf(0f) }
    val currentOnDragChanged by rememberUpdatedState(onDragChanged)
    val currentOnAppointmentMoved by rememberUpdatedState(onAppointmentMoved)
    DisposableEffect(Unit) {
        onDispose { if (drag != null) currentOnDragChanged(null) }
    }
    val placements = remember(events, arrangeOverlaps) {
        if (arrangeOverlaps) placeCalendarEvents(events)
        else events.sortedBy(Event::start).map { CalendarEventPlacement(it, 0, 1) }
    }

    Layout(
        modifier = modifier
            .pointerInput(events, hourHeight, verticalScrollState.value) {
                detectTapGestures { position ->
                    val contentY = position.y + if (scrollInternally) verticalScrollState.value else 0
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
            .then(if (scrollInternally) Modifier.verticalScroll(
                state = verticalScrollState,
                overscrollEffect = null,
            ) else Modifier)
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
                currentTimeMinutes?.takeIf { it in hourStart * 60..(hourStart + visibleHours) * 60 }?.let {
                    val y = (it - hourStart * 60) / 60f * hourHeight.toPx()
                    drawLine(Color(0xFFEA625B), Offset(0f, y), Offset(size.width, y), 1.5.dp.toPx())
                    drawCircle(Color(0xFFEA625B), 4.dp.toPx(), Offset(4.dp.toPx(), y))
                }
            },
        content = {
            placements.forEach { placement ->
                val event = placement.event
                val activeDrag = drag?.takeIf { it.event.appointmentId == event.appointmentId }
                val dragModifier = if (dragEnabled && event.canDragAppointment()) Modifier.pointerInput(event, hourHeight, dragAfterLongPress) {
                    val startDrag: (Offset) -> Unit = {
                        dragPixels = 0f
                        drag = CalendarAppointmentDrag(event, event.start.hour * 60 + event.start.minute)
                        currentOnDragChanged(drag)
                        if (dragAfterLongPress) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    val updateDrag: (PointerInputChange, Offset) -> Unit = { change, amount ->
                        change.consume()
                        dragPixels += amount.y
                        val start = snappedAppointmentStart(event.start.hour * 60 + event.start.minute,
                            minutesBetween(event.start, event.end).toInt(), dragPixels, hourHeight.toPx())
                        drag = CalendarAppointmentDrag(event, start)
                        currentOnDragChanged(drag)
                    }
                    val endDrag: () -> Unit = {
                        val finished = drag
                        drag = null
                        currentOnDragChanged(null)
                        if (finished != null && finished.startMinutes != event.start.hour * 60 + event.start.minute) {
                            currentOnAppointmentMoved(event, finished.startMinutes)
                        }
                    }
                    val cancelDrag: () -> Unit = {
                        drag = null
                        currentOnDragChanged(null)
                    }
                    if (dragAfterLongPress) detectDragGesturesAfterLongPress(startDrag, endDrag, cancelDrag, updateDrag)
                    else detectDragGestures(onDragStart = startDrag, onDragEnd = endDrag,
                        onDragCancel = cancelDrag, onDrag = updateDrag)
                } else Modifier
                Box(modifier = Modifier.eventData(placement)
                    .zIndex(if (activeDrag != null) 2f else if (event.calendarBlockId != null) 0f else 1f).then(dragModifier)) {
                    if (activeDrag != null) {
                        CalendarEventItem(event = event, isPlaceholder = true)
                    }
                    CalendarEventItem(
                        event = activeDrag?.preview ?: event,
                        onEdit = { if (activeDrag == null) onEditAppointment(event) },
                        isDragging = activeDrag != null,
                        compactShortEvents = compactShortEvents,
                        modifier = Modifier.graphicsLayer {
                            translationY = if (activeDrag == null) 0f else
                                (activeDrag.startMinutes - (event.start.hour * 60 + event.start.minute)) / 60f * hourHeight.toPx()
                        },
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
