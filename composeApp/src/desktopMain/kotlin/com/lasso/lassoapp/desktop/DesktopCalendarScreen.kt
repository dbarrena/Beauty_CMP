package com.lasso.lassoapp.desktop

import androidx.compose.foundation.*
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.model.Event
import com.lasso.lassoapp.model.room.Session
import com.lasso.lassoapp.screens.calendar.*
import com.lasso.lassoapp.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock

@Composable
internal fun DesktopCalendarScreen(session: Session, onLogout: suspend () -> Unit) {
    val viewModel = koinViewModel<CalendarScreenViewModel>()
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    var hiddenEmployees by remember { mutableStateOf(emptySet<Int>()) }
    var accountOpen by remember { mutableStateOf(false) }
    var createOpen by remember { mutableStateOf(false) }
    var loggingOut by remember { mutableStateOf(false) }
    val calendarVerticalScroll = rememberScrollState()
    var focusedRevision by remember { mutableIntStateOf(0) }
    LaunchedEffect(state.calendarLoadRevision) {
        if (state.calendarLoadRevision != 0) {
            delay(600)
            focusedRevision = state.calendarLoadRevision
        }
    }
    var now by remember { mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())) }
    LaunchedEffect(Unit) {
        while (true) {
            now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            delay(30_000)
        }
    }
    LaunchedEffect(state.error) { state.error?.let { snackbar.showSnackbar(it) } }
    LaunchedEffect(state.calendarBlockNotice) {
        state.calendarBlockNotice?.let { snackbar.showSnackbar(it); viewModel.clearCalendarBlockNotice() }
    }
    val employees = state.employeeSchedules.filterNot { it.id in hiddenEmployees }
    val busy = state.isLoading || state.isSavingAppointment || state.isDeleting ||
        state.isSavingCalendarBlock || state.isDeletingCalendarBlock || loggingOut
    val today = now.date
    val preferredEmployee = employees.singleOrNull()?.id

    Row(Modifier.fillMaxSize()) {
        DesktopNavigationRail()
    Scaffold(modifier = Modifier.weight(1f), containerColor = LassoPageBackground, snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Row(Modifier.fillMaxWidth().height(80.dp).padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                DesktopLassoLogo(Modifier.padding(start = 8.dp, end = 16.dp).width(110.dp).height(40.dp))
                Spacer(Modifier.width(32.dp))
                OutlinedButton(onClick = { viewModel.onDateSelected(today) }, enabled = !busy,
                    shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, LassoDivider)) { Text("Hoy", color = LassoTextPrimary) }
                CalendarDateNavigationButton(CalendarRangeDirection.Previous, !busy,
                    state.isLoadingPreviousRange, viewModel::loadPreviousRange)
                CalendarDateNavigationButton(CalendarRangeDirection.Next, !busy,
                    state.isLoadingNextRange, viewModel::loadNextRange)
                Text(calendarDateHeaderLabel(state.selectedDate, state.viewMode), style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f).padding(horizontal = 12.dp))
                var modeOpen by remember { mutableStateOf(false) }
                Box {
                    OutlinedButton(onClick = { modeOpen = true }, enabled = !busy, shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, LassoDivider)) {
                        Text(if (state.viewMode == CalendarViewMode.Day) "Día" else "3 días", color = LassoTextPrimary)
                        Icon(Icons.Default.KeyboardArrowDown, null, modifier = Modifier.size(18.dp))
                    }
                    DropdownMenu(modeOpen, onDismissRequest = { modeOpen = false }) {
                        CalendarViewMode.entries.forEach { mode -> DropdownMenuItem(
                            text = { Text(if (mode == CalendarViewMode.Day) "Día · por empleado" else "3 días") },
                            onClick = { modeOpen = false; viewModel.onViewModeSelected(mode) }) }
                    }
                }
                IconButton(onClick = viewModel::retry, enabled = !busy) {
                    Icon(Icons.Default.Refresh, "Actualizar calendario", tint = LassoPrimary)
                }
                Box {
                    Surface(onClick = { accountOpen = true }, color = LassoPrimary.copy(alpha = .14f), shape = CircleShape,
                        modifier = Modifier.padding(start = 12.dp).size(40.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(session.employeeName.take(1).uppercase(), color = LassoPrimaryDark, fontWeight = FontWeight.Bold)
                        }
                    }
                    DropdownMenu(accountOpen, onDismissRequest = { accountOpen = false }) {
                        Text(session.employeeName, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                        Text(session.partnerName, color = LassoGray, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
                        HorizontalDivider(Modifier.padding(vertical = 8.dp))
                        DropdownMenuItem(text = { Text("Cerrar sesión") }, enabled = !busy, onClick = {
                            accountOpen = false
                            loggingOut = true
                            scope.launch {
                                try { onLogout() }
                                catch (exception: kotlinx.coroutines.CancellationException) { throw exception }
                                catch (exception: Exception) { snackbar.showSnackbar(exception.message ?: "No se pudo cerrar la sesión.") }
                                finally { loggingOut = false }
                            }
                        })
                    }
                }
            }
            Row(Modifier.weight(1f).padding(end = 20.dp, bottom = 20.dp)) {
                Column(Modifier.width(250.dp).fillMaxHeight().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
                    Box(Modifier.padding(bottom = 24.dp)) {
                        Button(onClick = { createOpen = true }, enabled = !busy,
                            shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().height(52.dp)) {
                            Icon(Icons.Default.Add, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Crear", fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.weight(1f))
                            Icon(Icons.Default.KeyboardArrowDown, null)
                        }
                        DropdownMenu(createOpen, onDismissRequest = { createOpen = false }) {
                            DropdownMenuItem(text = { Text("Nueva cita") }, leadingIcon = { Icon(Icons.Default.Event, null) },
                                onClick = { createOpen = false; viewModel.showNewAppointmentDialog(employeeId = preferredEmployee) })
                            DropdownMenuItem(text = { Text("Bloquear horario") }, leadingIcon = { Icon(Icons.Default.Block, null) },
                                onClick = { createOpen = false; viewModel.showCalendarBlockDialog() })
                        }
                    }
                    DesktopMonthPicker(state.selectedDate, today, enabled = !busy, onDateSelected = viewModel::onDateSelected)
                    HorizontalDivider(Modifier.padding(vertical = 24.dp), color = LassoDivider)
                    Text("Mi equipo", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))
                    if (state.isLoading && !state.hasLoadedCalendar) {
                        Text("Cargando calendarios…", color = LassoGray, style = MaterialTheme.typography.bodySmall)
                    } else if (state.employeeSchedules.isEmpty()) {
                        Text("No hay calendarios disponibles.", color = LassoGray, style = MaterialTheme.typography.bodySmall)
                    }
                    state.employeeSchedules.forEach { employee ->
                        val checked = employee.id !in hiddenEmployees
                        Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).clickable {
                            hiddenEmployees = if (checked) hiddenEmployees + employee.id else hiddenEmployees - employee.id
                        }.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = checked, onCheckedChange = {
                                hiddenEmployees = if (it) hiddenEmployees - employee.id else hiddenEmployees + employee.id
                            }, colors = CheckboxDefaults.colors(checkedColor = calendarColorOrPrimary(employee.calendarColor)))
                            Text(employee.name, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    Text("Selecciona un espacio para crear una cita. Arrastra una cita para cambiar su horario.",
                        color = LassoGray, style = MaterialTheme.typography.bodySmall, lineHeight = 19.sp)
                }
                Surface(Modifier.weight(1f).fillMaxHeight(), shape = RoundedCornerShape(24.dp), color = Color.White,
                    border = BorderStroke(1.dp, LassoOutlineHairline)) {
                    Column {
                        Box(Modifier.fillMaxWidth().height(4.dp)) {
                            if (state.isLoading || state.isSavingAppointment) LinearProgressIndicator(Modifier.fillMaxWidth())
                        }
                        if (state.error != null) Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.errorContainer)
                            .padding(horizontal = 16.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(state.error.orEmpty(), modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onErrorContainer)
                            TextButton(onClick = viewModel::retry, enabled = !busy) { Text("Reintentar") }
                        }
                        AnimatedContent(
                            targetState = state to hiddenEmployees,
                            contentKey = { (page, hidden) -> Triple(page.selectedDate, page.viewMode, page.employeeSchedules.map { it.id }.filterNot { it in hidden }) },
                            transitionSpec = {
                                if (targetState.first.selectedDate == initialState.first.selectedDate &&
                                    targetState.first.viewMode == initialState.first.viewMode) {
                                    val adding = targetState.first.employeeSchedules.count { it.id !in targetState.second } >
                                        initialState.first.employeeSchedules.count { it.id !in initialState.second }
                                    slideInHorizontally(tween(300)) { if (adding) it else -it } togetherWith
                                        slideOutHorizontally(tween(300)) { if (adding) -it else it }
                                } else {
                                    val forward = targetState.first.selectedDate >= initialState.first.selectedDate
                                    slideInHorizontally(tween(350)) { if (forward) it else -it } togetherWith
                                        slideOutHorizontally(tween(350)) { if (forward) -it else it }
                                }
                            },
                            label = "calendar range",
                        ) { (page, pageHidden) ->
                            val pageEmployees = page.employeeSchedules.filterNot { it.id in pageHidden }
                            val pageEvents = page.events.filterNot {
                                it.employeeId in pageHidden ||
                                    (page.viewMode != CalendarViewMode.Day && it.calendarBlockId != null)
                            }
                            if (!page.isLoading && pageEmployees.isEmpty()) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Icon(Icons.Default.CalendarMonth, null, tint = LassoPrimaryDark, modifier = Modifier.size(36.dp))
                                        Text(if (page.employeeSchedules.isEmpty()) "No hay calendarios disponibles" else "Selecciona un calendario de tu equipo", color = LassoGray)
                                    }
                                }
                            } else DesktopCalendarTimeline(page, pageEmployees, pageEvents, now, !busy && page.selectedDate == state.selectedDate && pageHidden == hiddenEmployees, calendarVerticalScroll,
                            autoFocusEnabled = page.calendarLoadRevision != focusedRevision,
                            onOpenEvent = { event ->
                                if (event.calendarBlockId != null) viewModel.showCalendarBlockDialog(event.calendarBlockId)
                                else state.employeeSchedules.firstOrNull { it.id == event.employeeId }?.let { employee ->
                                    employee.appointments.firstOrNull { it.id == event.appointmentId }?.let {
                                        viewModel.showEditAppointmentDialog(it, employee.id)
                                    }
                                }
                            }, onEmptySlot = { date, employeeId, minutes -> viewModel.showNewAppointmentDialog(employeeId, date, minutes) },
                            onMove = viewModel::moveAppointment)
                        }
                    }
                }
            }
        }
    }
    }
    CalendarDialogs(state, viewModel, preferredEmployee)
}
