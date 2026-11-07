package com.lasso.lassoapp.screens.calendar

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
internal fun CalendarAppointmentAutoScroll(
    loadRevision: Int,
    selectedEmployeeId: Int?,
    enabled: Boolean,
    target: CalendarAppointmentFocus?,
    columnWidthPx: Float,
    hourHeightPx: Float,
    horizontalScrollState: ScrollState,
    verticalScrollState: ScrollState,
    keepLastColumn: Boolean = false,
) {
    // A successful response advances the revision even when the returned appointments are unchanged.
    // A gesture's response arrives before its range animation finishes. Retry when that transition
    // releases the scroll states so focus uses the incoming range's columns and scroll bounds.
    LaunchedEffect(loadRevision, selectedEmployeeId, enabled) {
        if (loadRevision == 0 || !enabled || target == null) return@LaunchedEffect
        // Resume after the loaded columns have had a layout pass, then wait for both scroll viewports.
        withFrameNanos { }
        snapshotFlow {
            horizontalScrollState.viewportSize > 0 && horizontalScrollState.maxValue != Int.MAX_VALUE &&
                verticalScrollState.viewportSize > 0 && verticalScrollState.maxValue != Int.MAX_VALUE
        }.first { it }
        // Backward navigation stays at the range's right edge; the time still comes from all columns.
        val horizontalOffset = if (keepLastColumn) horizontalScrollState.maxValue
            else (target.columnIndex * columnWidthPx).roundToInt()
        // The grid starts at 07:00. Leave a little space above the appointment for context.
        val verticalOffset = ((target.startMinutes - 7 * 60 - 15).coerceAtLeast(0) / 60f * hourHeightPx).roundToInt()
        coroutineScope {
            launch {
                horizontalScrollState.animateScrollTo(
                    horizontalOffset.coerceIn(0, horizontalScrollState.maxValue), tween(450),
                )
            }
            launch {
                verticalScrollState.animateScrollTo(
                    verticalOffset.coerceIn(0, verticalScrollState.maxValue), tween(450),
                )
            }
        }
    }
}
