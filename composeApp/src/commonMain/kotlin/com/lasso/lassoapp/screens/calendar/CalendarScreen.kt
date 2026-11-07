package com.lasso.lassoapp.screens.calendar

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import com.lasso.lassoapp.model.CalendarAppointment
import com.lasso.lassoapp.model.EmployeeAppointmentSchedule
import com.lasso.lassoapp.model.Event
import com.lasso.lassoapp.screens.utils.rememberLoadingShimmer
import com.lasso.lassoapp.ui.theme.LassoPrimary
import com.lasso.lassoapp.ui.theme.LassoTextPrimary
import com.lasso.lassoapp.ui.theme.calendarColorOrPrimary
import com.lasso.lassoapp.utils.formatFullDateSpanish
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlinx.coroutines.flow.first
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CalendarScreen(modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<CalendarScreenViewModel>()
    val state by viewModel.state.collectAsState()
    val isThreeDayView = state.viewMode == CalendarViewMode.ThreeDays
    val initialShimmer = if (state.isLoading && !state.hasLoadedCalendar) rememberLoadingShimmer() else null
    var selectedEmployeeId by remember { mutableStateOf<Int?>(null) }
    var outgoingRange by remember { mutableStateOf<CalendarRangeSnapshot?>(null) }
    var appointmentDrag by remember { mutableStateOf<CalendarAppointmentDrag?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.calendarBlockNotice) {
        state.calendarBlockNotice?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearCalendarBlockNotice()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            outgoingRange = null
            snackbarHostState.showSnackbar(it)
        }
    }

    LaunchedEffect(state.employeeSchedules) {
        if (selectedEmployeeId != null && state.employeeSchedules.none { it.id == selectedEmployeeId }) {
            selectedEmployeeId = null
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF8F8FA),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            CalendarAddActions(
                onAddAppointment = { viewModel.showNewAppointmentDialog() },
                onAddCalendarBlock = { viewModel.showCalendarBlockDialog() },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.isLoading && !state.isLoadingNextRange && !state.isLoadingPreviousRange) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            } else {
                Spacer(modifier = Modifier.height(4.dp))
            }

            if (!isThreeDayView) {
                Text(
                    text = state.selectedDate.formatFullDateSpanish(),
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp, start = 16.dp, end = 16.dp),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = LassoTextPrimary,
                    textAlign = TextAlign.Center,
                )
            }

            if (initialShimmer != null) {
                CalendarLoadingPills(initialShimmer, Modifier.padding(top = 16.dp))
            } else if (state.employeeSchedules.isNotEmpty()) {
                EmployeeFilterPills(
                    employees = state.employeeSchedules,
                    selectedEmployeeId = selectedEmployeeId,
                    informative = isThreeDayView,
                    onEmployeeSelected = { selectedEmployeeId = it },
                    modifier = Modifier.padding(top = 16.dp),
                )
            }

            val verticalScrollState = rememberScrollState()
            var horizontalScrollState by rememberSaveable(stateSaver = ScrollState.Saver) {
                mutableStateOf(ScrollState(0))
            }
            val hourHeight = 90.dp
            val sidebarWidth = 60.dp
            val headerHeight = if (isThreeDayView) 72.dp else if (selectedEmployeeId == null) 58.dp else 42.dp
            val displayedEmployees = if (selectedEmployeeId == null) {
                state.employeeSchedules
            } else {
                state.employeeSchedules.filter { it.id == selectedEmployeeId }
            }
                
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                    val availableCalendarWidth = maxWidth - sidebarWidth - 16.dp
                    val density = LocalDensity.current
                    val previousRange = outgoingRange
                    val columnWidth = employeeColumnWidth(availableCalendarWidth, displayedEmployees.size, selectedEmployeeId)
                    val previousEmployees = previousRange?.employees.orEmpty().filter {
                        selectedEmployeeId == null || it.id == selectedEmployeeId
                    }
                    val previousColumnWidth = employeeColumnWidth(availableCalendarWidth, previousEmployees.size, selectedEmployeeId)
                    val isRangeTransition = previousRange != null && previousRange.viewMode == state.viewMode &&
                        state.selectedDate == previousRange.startDate.plus(
                            previousRange.direction.dayOffset(state.viewMode.dayCount), DateTimeUnit.DAY)
                    val isBackwardTransition = isRangeTransition && previousRange.direction == CalendarRangeDirection.Previous
                    val incomingWidth = if (isBackwardTransition) with(density) {
                        if (isThreeDayView) calendarDayColumnWidth(
                            List(3) { state.selectedDate.plus(it, DateTimeUnit.DAY) },
                            state.events,
                            availableCalendarWidth,
                        ).roundToPx() * 3
                        else (columnWidth * displayedEmployees.size.coerceAtLeast(1)).roundToPx()
                    } else 0
                    // Starting this scroll state at the old first day keeps prepended columns offscreen
                    // until the backward animation begins, without flashing the new range's first day.
                    val activeScrollState = if (isBackwardTransition) {
                        remember(state.selectedDate, previousRange) { ScrollState(incomingWidth) }
                    } else horizontalScrollState

                    LaunchedEffect(state.selectedDate, state.viewMode) {
                        if (isBackwardTransition) {
                            snapshotFlow { activeScrollState.maxValue }
                                .first { it != Int.MAX_VALUE && it >= incomingWidth }
                            val viewportWidth = with(density) { availableCalendarWidth.roundToPx() }
                            val incomingEnd = (incomingWidth - viewportWidth).coerceAtLeast(0)
                            activeScrollState.animateScrollTo(incomingEnd, tween(durationMillis = 450))
                            horizontalScrollState = activeScrollState
                            outgoingRange = null
                        } else if (isRangeTransition) {
                            val incomingStart = with(density) {
                                if (isThreeDayView) calendarDayColumnWidth(
                                    previousRange.dates,
                                    previousRange.events,
                                    availableCalendarWidth,
                                ).roundToPx() * 3
                                else (previousColumnWidth * previousEmployees.size.coerceAtLeast(1)).roundToPx()
                            }
                            // Wait for both ranges to be measured before scrolling into the incoming range.
                            snapshotFlow { horizontalScrollState.maxValue }.first { it >= incomingStart }
                            horizontalScrollState.animateScrollTo(incomingStart, tween(durationMillis = 450))
                            // Removing the outgoing columns and adjusting the offset in the same frame
                            // leaves the incoming first day in exactly the same visible position.
                            outgoingRange = null
                            horizontalScrollState.scrollTo(0)
                        } else {
                            outgoingRange = null
                            horizontalScrollState.scrollTo(0)
                        }
                    }
                    val focusDates = List(state.viewMode.dayCount) { state.selectedDate.plus(it, DateTimeUnit.DAY) }
                    val focusColumnWidth = if (isThreeDayView) {
                        calendarDayColumnWidth(focusDates, state.events, availableCalendarWidth)
                    } else columnWidth
                    CalendarAppointmentAutoScroll(
                        loadRevision = state.calendarLoadRevision,
                        selectedEmployeeId = selectedEmployeeId,
                        enabled = !state.isLoading && !isRangeTransition,
                        target = firstAppointmentFocus(
                            state.events, focusDates, displayedEmployees.map { it.id }, state.viewMode,
                        ),
                        columnWidthPx = with(density) { focusColumnWidth.toPx() },
                        hourHeightPx = with(density) { hourHeight.toPx() },
                        horizontalScrollState = activeScrollState,
                        verticalScrollState = verticalScrollState,
                        keepLastColumn = state.calendarLoadDirection == CalendarRangeDirection.Previous,
                    )
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp, vertical = 10.dp)
                        ) {
                            Column(modifier = Modifier.width(sidebarWidth)) {
                                Spacer(modifier = Modifier.height(headerHeight))
                                CalendarHourSidebar(
                                    hourHeight = hourHeight,
                                    draggedStartMinutes = appointmentDrag?.startMinutes,
                                    draggedColor = appointmentDrag?.event?.color ?: LassoPrimary,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 8.dp)
                                        .verticalScroll(
                                            state = verticalScrollState,
                                            overscrollEffect = null,
                                        )
                                )
                            }

                            CalendarRangeScroller(
                                modifier = Modifier.fillMaxSize(),
                                scrollState = activeScrollState,
                                enabled = initialShimmer == null && appointmentDrag == null && !state.isSavingAppointment,
                                dayCount = state.viewMode.dayCount,
                                isLoading = state.isLoading || isRangeTransition,
                                isLoadingNextRange = state.isLoadingNextRange,
                                isLoadingPreviousRange = state.isLoadingPreviousRange,
                                userScrollEnabled = initialShimmer == null && !isRangeTransition && appointmentDrag == null,
                                onNextRange = {
                                    if (!state.isLoading && !isRangeTransition) {
                                        outgoingRange = CalendarRangeSnapshot(state.selectedDate, state.events,
                                            CalendarRangeDirection.Next, state.viewMode, state.employeeSchedules)
                                        viewModel.loadNextRange()
                                    }
                                },
                                onPreviousRange = {
                                    if (!state.isLoading && !isRangeTransition) {
                                        outgoingRange = CalendarRangeSnapshot(state.selectedDate, state.events,
                                            CalendarRangeDirection.Previous, state.viewMode, state.employeeSchedules)
                                        viewModel.loadPreviousRange()
                                    }
                                },
                            ) {
                                if (initialShimmer != null) {
                                    CalendarLoadingColumns(
                                        brush = initialShimmer,
                                        columnWidth = if (isThreeDayView) maxOf(240.dp, availableCalendarWidth / 2.2f)
                                            else employeeColumnWidth(availableCalendarWidth, 2, null),
                                        headerHeight = headerHeight,
                                        hourHeight = hourHeight,
                                        verticalScrollState = verticalScrollState,
                                    )
                                } else if (isThreeDayView) {
                                    if (isRangeTransition && !isBackwardTransition) {
                                        CalendarDayColumns(
                                            dates = previousRange.dates,
                                            events = previousRange.events,
                                            availableWidth = availableCalendarWidth,
                                            hourHeight = hourHeight,
                                            headerHeight = headerHeight,
                                            verticalScrollState = verticalScrollState,
                                            onEditAppointment = {},
                                            onEmptySlotClick = { _, _ -> },
                                        )
                                    }
                                    CalendarDayColumns(
                                        dates = List(3) { state.selectedDate.plus(it, DateTimeUnit.DAY) },
                                        events = state.events,
                                        availableWidth = availableCalendarWidth,
                                        dragEnabled = !isRangeTransition && !state.isLoading && !state.isSavingAppointment && !state.isDeleting,
                                        onDragChanged = { appointmentDrag = it },
                                        onAppointmentMoved = viewModel::moveAppointment,
                                        hourHeight = hourHeight,
                                        headerHeight = headerHeight,
                                        verticalScrollState = verticalScrollState,
                                        onEditAppointment = { event ->
                                            if (event.calendarBlockId != null) {
                                                viewModel.showCalendarBlockDialog(event.calendarBlockId)
                                            } else {
                                            val employee = state.employeeSchedules.firstOrNull { it.id == event.employeeId }
                                            val appointment = employee?.appointments?.firstOrNull { it.id == event.appointmentId }
                                            if (appointment != null) {
                                                viewModel.showEditAppointmentDialog(appointment, employee.id)
                                            }
                                            }
                                        },
                                        onEmptySlotClick = { date, startMinutes ->
                                            viewModel.showNewAppointmentDialog(
                                                date = date,
                                                startMinutes = startMinutes,
                                            )
                                        },
                                    )
                                    if (isBackwardTransition) {
                                        CalendarDayColumns(
                                            dates = previousRange.dates,
                                            events = previousRange.events,
                                            availableWidth = availableCalendarWidth,
                                            hourHeight = hourHeight,
                                            headerHeight = headerHeight,
                                            verticalScrollState = verticalScrollState,
                                            onEditAppointment = {},
                                            onEmptySlotClick = { _, _ -> },
                                        )
                                    }
                                } else {
                                    if (isRangeTransition && !isBackwardTransition) {
                                        CalendarEmployeeColumns(previousEmployees, previousRange.events,
                                            previousColumnWidth, hourHeight, headerHeight, verticalScrollState,
                                            selectedEmployeeId == null)
                                    }
                                    CalendarEmployeeColumns(displayedEmployees, state.events,
                                        columnWidth, hourHeight, headerHeight, verticalScrollState,
                                        selectedEmployeeId == null,
                                        dragEnabled = !isRangeTransition && !state.isLoading && !state.isSavingAppointment && !state.isDeleting,
                                        onDragChanged = { appointmentDrag = it },
                                        onAppointmentMoved = viewModel::moveAppointment,
                                        onEditAppointment = viewModel::showEditAppointmentDialog,
                                        onOpenCalendarBlock = { viewModel.showCalendarBlockDialog(it) },
                                        onEmptySlotClick = { employeeId, startMinutes ->
                                            viewModel.showNewAppointmentDialog(employeeId, state.selectedDate, startMinutes)
                                        })
                                    if (isBackwardTransition) {
                                        CalendarEmployeeColumns(previousEmployees, previousRange.events,
                                            previousColumnWidth, hourHeight, headerHeight, verticalScrollState,
                                            selectedEmployeeId == null)
                                    }
                                }
                            }
                        }
                    }
            }
        }
    }

    CalendarDialogs(state, viewModel, selectedEmployeeId)

}

@Composable
private fun CalendarEmployeeColumns(
    employees: List<EmployeeAppointmentSchedule>,
    events: List<Event>,
    columnWidth: Dp,
    hourHeight: Dp,
    headerHeight: Dp,
    verticalScrollState: ScrollState,
    showEmployeeName: Boolean,
    onEditAppointment: (CalendarAppointment, Int) -> Unit = { _, _ -> },
    onEmptySlotClick: (Int, Int) -> Unit = { _, _ -> },
    dragEnabled: Boolean = false,
    onDragChanged: (CalendarAppointmentDrag?) -> Unit = {},
    onAppointmentMoved: (Event, Int) -> Unit = { _, _ -> },
    onOpenCalendarBlock: (Int) -> Unit = {},
) {
    if (employees.isEmpty()) Box(Modifier.width(columnWidth))
    employees.forEach { employee ->
        Column(Modifier.width(columnWidth)) {
            EmployeeHeader(employee.name, employee.appointments.size, showEmployeeName,
                Modifier.height(headerHeight).fillMaxWidth())
            CalendarEvents(
                hourHeight = hourHeight,
                verticalScrollState = verticalScrollState,
                events = events.filter { it.employeeId == employee.id },
                onEditAppointment = { event ->
                    if (event.calendarBlockId != null) onOpenCalendarBlock(event.calendarBlockId) else {
                    employee.appointments.firstOrNull { it.id == event.appointmentId }?.let {
                        onEditAppointment(it, employee.id)
                    }
                    }
                },
                onEmptySlotClick = { onEmptySlotClick(employee.id, it) },
                modifier = Modifier.fillMaxSize(),
                dragEnabled = dragEnabled,
                onDragChanged = onDragChanged,
                onAppointmentMoved = onAppointmentMoved,
            )
        }
    }
}

private fun employeeColumnWidth(availableWidth: Dp, employeeCount: Int, selectedEmployeeId: Int?): Dp =
    if (selectedEmployeeId != null) availableWidth
    else maxOf(140.dp, availableWidth / employeeCount.coerceAtLeast(1).toFloat())

@Composable
private fun EmployeeHeader(
    employeeName: String,
    appointmentCount: Int,
    showEmployeeName: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (showEmployeeName) {
            Text(
                text = employeeName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF353D3C)
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
        Text(
            text = appointmentCountLabel(appointmentCount),
            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF7E8585)),
            maxLines = 1,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun EmployeeFilterPills(
    employees: List<EmployeeAppointmentSchedule>,
    selectedEmployeeId: Int?,
    onEmployeeSelected: (Int?) -> Unit,
    informative: Boolean = false,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (!informative) {
            item {
                EmployeeFilterPill(
                    label = "Todos",
                    color = LassoPrimary,
                    selected = selectedEmployeeId == null,
                    onClick = { onEmployeeSelected(null) },
                )
            }
        }
        items(employees, key = { it.id }) { employee ->
            EmployeeFilterPill(
                label = employee.name,
                color = calendarColorOrPrimary(employee.calendarColor),
                selected = informative || selectedEmployeeId == employee.id,
                onClick = if (informative) null else ({ onEmployeeSelected(employee.id) }),
            )
        }
    }
}

@Composable
private fun EmployeeFilterPill(
    label: String,
    color: Color,
    selected: Boolean,
    onClick: (() -> Unit)?,
) {
    Box(
        modifier = Modifier
            .background(
                color = if (selected) color else color.copy(alpha = 0.15f),
                shape = RoundedCornerShape(50),
            )
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 22.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else color,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private fun appointmentCountLabel(count: Int): String =
    if (count == 1) "1 cita" else "$count citas"

private data class CalendarRangeSnapshot(
    val startDate: LocalDate,
    val events: List<Event>,
    val direction: CalendarRangeDirection,
    val viewMode: CalendarViewMode,
    val employees: List<EmployeeAppointmentSchedule>,
) {
    val dates: List<LocalDate> get() = List(3) { startDate.plus(it, DateTimeUnit.DAY) }
}

