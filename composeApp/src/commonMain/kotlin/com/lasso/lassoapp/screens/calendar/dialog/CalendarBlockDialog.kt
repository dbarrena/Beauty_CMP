package com.lasso.lassoapp.screens.calendar.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.model.CalendarBlockWriteRequest
import com.lasso.lassoapp.model.CalendarBlock
import com.lasso.lassoapp.model.Employee
import com.lasso.lassoapp.model.validationError
import com.lasso.lassoapp.screens.calendar.CalendarPickerDialog
import com.lasso.lassoapp.screens.utils.LassoDialogFrame
import com.lasso.lassoapp.screens.utils.DialogContentScaffold
import com.lasso.lassoapp.ui.theme.LassoTextMuted
import com.lasso.lassoapp.ui.theme.LassoTextPrimary
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
    val isEditing = block != null
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
    LassoDialogFrame(onDismiss = { if (!isBusy) onDismiss() }, shape = RoundedCornerShape(16.dp),
        color = Color.White, expanded = true) {
        DialogContentScaffold(
            onClose = onDismiss,
            closeEnabled = !isBusy,
            scroll = scroll,
            modifier = Modifier.fillMaxSize(),
            header = {
                Text("Bloqueo de calendario", modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                    textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = LassoTextPrimary)
            },
            actions = {
                Column {
                    AppointmentDialogActions(
                        isEditing = isEditing,
                        isValid = canSave,
                        isSaving = isSaving,
                        isDeleting = isDeleting,
                        onSave = { onSave(form.toRequest()) },
                        onDelete = onDelete,
                        deleteContentDescription = "Eliminar bloqueo",
                    )
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = onDismiss, enabled = !isBusy,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                        Text("Cancelar", color = LassoTextMuted, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    }
                }
            },
        ) {
                Spacer(Modifier.height(12.dp))
                AppointmentFieldLabel("Nombre del bloqueo (opcional)")
                OutlinedTextField(
                    value = form.title,
                    onValueChange = { form.title = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ej. Comida, descanso o reunión") },
                    enabled = !isBusy,
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                )
                AppointmentFormSpacer()
                AppointmentFieldLabel("Empleado")
                AppointmentPickerField(
                    value = selectedEmployee?.name ?: employeeName,
                    placeholder = if (isLoadingEmployees) "Cargando empleados…" else "Seleccionar empleado",
                    options = employees,
                    optionLabel = Employee::name,
                    enabled = !isBusy && !isLoadingEmployees,
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
                    enabled = !isBusy,
                    onStartSelected = form::selectStartMinutes,
                    onEndSelected = form::selectEndMinutes,
                    timeOptions = CALENDAR_BLOCK_TIME_OPTIONS,
                )
                AppointmentFormSpacer()
                AppointmentDateField(form.startDate, !isBusy, { pickingStartDate = true },
                    label = "Fecha inicio (opcional)", onClear = { form.selectStartDate(null) })
                AppointmentFormSpacer()
                AppointmentDateField(form.endDate, !isBusy, { pickingStartDate = false },
                    label = "Fecha fin (opcional)", onClear = { form.selectEndDate(null) })
                Spacer(Modifier.height(12.dp))
                Text("Sin fechas, el bloqueo se aplica todos los días.", style = MaterialTheme.typography.bodySmall, color = LassoTextMuted)
                (error ?: validationError)?.let {
                    Spacer(Modifier.height(12.dp))
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
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
