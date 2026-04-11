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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.screens.home.HomeScreenViewModel
import com.lasso.lassoapp.screens.utils.FullScreenLoading
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreenV2(
    onNavigateToPos: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToSales: () -> Unit,
    onNavigateToCashClosure: () -> Unit,
) {
    val viewModel = koinViewModel<HomeScreenViewModel>()
    val state = viewModel.state.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        if (state.value.isLoading) {
            FullScreenLoading()
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
                HomeMetricsCards(
                    todayEarningsFormatted = state.value.todayEarningsFormatted,
                    todayTransactionCount = state.value.todayTransactionCount,
                    weekEarningsFormatted = state.value.weekEarningsFormatted,
                    appointmentsToday = state.value.appointmentsToday,
                    appointmentsPending = state.value.appointmentsPending,
                )
                Spacer(Modifier.height(20.dp))
                HomeQuickActionsRow(
                    onNuevaVenta = onNavigateToPos,
                    onAgendar = onNavigateToCalendar,
                    onVentas = onNavigateToSales,
                    onCorteCaja = onNavigateToCashClosure,
                )
                Spacer(Modifier.height(24.dp))
                HomeWeeklySalesChart(lastSevenDays = state.value.lastSevenDays)
                Spacer(Modifier.height(24.dp))
                HomeAgendaSection(onVerTodo = onNavigateToCalendar)
            }
        }
    }
}
