package com.lasso.lassoapp.screens.calendar

import androidx.compose.runtime.Composable
import com.lasso.lassoapp.screens.calendar.dialog.AppointmentDialog
import com.lasso.lassoapp.screens.calendar.dialog.CalendarBlockDialog
import com.lasso.lassoapp.screens.clients.dialog.ClientDialog

/** Shared dialogs for both calendar shells. */
@Composable
internal fun CalendarDialogs(state: CalendarScreenState, viewModel: CalendarScreenViewModel, selectedEmployeeId: Int?) {
    if (state.isDateDialogDisplayed) {
        CalendarDateDialog(
            selectedDate = state.selectedDate,
            viewMode = state.viewMode,
            onDismiss = viewModel::hideDateDialog,
            onDateSelected = viewModel::onDateSelected,
            onViewModeSelected = viewModel::onViewModeSelected,
        )
    }


    if (state.isCalendarBlockDialogDisplayed) {
        CalendarBlockDialog(
            initialDate = state.selectedDate,
            initialEmployeeId = selectedEmployeeId,
            employees = state.calendarBlockEmployees,
            isLoadingEmployees = state.isLoadingCalendarBlockEmployees,
            isSaving = state.isSavingCalendarBlock,
            error = state.calendarBlockError,
            onDismiss = viewModel::hideCalendarBlockDialog,
            onRetryEmployees = viewModel::loadCalendarBlockEmployees,
            onSave = { request ->
                if (state.selectedCalendarBlock == null) viewModel.createCalendarBlock(request)
                else viewModel.editCalendarBlock(request)
            },
            block = state.selectedCalendarBlock,
            isDeleting = state.isDeletingCalendarBlock,
            onDelete = viewModel::deleteCalendarBlock,
            employeeName = state.employeeSchedules.firstOrNull { it.id == state.selectedCalendarBlock?.employeeId }?.name,
        )
    }

    if (state.isAppointmentDialogDisplayed) {
        AppointmentDialog(
            appointment = state.selectedAppointment,
            appointmentEmployeeId = state.selectedAppointmentEmployeeId,
            clients = state.clients,
            employees = state.employees,
            services = state.services,
            initialDate = state.initialAppointmentDate ?: state.selectedDate,
            initialStartMinutes = state.initialAppointmentStartMinutes,
            preferredClient = state.createdClient,
            isSaving = state.isSavingAppointment,
            isDeleting = state.isDeleting,
            optionsError = state.optionsError ?: state.mutationError,
            onDismiss = viewModel::hideAppointmentDialog,
            onNewClient = viewModel::showNewClientDialog,
            onEditClient = viewModel::showEditClientDialog,
            onResult = viewModel::saveAppointment,
            onDelete = {
                state.selectedAppointment?.let { viewModel.deleteAppointment(it.id) }
            },
        )
    }

    if (state.isNewClientDialogDisplayed) {
        ClientDialog(
            client = state.editingClient,
            isLoading = state.isSavingClient,
            error = state.clientSaveError,
            onDismiss = viewModel::hideNewClientDialog,
            onSave = viewModel::saveClient,
        )
    }
}
