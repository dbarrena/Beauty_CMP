package com.lasso.lassoapp.screens.calendar.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.model.CalendarBlockWriteRequest
import com.lasso.lassoapp.model.CalendarBlock
import com.lasso.lassoapp.model.Employee
import com.lasso.lassoapp.model.validationError
import com.lasso.lassoapp.screens.calendar.CalendarPickerDialog
import com.lasso.lassoapp.screens.utils.LassoDialogFrame
import com.lasso.lassoapp.ui.theme.LassoPrimary
import com.lasso.lassoapp.ui.theme.LassoTextMuted
import com.lasso.lassoapp.ui.theme.LassoTextPrimary
import com.lasso.lassoapp.ui.theme.LassoError
import kotlinx.datetime.LocalDate

@Composable
internal fun CalendarBlockDialog(
    initialDate: LocalDate,
    initialEmployeeId: Int?,
    employees: List<Employee>,
    isLoadingEmployees: Boolean,
    isSaving: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onRetryEmployees: () -> Unit,
    onSave: (CalendarBlockWriteRequest) -> Unit,
    block: CalendarBlock? = null,
    isDeleting: Boolean = false,
    onDelete: () -> Unit = {},
    employeeName: String? = null,
) {
    val form = remember(block?.id) { CalendarBlockFormState(initialDate, initialEmployeeId, block) }
    val isReadOnly = block != null
    val isBusy = isSaving || isDeleting
    var pickingStartDate by remember { mutableStateOf<Boolean?>(null) }
    val scroll = rememberScrollState()
    val selectedEmployee = employees.firstOrNull { it.id == form.employeeId }
    val request = form.toRequest()
    val validationError = if (selectedEmployee != null) request.validationError() else null
    val canSave = selectedEmployee != null && validationError == null && !isLoadingEmployees && !isBusy
    LaunchedEffect(employees, initialEmployeeId) {
        if (form.employeeId == null) {
            form.employeeId = employees.firstOrNull { it.id == initialEmployeeId }?.id ?: employees.singleOrNull()?.id
        }
    }
    LaunchedEffect(error) {
        if (error != null) scroll.animateScrollTo(scroll.maxValue)
    }
    LassoDialogFrame(onDismiss = { if (!isBusy) onDismiss() }, expanded = !isReadOnly) {
        Column(if (isReadOnly) Modifier.fillMaxWidth() else Modifier.fillMaxSize()) {
            Box(Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp)) {
                Text("Bloqueo de calendario", modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp).align(Alignment.Center),
                    style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold, color = LassoTextPrimary)
                IconButton(onClick = onDismiss, enabled = !isBusy, modifier = Modifier.align(Alignment.TopEnd)) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = LassoTextMuted)
                }
            }
            Column(Modifier.weight(1f, fill = !isReadOnly).verticalScroll(scroll)
                .padding(horizontal = 24.dp).padding(top = 24.dp, bottom = 16.dp)) {
                AppointmentFieldLabel("Empleado")
                AppointmentPickerField(
                    value = selectedEmployee?.name ?: if (isReadOnly) employeeName else null,
                    placeholder = if (isLoadingEmployees) "Cargando empleados…" else "Seleccionar empleado",
                    options = employees,
                    optionLabel = Employee::name,
                    enabled = !isReadOnly && !isBusy && !isLoadingEmployees,
                    onSelected = { form.employeeId = it.id },
                )
                if (!isLoadingEmployees && employees.isEmpty()) {
                    Text("No hay empleados disponibles.", style = MaterialTheme.typography.bodySmall, color = LassoTextMuted)
                    TextButton(onClick = onRetryEmployees, enabled = !isBusy) { Text("Reintentar") }
                }
                AppointmentFormSpacer()
                AppointmentTimeRangeField(
                    startMinutes = form.startMinutes,
                    endMinutes = form.endMinutes,
                    enabled = !isReadOnly && !isBusy,
                    onStartSelected = form::selectStartMinutes,
                    onEndSelected = form::selectEndMinutes,
                    timeOptions = CALENDAR_BLOCK_TIME_OPTIONS,
                )
                if (!isReadOnly || form.startDate != null) {
                    AppointmentFormSpacer()
                    AppointmentDateField(form.startDate, !isReadOnly && !isBusy, { pickingStartDate = true },
                        label = "Fecha inicio (opcional)", onClear = { form.selectStartDate(null) })
                }
                if (!isReadOnly || form.endDate != null) {
                    AppointmentFormSpacer()
                    AppointmentDateField(form.endDate, !isReadOnly && !isBusy, { pickingStartDate = false },
                        label = "Fecha fin (opcional)", onClear = { form.selectEndDate(null) })
                }
                if (!isReadOnly) {
                    Spacer(Modifier.height(12.dp))
                    Text("Sin fechas, el bloqueo se aplica todos los días.", style = MaterialTheme.typography.bodySmall, color = LassoTextMuted)
                }
                (error ?: if (!isReadOnly) validationError else null)?.let {
                    Spacer(Modifier.height(12.dp))
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
                Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(top = 12.dp, bottom = 24.dp)) {
                    Button(
                        onClick = { if (isReadOnly) onDelete() else onSave(form.toRequest()) },
                        enabled = if (isReadOnly) !isBusy else canSave,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (isReadOnly) LassoError else LassoPrimary, contentColor = Color.White),
                    ) {
                        if (isBusy) CircularProgressIndicator(Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                        else Text(if (isReadOnly) "Eliminar bloqueo" else "Agregar bloqueo", fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = onDismiss, enabled = !isBusy,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                        Text("Cancelar", color = LassoTextMuted)
                    }
                }
        }
    }
    pickingStartDate?.let { start ->
        CalendarPickerDialog(
            initialDate = (if (start) form.startDate else form.endDate) ?: initialDate,
            onDismiss = { pickingStartDate = null },
            onConfirm = {
                if (start) form.selectStartDate(it) else form.selectEndDate(it)
                pickingStartDate = null
            },
        )
    }
}

private val CALENDAR_BLOCK_TIME_OPTIONS = (0 until 24 * 60 step 15).toList()
