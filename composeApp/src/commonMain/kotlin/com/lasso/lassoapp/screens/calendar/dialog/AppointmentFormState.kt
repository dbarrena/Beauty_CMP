package com.lasso.lassoapp.screens.calendar.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.lasso.lassoapp.model.AppointmentWriteRequest
import com.lasso.lassoapp.model.CalendarAppointment
import com.lasso.lassoapp.model.Client
import com.lasso.lassoapp.model.Employee
import com.lasso.lassoapp.model.Service
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@Stable
internal class AppointmentFormState(
    initialClient: Client?,
    initialEmployee: Employee?,
    initialService: Service?,
    initialDate: LocalDate,
    initialStartMinutes: Int,
    initialEndMinutes: Int,
    initialNotes: String,
) {
    var selectedClient by mutableStateOf(initialClient)
        private set
    var selectedEmployee by mutableStateOf(initialEmployee)
        private set
    var selectedService by mutableStateOf(initialService)
        private set
    var selectedDate by mutableStateOf(initialDate)
        private set
    var startMinutes by mutableStateOf(initialStartMinutes)
        private set
    var endMinutes by mutableStateOf(initialEndMinutes)
        private set
    var notes by mutableStateOf(initialNotes)
        private set

    val isValid: Boolean
        get() = selectedEmployee != null && endMinutes > startMinutes

    fun selectClient(client: Client) {
        selectedClient = client
    }

    fun selectEmployee(employee: Employee) {
        selectedEmployee = employee
    }

    fun selectService(service: Service) {
        selectedService = service
    }

    fun selectDate(date: LocalDate) {
        selectedDate = date
    }

    fun selectStartMinutes(minutes: Int) {
        startMinutes = minutes
        if (endMinutes <= minutes) {
            endMinutes = (minutes + DEFAULT_APPOINTMENT_DURATION_MINUTES)
                .coerceAtMost(LAST_TIME_OF_DAY_MINUTES)
        }
    }

    fun selectEndMinutes(minutes: Int) {
        endMinutes = minutes
    }

    fun updateNotes(value: String) {
        notes = value
    }

    fun useClientIfMissing(client: Client?) {
        if (client != null && selectedClient == null) selectedClient = client
    }

    fun useEmployeeIfMissing(employee: Employee?) {
        if (employee != null && selectedEmployee == null) selectedEmployee = employee
    }

    fun useServiceIfMissing(service: Service?) {
        if (service != null && selectedService == null) selectedService = service
    }

    fun toRequest(): AppointmentWriteRequest = AppointmentWriteRequest(
        employeeId = requireNotNull(selectedEmployee).id,
        clientId = selectedClient?.id,
        serviceName = selectedService?.name,
        startsAt = selectedDate.atMinutes(startMinutes),
        endsAt = selectedDate.atMinutes(endMinutes),
        notes = notes.trim().ifBlank { null },
    )
}

@Composable
internal fun rememberAppointmentFormState(
    appointment: CalendarAppointment?,
    appointmentEmployeeId: Int?,
    clients: List<Client>,
    employees: List<Employee>,
    services: List<Service>,
    initialDate: LocalDate,
    initialStartMinutes: Int?,
    preferredClient: Client?,
): AppointmentFormState {
    val appointmentStart = appointment?.startsAt?.toLocalDateTime()
    val appointmentEnd = appointment?.endsAt?.toLocalDateTime()
    val defaultStart = initialStartMinutes ?: DEFAULT_START_MINUTES

    val state = remember(appointment?.id, initialDate, initialStartMinutes) {
        AppointmentFormState(
            initialClient = clients.firstOrNull { it.id == appointment?.client?.id },
            initialEmployee = employees.firstOrNull { it.id == appointmentEmployeeId },
            initialService = services.firstOrNull { it.name == appointment?.serviceName },
            initialDate = appointmentStart?.date ?: initialDate,
            initialStartMinutes = appointmentStart?.minutesOfDay ?: defaultStart,
            initialEndMinutes = appointmentEnd?.minutesOfDay
                ?: (defaultStart + DEFAULT_APPOINTMENT_DURATION_MINUTES),
            initialNotes = appointment?.notes.orEmpty(),
        )
    }

    LaunchedEffect(preferredClient?.id) {
        if (preferredClient != null) state.selectClient(preferredClient)
    }
    LaunchedEffect(appointment?.id, clients) {
        state.useClientIfMissing(clients.firstOrNull { it.id == appointment?.client?.id })
    }
    LaunchedEffect(appointmentEmployeeId, employees) {
        state.useEmployeeIfMissing(employees.firstOrNull { it.id == appointmentEmployeeId })
    }
    LaunchedEffect(appointment?.id, services) {
        state.useServiceIfMissing(services.firstOrNull { it.name == appointment?.serviceName })
    }

    return state
}

private const val DEFAULT_START_MINUTES = 10 * 60
private const val DEFAULT_APPOINTMENT_DURATION_MINUTES = 60
private const val LAST_TIME_OF_DAY_MINUTES = 23 * 60 + 30

private fun LocalDate.atMinutes(minutes: Int): Long = LocalDateTime(
    year = year,
    month = month,
    day = day,
    hour = minutes / 60,
    minute = minutes % 60,
).toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()

private fun Long.toLocalDateTime(): LocalDateTime =
    Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.currentSystemDefault())

private val LocalDateTime.minutesOfDay: Int
    get() = hour * 60 + minute
