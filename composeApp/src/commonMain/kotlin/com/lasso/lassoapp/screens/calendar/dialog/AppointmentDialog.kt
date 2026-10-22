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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.model.AppointmentWriteRequest
import com.lasso.lassoapp.model.CalendarAppointment
import com.lasso.lassoapp.model.Client
import com.lasso.lassoapp.model.Employee
import com.lasso.lassoapp.model.Service
import com.lasso.lassoapp.screens.calendar.CalendarPickerDialog
import com.lasso.lassoapp.screens.clients.ClientSelectorField
import com.lasso.lassoapp.screens.clients.search_client_dialog.SearchClientDialog
import com.lasso.lassoapp.screens.product_catalog.dialog.search_service.SearchServiceDialog
import com.lasso.lassoapp.screens.utils.DialogContentScaffold
import com.lasso.lassoapp.screens.utils.LassoDialogFrame
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
    val formState =
        rememberAppointmentFormState(
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
    var showClientSearch by remember { mutableStateOf(false) }
    var showServiceSearch by remember { mutableStateOf(false) }

    LaunchedEffect(optionsError) {
        if (optionsError != null) formScrollState.animateScrollTo(formScrollState.maxValue)
    }

    LassoDialogFrame(
        onDismiss = { if (!isBusy) onDismiss() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        expanded = true,
    ) {
        DialogContentScaffold(
            onClose = onDismiss,
            closeEnabled = !isBusy,
            scroll = formScrollState,
            modifier = Modifier.fillMaxSize(),
            header = { AppointmentDialogTitle(isEditing = appointment != null) },
            actions = {
                Column {
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
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                    ) {
                        Text(
                            "Cancelar",
                            color = LassoTextMuted,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                        )
                    }
                }
            },
        ) {
            ClientSelectorField(
                selectedClient = formState.selectedClient,
                enabled = !isBusy,
                onSearchClick = { showClientSearch = true },
                onClientRemoved = formState::clearClient,
                onNewClient = onNewClient,
                showWhatsAppAction = appointment != null,
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
            AppointmentServiceField(
                selectedService = formState.selectedService,
                enabled = !isBusy,
                onSearchClick = { showServiceSearch = true },
                onServiceRemoved = formState::clearService,
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

    if (showClientSearch) {
        SearchClientDialog(
            clients = clients,
            onDismiss = { showClientSearch = false },
            onClientSelected = formState::selectClient,
            onNewClient = onNewClient,
        )
    }

    if (showServiceSearch) {
        SearchServiceDialog(
            services = services,
            onDismiss = { showServiceSearch = false },
            onServiceSelected = formState::selectService,
        )
    }
}

@Composable
private fun AppointmentDialogTitle(isEditing: Boolean) {
    Text(
        text = if (isEditing) "Editar cita" else "Nueva cita",
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        style =
            MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = LassoTextPrimary,
            ),
        modifier = Modifier.fillMaxWidth(),
    )
}
