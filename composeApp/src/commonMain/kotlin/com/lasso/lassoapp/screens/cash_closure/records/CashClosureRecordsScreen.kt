package com.lasso.lassoapp.screens.cash_closure.records

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.model.CashClosureRecordsResponse
import com.lasso.lassoapp.screens.sales.detail.SaleDetailsDialogScreen
import com.lasso.lassoapp.screens.utils.CurrentMonthDateRangePicker
import com.lasso.lassoapp.screens.utils.FullScreenLoading
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CashClosureRecordsScreen() {
    val viewModel = koinViewModel<CashClosureRecordsScreenViewModel>()
    CashClosureRecordsScreenContent(viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CashClosureRecordsScreenContent(viewModel: CashClosureRecordsScreenViewModel) {
    val state = viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false,
        )
    )

    if (state.value.isLoading) {
        FullScreenLoading()
    } else {
        Column {
            Column(
                modifier = Modifier.padding(8.dp).fillMaxSize()
            ) {

                if (state.value.cashClosures.isEmpty()) {
                    EmptyScreen()
                }

                LazyColumn {
                    items(state.value.cashClosures) { cashClosure ->
                        CashClosureItem(cashClosure) { selectedCashClosure ->
                            println("SaleItem clicked: ${selectedCashClosure.id}")
                            viewModel.setSelectedCashClosure(selectedCashClosure)
                            scope.launch { scaffoldState.bottomSheetState.expand() }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CashClosureItem(sale: CashClosureRecordsResponse, onSaleClick: (CashClosureRecordsResponse) -> Unit = {}) {
    val paymentType = sale.cashClosures.joinToString(", ") { payment ->
        val typeLabel = when (payment.paymentType) {
            "cash" -> "Efectivo"
            "transfer" -> "Transferencia"
            else -> "Tarjeta"
        }

        "$typeLabel (${payment.total})"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = { onSaleClick(sale) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Total: ${sale.total}", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Numero de pagos: ${sale.totalPayments}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Tipo de pago: $paymentType",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Fecha de corte: ${sale.formattedDate}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun EmptyScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "No hay cortes de caja disponibles",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
            Text(
                text = "Realice un corte de caja para ver los registros aquí.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
        }
    }
}