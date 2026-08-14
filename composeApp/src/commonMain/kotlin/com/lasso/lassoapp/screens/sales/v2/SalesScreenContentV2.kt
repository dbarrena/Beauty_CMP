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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
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

/**
 * Figma-aligned sales history scaffold: summary, payment breakdown, period chips, rich cards.
 */
@Composable
internal fun SalesScreenContentV2(
    state: SalesScreenStateV2,
    isAdmin: Boolean,
    onBack: () -> Unit,
    onReloadSales: () -> Unit,
    onClearSelectedSale: () -> Unit,
    onLoadForPeriod: (SalesPeriodFilter) -> Unit,
    onApplyCustomDateRange: (Long?, Long?) -> Unit,
    onSetSelectedSale: (SaleApiResponse) -> Unit,
    onDeleteSale: (Int) -> Unit,
) {
    var showCustomRangeDialog by remember { mutableStateOf(false) }
    var pendingDeleteSaleId by remember { mutableStateOf<Int?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
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
                            discounts = state.discounts,
                            transactionCount = state.sales.size,
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
                            periods = if (isAdmin) {
                                SalesPeriodFilter.entries
                            } else {
                                listOf(SalesPeriodFilter.Today)
                            },
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
                                isAdmin = isAdmin,
                                sale = sale,
                                onEdit = {
                                    onSetSelectedSale(sale)
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

    state.selectedSale?.let { sale ->
        SaleDetailsDialogScreen(sale) { shouldReload ->
            onClearSelectedSale()
            if (shouldReload) onReloadSales()
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
                    },
                ) { Text("Eliminar", color = LassoTertiary) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteSaleId = null }) { Text("Cancelar") }
            },
        )
    }
}
