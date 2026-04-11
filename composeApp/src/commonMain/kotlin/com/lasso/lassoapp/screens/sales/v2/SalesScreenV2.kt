package com.lasso.lassoapp.screens.sales.v2

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.model.PaymentApiResponse
import com.lasso.lassoapp.model.SaleApiResponse
import com.lasso.lassoapp.model.SaleDetailApiResponse
import com.lasso.lassoapp.screens.sales.detail.SaleDetailsDialogScreen
import com.lasso.lassoapp.screens.utils.FullScreenLoading
import com.lasso.lassoapp.screens.utils.toSaleCardDateTimeString
import com.lasso.lassoapp.ui.theme.LassoTextPlaceholder
import com.lasso.lassoapp.ui.theme.LassoTertiary
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToLong
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Figma-aligned sales history UI (summary, payment breakdown, period chips, rich cards).
 * Legacy fallback: `com.lasso.lassoapp.screens.sales.SalesScreen` + `SalesScreenViewModel`.
 */
@Composable
fun SalesScreenV2(onBack: () -> Unit = {}) {
    val viewModel = koinViewModel<SalesScreenViewModelV2>()
    SalesScreenContentV2(viewModel = viewModel, onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SalesScreenContentV2(
    viewModel: SalesScreenViewModelV2,
    onBack: () -> Unit,
) {
    val state = viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false,
        ),
    )

    var showCustomRangeDialog by remember { mutableStateOf(false) }
    var pendingDeleteSaleId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(state.value.selectedSale) {
        if (state.value.selectedSale == null) {
            scaffoldState.bottomSheetState.hide()
        }
    }

    if (state.value.isLoading && state.value.sales.isEmpty()) {
        FullScreenLoading()
        return
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
                state.value.selectedSale?.let { sale ->
                    SaleDetailsDialogScreen(sale) { shouldReload ->
                        scope.launch {
                            if (shouldReload) {
                                viewModel.reloadSales()
                            }
                            scaffoldState.bottomSheetState.hide()
                            viewModel.clearSelectedSale()
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
                if (state.value.isLoading && state.value.sales.isNotEmpty()) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
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
                            total = state.value.total,
                            transactionCount = state.value.sales.size,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }
                    item {
                        SalesPaymentBreakdownRow(
                            sales = state.value.sales,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }
                    item {
                        SalesPeriodChipsRow(
                            selected = state.value.periodFilter,
                            onSelect = { period ->
                                when (period) {
                                    SalesPeriodFilter.Custom -> showCustomRangeDialog = true
                                    else -> viewModel.loadForPeriod(period)
                                }
                            },
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }
                    if (state.value.sales.isEmpty()) {
                        item {
                            EmptySalesBody(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                            )
                        }
                    } else {
                        items(state.value.sales, key = { it.id }) { sale ->
                            SalesTransactionCard(
                                sale = sale,
                                onEdit = {
                                    viewModel.setSelectedSale(sale)
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
                viewModel.applyCustomDateRange(start, end)
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
                        viewModel.deleteSale(saleId)
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

@Composable
private fun SalesScreenTitleRow(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Ventas",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "Historial y gestión de ventas",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SalesSummaryCard(
    total: Double,
    transactionCount: Int,
    modifier: Modifier = Modifier,
) {
    val primary = MaterialTheme.colorScheme.primary
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            primary.copy(alpha = 0.12f),
                            primary.copy(alpha = 0.05f),
                        ),
                    ),
                )
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column {
                    Text(
                        text = "Total de ventas",
                        style = MaterialTheme.typography.bodySmall,
                        color = LassoTextPlaceholder,
                    )
                    Text(
                        text = formatMoney(total),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = primary,
                        ),
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Transacciones",
                        style = MaterialTheme.typography.bodySmall,
                        color = LassoTextPlaceholder,
                    )
                    Text(
                        text = transactionCount.toString(),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

private data class PaymentBreakdown(val cash: Double, val card: Double, val transfer: Double)

private fun List<SaleApiResponse>.paymentBreakdown(): PaymentBreakdown {
    var cash = 0.0
    var card = 0.0
    var transfer = 0.0
    for (sale in this) {
        for (p in sale.payments) {
            val v = parsePaymentAmount(p)
            when (p.paymentType) {
                "cash" -> cash += v
                "transfer" -> transfer += v
                else -> card += v
            }
        }
    }
    fun r(x: Double) = (x * 100).roundToLong() / 100.0
    return PaymentBreakdown(r(cash), r(card), r(transfer))
}

private fun parsePaymentAmount(p: PaymentApiResponse): Double =
    p.total.replace("$", "").replace(",", "").toDoubleOrNull() ?: 0.0

@Composable
private fun SalesPaymentBreakdownRow(
    sales: List<SaleApiResponse>,
    modifier: Modifier = Modifier,
) {
    val b = sales.paymentBreakdown()
    Column(modifier = modifier) {
        Text(
            text = "Por método de pago",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(8.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                PaymentMiniCard(
                    label = "Efectivo",
                    amount = b.cash,
                    icon = { Icon(Icons.Outlined.AttachMoney, null, tint = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.width(128.dp),
                )
            }
            item {
                PaymentMiniCard(
                    label = "Tarjeta",
                    amount = b.card,
                    icon = { Icon(Icons.Outlined.CreditCard, null, tint = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.width(128.dp),
                )
            }
            item {
                PaymentMiniCard(
                    label = "Transferencia",
                    amount = b.transfer,
                    icon = { Icon(Icons.Outlined.Smartphone, null, tint = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.width(128.dp),
                )
            }
        }
    }
}

@Composable
private fun PaymentMiniCard(
    label: String,
    amount: Double,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.height(90.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
        ) {
            Box(Modifier.size(24.dp)) { icon() }
            Spacer(Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = LassoTextPlaceholder,
            )
            Text(
                text = formatMoney(amount),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SalesPeriodChipsRow(
    selected: SalesPeriodFilter,
    onSelect: (SalesPeriodFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    val chips = listOf(
        SalesPeriodFilter.Today to "Hoy",
        SalesPeriodFilter.ThisWeek to "Esta semana",
        SalesPeriodFilter.ThisMonth to "Este mes",
        SalesPeriodFilter.Custom to "Personalizado",
    )
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(chips) { (filter, label) ->
            val isSelected = selected == filter
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(filter) },
                label = { Text(label, maxLines = 1) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = MaterialTheme.colorScheme.onSurface,
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        }
    }
}

@Composable
private fun SalesTransactionCard(
    sale: SaleApiResponse,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.AccessTime,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = LassoTextPlaceholder,
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = sale.createdAt.toSaleCardDateTimeString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = LassoTextPlaceholder,
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    sale.clientId?.let { id ->
                        Text(
                            text = "Cliente #$id",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(Modifier.height(4.dp))
                    }
                    sale.saleDetails.forEach { detail ->
                        val line = detailLineLabel(detail)
                        if (line.isNotBlank()) {
                            Text(
                                text = "• $line",
                                style = MaterialTheme.typography.bodySmall,
                                color = LassoTextPlaceholder,
                            )
                        }
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = sale.total,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        paymentIconForSale(sale)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = sale.paymentSummaryLabel(),
                            style = MaterialTheme.typography.labelSmall,
                            color = LassoTextPlaceholder,
                            textAlign = TextAlign.End,
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedActionButton(
                    text = "Editar",
                    icon = { Icon(Icons.Outlined.Edit, null, Modifier.size(16.dp)) },
                    borderColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.primary,
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                )
                OutlinedActionButton(
                    text = "Eliminar",
                    icon = { Icon(Icons.Outlined.Delete, null, Modifier.size(16.dp)) },
                    borderColor = LassoTertiary,
                    contentColor = LassoTertiary,
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun OutlinedActionButton(
    text: String,
    icon: @Composable () -> Unit,
    borderColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
    ) {
        icon()
        Spacer(Modifier.width(6.dp))
        Text(text, color = contentColor, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun paymentIconForSale(sale: SaleApiResponse) {
    val primaryType = sale.payments.firstOrNull()?.paymentType ?: ""
    val icon = when (primaryType) {
        "cash" -> Icons.Outlined.AttachMoney
        "transfer" -> Icons.Outlined.Smartphone
        else -> Icons.Outlined.CreditCard
    }
    Icon(icon, null, Modifier.size(16.dp), tint = LassoTextPlaceholder)
}

private fun SaleApiResponse.paymentSummaryLabel(): String =
    payments.joinToString(" + ") { p ->
        when (p.paymentType) {
            "cash" -> "Efectivo"
            "transfer" -> "Transferencia"
            else -> "Tarjeta"
        }
    }

private fun detailLineLabel(detail: SaleDetailApiResponse): String {
    detail.product?.name?.let { return it }
    detail.service?.name?.let { return it }
    return ""
}

@Composable
private fun EmptySalesBody(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "No hay ventas en este periodo",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "Prueba otro rango o registra una venta desde Punto de Venta",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
private fun SalesCustomDateRangeDialog(
    onDismiss: () -> Unit,
    onConfirm: (Long?, Long?) -> Unit,
) {
    val timeZone = TimeZone.currentSystemDefault()
    val today = Clock.System.now().toLocalDateTime(timeZone).date
    val firstDay = LocalDate(today.year, today.monthNumber, 1)
    val lastDay = firstDay.plus(DatePeriod(months = 1)).minus(DatePeriod(days = 1))
    val firstDayMillis = firstDay.atStartOfDayIn(timeZone).toEpochMilliseconds()
    val lastDayMillis = lastDay.atStartOfDayIn(timeZone).toEpochMilliseconds()

    val state = rememberDateRangePickerState(
        initialSelectedStartDateMillis = firstDayMillis,
        initialSelectedEndDateMillis = lastDayMillis,
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(state.selectedStartDateMillis, state.selectedEndDateMillis)
                },
            ) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
        colors = DatePickerDefaults.colors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
        ),
    ) {
        DateRangePicker(
            state = state,
            title = {},
            dateFormatter = DatePickerDefaults.dateFormatter(),
            showModeToggle = false,
            colors = DatePickerDefaults.colors(
                containerColor = Color.White,
                selectedDayContentColor = Color.White,
                todayDateBorderColor = Color.Gray,
                todayContentColor = Color.Black,
                dayInSelectionRangeContentColor = Color.Black,
            ),
        )
    }
}

private fun formatMoney(amount: Double): String {
    val cents = kotlin.math.round(amount * 100).toLong()
    val neg = cents < 0
    val abs = if (cents < 0) -cents else cents
    val d = abs / 100
    val c = (abs % 100).toInt()
    val s = "$$d.${c.toString().padStart(2, '0')}"
    return if (neg) "-$s" else s
}
