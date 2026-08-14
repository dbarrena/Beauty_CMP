package com.lasso.lassoapp.screens.calendar.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lasso.lassoapp.model.AppointmentWriteRequest
import com.lasso.lassoapp.model.CalendarAppointment
import com.lasso.lassoapp.model.Client
import com.lasso.lassoapp.model.Employee
import com.lasso.lassoapp.model.Service
import com.lasso.lassoapp.screens.calendar.CalendarPickerDialog
import com.lasso.lassoapp.ui.theme.LassoTextMuted
import com.lasso.lassoapp.ui.theme.LassoTextPrimary
import kotlinx.datetime.LocalDate

@Composable
fun AppointmentDialog(
    appointment: CalendarAppointment?,
    appointmentEmployeeId: Int?,
    clients: List<Client>,
    employees: List<Employee>,
    services: List<Service>,
    initialDate: LocalDate,
    initialStartMinutes: Int? = null,
    preferredClient: Client? = null,
    isSaving: Boolean,
    isDeleting: Boolean,
    optionsError: String? = null,
    onDismiss: () -> Unit,
    onNewClient: () -> Unit,
    onResult: (AppointmentWriteRequest) -> Unit,
    onDelete: () -> Unit,
) {
    val formState = rememberAppointmentFormState(
        appointment = appointment,
        appointmentEmployeeId = appointmentEmployeeId,
        clients = clients,
        employees = employees,
        services = services,
        initialDate = initialDate,
        initialStartMinutes = initialStartMinutes,
        preferredClient = preferredClient,
    )
    val formScrollState = rememberScrollState()
    val isBusy = isSaving || isDeleting
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(optionsError) {
        if (optionsError != null) formScrollState.animateScrollTo(formScrollState.maxValue)
    }

    Dialog(onDismissRequest = { if (!isBusy) onDismiss() }) {
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
                    AppointmentDialogTitle(isEditing = appointment != null)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(formScrollState),
                    ) {
                        AppointmentClientField(
                            selectedClient = formState.selectedClient,
                            clients = clients,
                            enabled = !isBusy,
                            onClientSelected = formState::selectClient,
                            onNewClient = onNewClient,
                        )

                        AppointmentFormSpacer()
                        AppointmentFieldLabel("Empleado")
                        AppointmentPickerField(
                            value = formState.selectedEmployee?.name,
                            placeholder = "Empleado",
                            options = employees,
                            optionLabel = Employee::name,
                            enabled = !isBusy,
                            onSelected = formState::selectEmployee,
                        )

                        AppointmentFormSpacer()
                        AppointmentFieldLabel("Servicio")
                        AppointmentPickerField(
                            value = formState.selectedService?.name,
                            placeholder = "Servicio",
                            options = services,
                            optionLabel = Service::name,
                            enabled = !isBusy,
                            onSelected = formState::selectService,
                        )

                        AppointmentFormSpacer()
                        AppointmentDateField(
                            date = formState.selectedDate,
                            enabled = !isBusy,
                            onClick = { showDatePicker = true },
                        )

                        AppointmentFormSpacer()
                        AppointmentTimeRangeField(
                            startMinutes = formState.startMinutes,
                            endMinutes = formState.endMinutes,
                            enabled = !isBusy,
                            onStartSelected = formState::selectStartMinutes,
                            onEndSelected = formState::selectEndMinutes,
                        )

                        AppointmentFormSpacer()
                        AppointmentNotesField(
                            notes = formState.notes,
                            enabled = !isBusy,
                            onNotesChanged = formState::updateNotes,
                        )

                        optionsError?.let {
                            Spacer(Modifier.height(8.dp))
                            Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    AppointmentDialogActions(
                        isEditing = appointment != null,
                        isValid = formState.isValid,
                        isSaving = isSaving,
                        isDeleting = isDeleting,
                        onSave = { onResult(formState.toRequest()) },
                        onDelete = onDelete,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = onDismiss,
                        enabled = !isBusy,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                    ) {
                        Text(
                            "Cancelar",
                            color = LassoTextMuted,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                        )
                    }
                }

                if (!isBusy) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.TopEnd).size(24.dp),
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = LassoTextMuted.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        CalendarPickerDialog(
            initialDate = formState.selectedDate,
            onDismiss = { showDatePicker = false },
            onConfirm = {
                formState.selectDate(it)
                showDatePicker = false
            },
        )
    }
}

@Composable
private fun AppointmentDialogTitle(isEditing: Boolean) {
    Text(
        text = if (isEditing) "Editar cita" else "Nueva cita",
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = LassoTextPrimary,
        ),
        modifier = Modifier.padding(bottom = 24.dp),
    )
}
