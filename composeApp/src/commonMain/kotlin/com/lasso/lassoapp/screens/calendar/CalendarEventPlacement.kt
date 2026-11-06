package com.lasso.lassoapp.screens.calendar

import com.lasso.lassoapp.model.Event
import kotlinx.datetime.LocalDateTime

internal data class CalendarEventPlacement(
    val event: Event,
    val lane: Int,
    val laneCount: Int,
)

/** Events connected by an overlap share a lane count; touching endpoints do not overlap. */
internal fun placeCalendarEvents(events: List<Event>): List<CalendarEventPlacement> {
    val result = mutableListOf<CalendarEventPlacement>()
    val group = mutableListOf<Pair<Event, Int>>()
    val laneEnds = mutableListOf<LocalDateTime>()
    var groupEnd: LocalDateTime? = null

    fun finishGroup() {
        group.forEach { (event, lane) ->
            result += CalendarEventPlacement(event, lane, laneEnds.size)
        }
        group.clear()
        laneEnds.clear()
    }

    events.sortedWith(compareBy<Event> { it.start }.thenByDescending { it.end }).forEach { event ->
        if (groupEnd != null && event.start >= groupEnd) finishGroup()
        val freeLane = laneEnds.indexOfFirst { it <= event.start }
        val lane = if (freeLane == -1) laneEnds.size else freeLane
        if (freeLane == -1) laneEnds += event.end else laneEnds[lane] = event.end
        group += event to lane
        groupEnd = if (group.size == 1) event.end else maxOf(groupEnd!!, event.end)
    }
    finishGroup()
    return result
}
