package com.lasso.lassoapp.desktop

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.model.EmployeeAppointmentSchedule
import com.lasso.lassoapp.model.Event
import com.lasso.lassoapp.screens.calendar.*
import com.lasso.lassoapp.ui.theme.*
import com.lasso.lassoapp.utils.formatFullDateSpanish
import kotlinx.datetime.*

@Composable
internal fun DesktopCalendarTimeline(
    state: CalendarScreenState,
    employees: List<EmployeeAppointmentSchedule>,
    events: List<Event>,
    now: LocalDateTime,
    enabled: Boolean,
    vertical: ScrollState,
    autoFocusEnabled: Boolean,
    onOpenEvent: (Event) -> Unit,
    onEmptySlot: (LocalDate, Int?, Int) -> Unit,
    onMove: (Event, Int) -> Unit,
) {
    val horizontal = rememberScrollState()
    val hourHeight = 80.dp
    val headerHeight = 88.dp
    val density = LocalDensity.current
    val threeDays = state.viewMode == CalendarViewMode.ThreeDays
    val dates = List(state.viewMode.dayCount) { state.selectedDate.plus(it, DateTimeUnit.DAY) }
    var drag by remember { mutableStateOf<CalendarAppointmentDrag?>(null) }

    Row(Modifier.fillMaxSize().clipToBounds()) {
        Column(Modifier.width(68.dp)) {
            Spacer(Modifier.height(headerHeight))
            CalendarHourSidebar(hourHeight, Modifier.weight(1f).fillMaxWidth().padding(start = 12.dp)
                .verticalScroll(vertical, overscrollEffect = null),
                draggedStartMinutes = drag?.startMinutes, draggedColor = drag?.event?.color ?: LassoPrimary)
        }
        BoxWithConstraints(Modifier.weight(1f).fillMaxHeight().padding(end = 12.dp)) {
            val count = if (threeDays) 3 else employees.size.coerceAtLeast(1)
            val columnWidth = maxOf(if (threeDays) 260.dp else 220.dp, maxWidth / count)
            CalendarAppointmentAutoScroll(
                loadRevision = state.calendarLoadRevision,
                selectedEmployeeId = null,
                enabled = autoFocusEnabled && !state.isLoading && drag == null,
                target = firstAppointmentFocus(events, dates, employees.map { it.id }, state.viewMode),
                columnWidthPx = with(density) { columnWidth.toPx() },
                hourHeightPx = with(density) { hourHeight.toPx() },
                horizontalScrollState = horizontal,
                verticalScrollState = vertical,
            )
            Column(Modifier.fillMaxSize()) {
                Column(Modifier.weight(1f).horizontalScroll(horizontal, enabled = drag == null)) {
                    Row(Modifier.height(headerHeight)) {
                        repeat(count) { index ->
                            val employee = if (threeDays) null else employees.getOrNull(index)
                            val date = if (threeDays) dates[index] else state.selectedDate
                            val columnEvents = events.filter { if (threeDays) it.start.date == date else it.employeeId == employee?.id }
                            Column(Modifier.width(columnWidth).fillMaxHeight().timelineDivider()
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically)) {
                                if (employee != null) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Surface(color = calendarColorOrPrimary(employee.calendarColor), shape = CircleShape, modifier = Modifier.size(9.dp)) {}
                                        Text(employee.name, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                } else Text(if (state.isLoading && employees.isEmpty()) "Cargando…" else date.formatFullDateSpanish(),
                                    fontWeight = FontWeight.SemiBold, color = if (date == now.date) LassoPrimaryDark else LassoTextPrimary,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                                val appointments = columnEvents.count { it.appointmentId != null }
                                Text(if (appointments == 1) "1 cita" else "$appointments citas", color = LassoGray,
                                    style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                            }
                        }
                    }
                    Row(Modifier.weight(1f).verticalScroll(vertical, enabled = drag == null, overscrollEffect = null)) {
                        repeat(count) { index ->
                            val employee = if (threeDays) null else employees.getOrNull(index)
                            val date = if (threeDays) dates[index] else state.selectedDate
                            val columnEvents = events.filter { if (threeDays) it.start.date == date else it.employeeId == employee?.id }
                            CalendarEvents(hourHeight, vertical, columnEvents,
                                onEditAppointment = { if (enabled) onOpenEvent(it) },
                                onEmptySlotClick = { if (enabled) onEmptySlot(date, employee?.id ?: employees.singleOrNull()?.id, it) },
                                modifier = Modifier.width(columnWidth).height(hourHeight * 14).timelineDivider(),
                                arrangeOverlaps = true, dragEnabled = enabled, dragAfterLongPress = false,
                                scrollInternally = false, compactShortEvents = true,
                                onDragChanged = { drag = it }, onAppointmentMoved = onMove,
                                currentTimeMinutes = if (date == now.date) now.hour * 60 + now.minute else null)
                        }
                    }
                }
                HorizontalScrollbar(rememberScrollbarAdapter(horizontal), Modifier.fillMaxWidth().height(10.dp))
            }
            VerticalScrollbar(rememberScrollbarAdapter(vertical), Modifier.align(Alignment.CenterEnd)
                .padding(top = headerHeight, bottom = 10.dp).fillMaxHeight())
        }
    }
}

private fun Modifier.timelineDivider() = drawBehind {
    drawLine(LassoDivider, Offset(size.width, 0f), Offset(size.width, size.height), 1.dp.toPx())
}
