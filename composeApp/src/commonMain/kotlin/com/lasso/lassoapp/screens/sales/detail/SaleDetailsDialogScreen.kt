package com.lasso.lassoapp.screens.sales.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.lasso.lassoapp.model.PaymentApiResponse
import com.lasso.lassoapp.model.SaleApiResponse
import com.lasso.lassoapp.model.SaleDetailApiResponse
import com.lasso.lassoapp.screens.calendar.CalendarPickerDialog
import com.lasso.lassoapp.screens.sales.detail.edit_dialog.SaleDetailEditDialogScreen
import com.lasso.lassoapp.screens.utils.formatDdMmYyyy
import com.lasso.lassoapp.ui.theme.LassoPrimary
import com.lasso.lassoapp.ui.theme.LassoSecondary
import com.lasso.lassoapp.ui.theme.LassoSurfaceVariant
import com.lasso.lassoapp.ui.theme.LassoTertiary
import com.lasso.lassoapp.ui.theme.LassoTextMuted
import com.lasso.lassoapp.ui.theme.LassoTextPrimary
import com.lasso.lassoapp.utils.formatCurrency
import com.lasso.lassoapp.utils.parseCurrency
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SaleDetailsDialogScreen(
    sale: SaleApiResponse,
    onDismiss: (needsRefresh: Boolean) -> Unit,
) {
    val viewModel = koinViewModel<SaleDetailsDialogScreenViewModel>()
    val state by viewModel.state.collectAsState()
    var editingDetail by remember { mutableStateOf<SaleDetailApiResponse?>(null) }
    var detailPendingDelete by remember { mutableStateOf<SaleDetailApiResponse?>(null) }
    var confirmSaleDelete by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(sale.id) { viewModel.setSale(sale) }

    state.sale?.let { currentSale ->
        Dialog(
            onDismissRequest = { if (!state.isBusy) onDismiss(state.dismissShouldReload) },
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Detalles de venta",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = LassoTextPrimary,
                            ),
                            modifier = Modifier.padding(bottom = 24.dp),
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            ParentFields(state, onClientSelected = viewModel::selectClient, onEmployeeSelected = viewModel::selectEmployee, onDateClick = { showDatePicker = true })
                            if (state.isLoadingOptions) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    CircularProgressIndicator(Modifier.size(18.dp), color = LassoPrimary, strokeWidth = 2.dp)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Cargando datos de la venta…", color = LassoTextMuted, fontSize = 13.sp)
                                }
                            }
                            SaleDetailsSection(
                                details = currentSale.saleDetails,
                                enabled = !state.isBusy && !state.isLoadingOptions,
                                onEdit = { editingDetail = it },
                                onDelete = { detailPendingDelete = it },
                            )
                            PaymentsSection(currentSale.payments)
                            SaleTotalsCard(currentSale)
                            state.error?.let { error ->
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 13.sp,
                                )
                            }
                        }

                        Spacer(Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Button(
                                onClick = { viewModel.saveParent { onDismiss(true) } },
                                enabled = state.canSaveParent,
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = LassoPrimary),
                            ) {
                                if (state.isSavingParent) {
                                    CircularProgressIndicator(Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                                } else {
                                    Text("Guardar cambios", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                                }
                            }
                            OutlinedButton(
                                onClick = { confirmSaleDelete = true },
                                enabled = !state.isBusy,
                                modifier = Modifier.size(48.dp),
                                shape = CircleShape,
                                border = BorderStroke(1.dp, LassoTertiary),
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = LassoTertiary),
                            ) {
                                if (state.isDeletingSale) {
                                    CircularProgressIndicator(Modifier.size(22.dp), color = LassoTertiary, strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar venta", modifier = Modifier.size(24.dp))
                                }
                            }
                        }
                        TextButton(
                            onClick = { onDismiss(state.dismissShouldReload) },
                            enabled = !state.isBusy,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                        ) {
                            Text("Cancelar", color = LassoTextMuted, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        }
                    }

                    if (!state.isBusy) {
                        IconButton(
                            onClick = { onDismiss(state.dismissShouldReload) },
                            modifier = Modifier.align(Alignment.TopEnd).size(24.dp),
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = LassoTextMuted, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        state.draft?.let { draft ->
            CalendarPickerDialog(
                initialDate = draft.date,
                onDismiss = { showDatePicker = false },
                onConfirm = {
                    viewModel.selectDate(it)
                    showDatePicker = false
                },
            )
        }
    }

    editingDetail?.let { detail ->
        SaleDetailEditDialogScreen(
            selectedSaleDetail = detail,
            isLoading = state.isDetailOperationRunning,
            onConfirmEditChanges = { request ->
                viewModel.editSaleDetail(request) { editingDetail = null }
            },
            onDismiss = { editingDetail = null },
        )
    }

    detailPendingDelete?.let { detail ->
        DeleteConfirmationDialog(
            title = "Eliminar artículo",
            message = "¿Eliminar ${detail.displayName()} de esta venta?",
            enabled = !state.isDetailOperationRunning,
            onConfirm = {
                viewModel.deleteSaleDetail(detail) { detailPendingDelete = null }
            },
            onDismiss = { detailPendingDelete = null },
        )
    }

    if (confirmSaleDelete) {
        DeleteConfirmationDialog(
            title = "Eliminar venta",
            message = "¿Eliminar esta venta? Esta acción no se puede deshacer.",
            enabled = !state.isDeletingSale,
            onConfirm = { viewModel.deleteSale { onDismiss(true) } },
            onDismiss = { confirmSaleDelete = false },
        )
    }
}

@Composable
private fun ParentFields(
    state: SalesDetailScreenState,
    onClientSelected: (Int?) -> Unit,
    onEmployeeSelected: (Int) -> Unit,
    onDateClick: () -> Unit,
) {
    val draft = state.draft ?: return
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(Modifier.weight(1f)) {
            FieldLabel("Cliente")
            PickerField(
                value = draft.clientId?.let { id -> state.clients.firstOrNull { it.id == id }?.name ?: "Cliente #$id" } ?: "Sin cliente",
                options = listOf(null to "Sin cliente") + state.clients.map { it.id to it.name },
                enabled = !state.isBusy && !state.isLoadingOptions,
                onSelected = onClientSelected,
            )
        }
        Column(Modifier.weight(1f)) {
            FieldLabel("Empleado")
            PickerField(
                value = draft.employeeId?.let { id -> state.employees.firstOrNull { it.id == id }?.name } ?: "Seleccionar",
                options = state.employees.map { it.id to it.name },
                enabled = !state.isBusy && !state.isLoadingOptions,
                onSelected = { it?.let(onEmployeeSelected) },
            )
        }
    }
    Spacer(Modifier.height(12.dp))
    FieldLabel("Fecha")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(LassoSurfaceVariant, RoundedCornerShape(24.dp))
            .clickable(enabled = !state.isBusy && !state.isLoadingOptions, onClick = onDateClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(draft.date.formatDdMmYyyy(), color = LassoTextPrimary, fontSize = 16.sp)
        Icon(Icons.Default.CalendarMonth, contentDescription = "Seleccionar fecha", tint = LassoTextMuted, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun <T> PickerField(
    value: String,
    options: List<Pair<T?, String>>,
    enabled: Boolean,
    onSelected: (T?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(LassoSurfaceVariant, RoundedCornerShape(24.dp))
            .clickable(enabled = enabled && options.isNotEmpty()) { expanded = true }
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(value, color = LassoTextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = LassoTextMuted)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { (id, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onSelected(id)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun SaleDetailsSection(
    details: List<SaleDetailApiResponse>,
    enabled: Boolean,
    onEdit: (SaleDetailApiResponse) -> Unit,
    onDelete: (SaleDetailApiResponse) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FieldLabel("Artículos")
        details.forEach { detail ->
            Card(
                modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 64.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LassoSurfaceVariant),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(detail.displayName(), color = LassoTextPrimary, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("Cantidad: ${detail.quantity}", color = LassoTextMuted, fontSize = 13.sp)
                    }
                    Text(
                        (detail.price.parseCurrency() * detail.quantity).formatCurrency(includeSymbol = true),
                        color = LassoPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                    IconButton(onClick = { onEdit(detail) }, enabled = enabled) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar artículo", tint = LassoPrimary)
                    }
                    IconButton(onClick = { onDelete(detail) }, enabled = enabled) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar artículo", tint = LassoTertiary)
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentsSection(payments: List<PaymentApiResponse>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FieldLabel("Métodos de pago")
        payments.forEach { payment ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 56.dp)
                    .background(LassoSurfaceVariant, RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(payment.icon(), contentDescription = null, tint = LassoPrimary)
                Spacer(Modifier.width(10.dp))
                Text(payment.label(), color = LassoTextPrimary, modifier = Modifier.weight(1f))
                Text(payment.total.parseCurrency().formatCurrency(includeSymbol = true), color = LassoTextPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SaleTotalsCard(sale: SaleApiResponse) {
    val subtotal = sale.total.parseCurrency()
    val discount = sale.discountAmount?.parseCurrency() ?: 0.0
    val total = (subtotal - discount).coerceAtLeast(0.0)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = LassoSurfaceVariant),
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            TotalRow("Artículos", sale.saleDetails.size.toString())
            TotalRow("Subtotal", subtotal.formatCurrency(includeSymbol = true))
            if (discount > 0.0) TotalRow("Descuento", "-${discount.formatCurrency(includeSymbol = true)}", LassoSecondary)
            TotalRow("Total", total.formatCurrency(includeSymbol = true), LassoPrimary, FontWeight.Bold)
        }
    }
}

@Composable
private fun TotalRow(label: String, value: String, color: Color = LassoTextMuted, weight: FontWeight = FontWeight.Normal) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = color, fontWeight = weight)
        Text(value, color = color, fontWeight = weight)
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(text, color = LassoTextPrimary, fontSize = 15.sp, modifier = Modifier.padding(bottom = 6.dp))
}

@Composable
private fun DeleteConfirmationDialog(
    title: String,
    message: String,
    enabled: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { if (enabled) onDismiss() },
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = enabled) { Text("Eliminar", color = LassoTertiary) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = enabled) { Text("Cancelar") }
        },
    )
}

private fun SaleDetailApiResponse.displayName(): String = product?.name ?: service?.name ?: "Artículo"

private fun PaymentApiResponse.label(): String = when (paymentType) {
    "cash" -> "Efectivo"
    "card" -> "Tarjeta"
    "transfer" -> "Transferencia"
    "other" -> "Otro"
    "advance" -> "Anticipo"
    else -> paymentType
}

private fun PaymentApiResponse.icon() = when (paymentType) {
    "cash" -> Icons.Outlined.AttachMoney
    "transfer" -> Icons.Outlined.Smartphone
    "other" -> Icons.Outlined.MoreHoriz
    "advance" -> Icons.Outlined.Download
    else -> Icons.Outlined.CreditCard
}
