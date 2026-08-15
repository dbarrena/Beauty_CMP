package com.lasso.lassoapp.screens.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.model.EmployeeAppointmentSchedule
import com.lasso.lassoapp.screens.calendar.dialog.AppointmentDialog
import com.lasso.lassoapp.screens.clients.dialog.ClientDialog
import com.lasso.lassoapp.utils.formatFullDateSpanish
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CalendarScreen(modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<CalendarScreenViewModel>()
    val state by viewModel.state.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedEmployeeId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(state.employeeSchedules) {
        if (selectedEmployeeId != null && state.employeeSchedules.none { it.id == selectedEmployeeId }) {
            selectedEmployeeId = null
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF8F8FA)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            } else {
                Spacer(modifier = Modifier.height(4.dp))
            }

            CalendarHeader(
                modifier = Modifier.padding(vertical = 16.dp),
                onAddClick = { viewModel.showNewAppointmentDialog() },
            )

            // Date Selector
            CalendarDateSelector(
                modifier = Modifier,
                selectedDateFormatted = state.selectedDate.formatFullDateSpanish(),
                onPreviousDay = { viewModel.onPreviousDay() },
                onNextDay = { viewModel.onNextDay() },
                onDatePickerClick = { showDatePicker = true }
            )

            if (state.employeeSchedules.isNotEmpty()) {
                EmployeeFilterPills(
                    employees = state.employeeSchedules,
                    selectedEmployeeId = selectedEmployeeId,
                    onEmployeeSelected = { selectedEmployeeId = it },
                    modifier = Modifier.padding(top = 16.dp),
                )
            }

            val verticalScrollState = rememberScrollState()
            val horizontalScrollState = rememberScrollState()
            val hourHeight = 90.dp
            val sidebarWidth = 60.dp
            val headerHeight = if (selectedEmployeeId == null) 58.dp else 42.dp
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
                    val columnWidth = if (selectedEmployeeId != null) {
                        availableCalendarWidth
                    } else {
                        maxOf(140.dp, availableCalendarWidth / displayedEmployees.size.coerceAtLeast(1).toFloat())
                    }

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
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 8.dp)
                                        .verticalScroll(
                                            state = verticalScrollState,
                                            overscrollEffect = null,
                                        )
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .horizontalScroll(horizontalScrollState)
                            ) {
                                displayedEmployees.forEach { employee ->
                                    Column(modifier = Modifier.width(columnWidth)) {
                                        EmployeeHeader(
                                            employeeName = employee.name,
                                            appointmentCount = employee.appointments.size,
                                            showEmployeeName = selectedEmployeeId == null,
                                            modifier = Modifier
                                                .height(headerHeight)
                                                .fillMaxWidth(),
                                        )
                                        CalendarEvents(
                                            hourHeight = hourHeight,
                                            verticalScrollState = verticalScrollState,
                                            events = state.events.filter { it.employeeId == employee.id },
                                            onEditAppointment = { event ->
                                                val appointment = employee.appointments.firstOrNull {
                                                    it.id == event.appointmentId
                                                }
                                                if (appointment != null) {
                                                    viewModel.showEditAppointmentDialog(
                                                        appointment = appointment,
                                                        employeeId = employee.id,
                                                    )
                                                }
                                            },
                                            onEmptySlotClick = { startMinutes ->
                                                viewModel.showNewAppointmentDialog(
                                                    employeeId = employee.id,
                                                    date = state.selectedDate,
                                                    startMinutes = startMinutes,
                                                )
                                            },
                                            modifier = Modifier.fillMaxSize(),
                                        )
                                    }
                                }
                            }
                        }
                    }
            }
        }
    }

    if (showDatePicker) {
        CalendarPickerDialog(
            initialDate = state.selectedDate,
            onDismiss = { showDatePicker = false },
            onConfirm = { date ->
                viewModel.onDateSelected(date)
                showDatePicker = false
            }
        )
    }


    if (state.isAppointmentDialogDisplayed) {
        AppointmentDialog(
            appointment = state.selectedAppointment,
            appointmentEmployeeId = state.selectedAppointmentEmployeeId,
            clients = state.clients,
            employees = state.employees,
            services = state.services,
            initialDate = state.initialAppointmentDate ?: state.selectedDate,
            initialStartMinutes = state.initialAppointmentStartMinutes,
            preferredClient = state.createdClient,
            isSaving = state.isSavingAppointment,
            isDeleting = state.isDeleting,
            optionsError = state.optionsError ?: state.mutationError,
            onDismiss = viewModel::hideAppointmentDialog,
            onNewClient = viewModel::showNewClientDialog,
            onResult = viewModel::saveAppointment,
            onDelete = {
                state.selectedAppointment?.let { viewModel.deleteAppointment(it.id) }
            },
        )
    }

    if (state.isNewClientDialogDisplayed) {
        ClientDialog(
            client = null,
            isLoading = state.isSavingClient,
            error = state.clientSaveError,
            onDismiss = viewModel::hideNewClientDialog,
            onSave = viewModel::createClient,
        )
    }
}

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
                style = MaterialTheme.typography.titleSmall.copy(
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
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            EmployeeFilterPill(
                label = "Todos",
                selected = selectedEmployeeId == null,
                onClick = { onEmployeeSelected(null) },
            )
        }
        items(employees, key = { it.id }) { employee ->
            EmployeeFilterPill(
                label = employee.name,
                selected = selectedEmployeeId == employee.id,
                onClick = { onEmployeeSelected(employee.id) },
            )
        }
    }
}

@Composable
private fun EmployeeFilterPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .background(
                color = if (selected) Color(0xFF00CFAE) else Color(0xFFEEF1F4),
                shape = RoundedCornerShape(50),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 22.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else Color(0xFF89939F),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private fun appointmentCountLabel(count: Int): String =
    if (count == 1) "1 cita" else "$count citas"

@Composable
private fun CalendarHeader(
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Citas",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF353D3C)
                )
            )
            Text(
                text = "Gestiona tu agenda",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF9CA3AF)
                )
            )
        }

        FloatingActionButton(
            onClick = onAddClick,
            containerColor = Color(0xFF00D1AD),
            contentColor = Color.White,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Agregar cita"
            )
        }
    }
}

@Composable
private fun CalendarDateSelector(
    modifier: Modifier = Modifier,
    selectedDateFormatted: String,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onDatePickerClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onPreviousDay) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Day", tint = Color(0xFF353D3C))
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onDatePickerClick),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Fecha seleccionada",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF9CA3AF),
                        fontSize = 12.sp
                    )
                )
                Text(
                    text = selectedDateFormatted,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF353D3C),
                        fontSize = 16.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }

            IconButton(onClick = onNextDay) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Next Day", tint = Color(0xFF353D3C))
            }
        }
    }
}

