package com.lasso.lassoapp.screens.commissions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.model.Employee
import com.lasso.lassoapp.screens.commissions.components.CommissionItemCard
import com.lasso.lassoapp.screens.commissions.components.CommissionsSummaryCard
import com.lasso.lassoapp.screens.commissions.components.CommissionsTitleRow
import com.lasso.lassoapp.screens.commissions.dialog.EmployeePickerDialog
import com.lasso.lassoapp.screens.sales.v2.SalesPeriodFilter
import com.lasso.lassoapp.screens.sales.v2.custom_range.SalesCustomDateRangeDialog
import com.lasso.lassoapp.screens.sales.v2.period_chips.SalesPeriodChipsRow

@Composable
fun CommissionsContent(
    state: CommissionsState,
    onSelectEmployee: (Employee) -> Unit,
    onLoadForPeriod: (SalesPeriodFilter) -> Unit,
    onApplyCustomDateRange: (Long?, Long?) -> Unit,
) {
    var showEmployeePicker by remember { mutableStateOf(false) }
    var showCustomRangeDialog by remember { mutableStateOf(false) }

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
                    CommissionsTitleRow(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }

                item {
                    EmployeeSelector(
                        selectedEmployee = state.selectedEmployee,
                        onClick = { showEmployeePicker = true },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                item {
                    CommissionsSummaryCard(
                        totalCommission = state.totalCommission,
                        serviceCommission = state.serviceCommission,
                        productCommission = state.productCommission,
                        transactionCount = state.transactionCount,
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

                state.commissions.forEach { response ->
                    items(response.services) { service ->
                        CommissionItemCard(
                            name = service.name,
                            quantity = service.quantity,
                            totalSales = service.totalSales,
                            commission = service.commission,
                            isService = true,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    items(response.products) { product ->
                        CommissionItemCard(
                            name = product.name,
                            quantity = product.quantity,
                            totalSales = product.totalSales,
                            commission = product.commission,
                            isService = false,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }

    EmployeePickerDialog(
        isVisible = showEmployeePicker,
        employees = state.employees,
        isLoading = state.isLoading,
        onDismiss = { showEmployeePicker = false }) {
        onSelectEmployee(it)
        showEmployeePicker = false
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
}

@Composable
private fun EmployeeSelector(
    selectedEmployee: Employee?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = Color.White,
        tonalElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Empleado",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = selectedEmployee?.name ?: "Seleccionar empleado",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
