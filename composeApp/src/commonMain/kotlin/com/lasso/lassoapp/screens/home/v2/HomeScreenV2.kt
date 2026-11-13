package com.lasso.lassoapp.screens.home.v2

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.screens.home.HomeScreenViewModel
import com.lasso.lassoapp.model.HomeAppointment
import com.lasso.lassoapp.model.CalendarAppointment
import com.lasso.lassoapp.screens.calendar.CalendarScreenViewModel
import com.lasso.lassoapp.screens.calendar.CalendarDialogs
import kotlinx.coroutines.flow.first
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreenV2(
    onNavigateToPos: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToSales: () -> Unit,
    onNavigateToCashClosure: () -> Unit,
    onNavigateToCommissions: () -> Unit,
) {
    val viewModel = koinViewModel<HomeScreenViewModel>()
    val state = viewModel.state.collectAsState()
    var selectedAppointment by remember { mutableStateOf<HomeAppointment?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        if (state.value.isLoading) {
            HomeLoadingPlaceholders(isAdmin = state.value.isAdmin)
        } else {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp, bottom = 24.dp),
            ) {
                HomeHeaderTitle(partnerName = state.value.partnerName)
                Spacer(Modifier.height(16.dp))
                if (state.value.isAdmin) {
                    HomeMetricsCards(
                        todayEarningsFormatted = state.value.todayEarningsFormatted,
                        todayTransactionCount = state.value.todayTransactionCount,
                        weekEarningsFormatted = state.value.weekEarningsFormatted,
                        appointmentsToday = state.value.appointmentsToday,
                        appointmentsPending = state.value.appointmentsPending,
                    )
                    Spacer(Modifier.height(20.dp))
                }
                HomeQuickActionsRow(
                    onNuevaVenta = onNavigateToPos,
                    onAgendar = onNavigateToCalendar,
                    onVentas = onNavigateToSales,
                    onCorteCaja = onNavigateToCashClosure,
                    onComisiones = onNavigateToCommissions,
                    isAdmin = state.value.isAdmin,
                )
                // Disabling this to avoid complexity on the home endpoint,
                // will include this chart on reports screen eventually...
                /*if (state.value.isAdmin) {
                    Spacer(Modifier.height(24.dp))
                    HomeWeeklySalesChart(lastSevenDays = state.value.lastSevenDays)
                }*/
                Spacer(Modifier.height(12.dp))
                HomeAgendaSection(
                    appointments = state.value.home?.appointments.orEmpty(),
                    onVerTodo = onNavigateToCalendar,
                    onAppointmentClick = { selectedAppointment = it },
                )
            }
        }
    }
    selectedAppointment?.let { appointment ->
        HomeAgendaAppointmentEditor(appointment) {
            selectedAppointment = null
            viewModel.refresh()
        }
    }
}

@Composable
private fun HomeAgendaAppointmentEditor(appointment: HomeAppointment, onClose: () -> Unit) {
    val viewModel = koinViewModel<CalendarScreenViewModel>(key = "home-agenda-appointment")
    val state by viewModel.state.collectAsState()
    LaunchedEffect(appointment.id) {
        viewModel.showEditAppointmentDialog(CalendarAppointment(
            id = appointment.id, client = appointment.client, services = appointment.services,
            startsAt = appointment.startsAt, endsAt = appointment.endsAt, notes = appointment.notes,
        ), employeeId = appointment.employee.id)
        viewModel.state.first { !it.isAppointmentDialogDisplayed }
        onClose()
    }
    CalendarDialogs(state, viewModel, selectedEmployeeId = appointment.employee.id)
}
