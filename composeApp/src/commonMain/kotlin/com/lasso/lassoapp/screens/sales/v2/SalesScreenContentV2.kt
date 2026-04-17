package com.lasso.lassoapp.screens.sales.v2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.model.SaleApiResponse
import com.lasso.lassoapp.screens.sales.detail.SaleDetailsDialogScreen
import com.lasso.lassoapp.screens.sales.v2.custom_range.SalesCustomDateRangeDialog
import com.lasso.lassoapp.screens.sales.v2.empty_state.EmptySalesBody
import com.lasso.lassoapp.screens.sales.v2.payment_breakdown.SalesPaymentBreakdownRow
import com.lasso.lassoapp.screens.sales.v2.period_chips.SalesPeriodChipsRow
import com.lasso.lassoapp.screens.sales.v2.summary.SalesSummaryCard
import com.lasso.lassoapp.screens.sales.v2.title_row.SalesScreenTitleRow
import com.lasso.lassoapp.screens.sales.v2.transaction_card.SalesTransactionCard
import com.lasso.lassoapp.ui.theme.LassoTertiary
import kotlinx.coroutines.launch

/**
 * Figma-aligned sales history scaffold: summary, payment breakdown, period chips, rich cards.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SalesScreenContentV2(
    state: SalesScreenStateV2,
    onBack: () -> Unit,
    onReloadSales: () -> Unit,
    onClearSelectedSale: () -> Unit,
    onLoadForPeriod: (SalesPeriodFilter) -> Unit,
    onApplyCustomDateRange: (Long?, Long?) -> Unit,
    onSetSelectedSale: (SaleApiResponse) -> Unit,
    onDeleteSale: (Int) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false,
        ),
    )

    var showCustomRangeDialog by remember { mutableStateOf(false) }
    var pendingDeleteSaleId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(state.selectedSale) {
        if (state.selectedSale == null) {
            scaffoldState.bottomSheetState.hide()
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetSwipeEnabled = false,
        sheetDragHandle = null,
        sheetTonalElevation = 8.dp,
        sheetShadowElevation = 16.dp,
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 0.dp),
            ) {
                state.selectedSale?.let { sale ->
                    SaleDetailsDialogScreen(sale) { shouldReload ->
                        scope.launch {
                            if (shouldReload) {
                                onReloadSales()
                            }
                            scaffoldState.bottomSheetState.hide()
                            onClearSelectedSale()
                        }
                    }
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (state.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                } else {
                    Spacer(Modifier.height(4.dp))
                }
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        SalesScreenTitleRow(
                            onBack = onBack,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        )
                    }
                    item {
                        SalesSummaryCard(
                            total = state.total,
                            transactionCount = state.sales.size,
                            isLoading = state.isLoading,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }
                    item {
                        SalesPaymentBreakdownRow(
                            sales = state.sales,
                            isLoading = state.isLoading,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }
                    item {
                        SalesPeriodChipsRow(
                            selected = state.periodFilter,
                            onSelect = { period ->
                                when (period) {
                                    SalesPeriodFilter.Custom -> showCustomRangeDialog = true
                                    else -> onLoadForPeriod(period)
                                }
                            },
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }
                    if (state.sales.isEmpty()) {
                        item {
                            EmptySalesBody(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                            )
                        }
                    } else {
                        items(state.sales, key = { it.id }) { sale ->
                            SalesTransactionCard(
                                sale = sale,
                                onEdit = {
                                    onSetSelectedSale(sale)
                                    scope.launch { scaffoldState.bottomSheetState.expand() }
                                },
                                onDelete = { pendingDeleteSaleId = sale.id },
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )
                        }
                    }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }

    if (showCustomRangeDialog) {
        SalesCustomDateRangeDialog(
            onDismiss = { showCustomRangeDialog = false },
            onConfirm = { start, end ->
                showCustomRangeDialog = false
                onApplyCustomDateRange(start, end)
            },
        )
    }

    pendingDeleteSaleId?.let { saleId ->
        AlertDialog(
            onDismissRequest = { pendingDeleteSaleId = null },
            title = { Text("Eliminar venta") },
            text = { Text("¿Eliminar esta venta? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteSale(saleId)
                        pendingDeleteSaleId = null
                        scope.launch { scaffoldState.bottomSheetState.hide() }
                    },
                ) { Text("Eliminar", color = LassoTertiary) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteSaleId = null }) { Text("Cancelar") }
            },
        )
    }
}
