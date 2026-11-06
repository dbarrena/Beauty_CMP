package com.lasso.lassoapp.screens.calendar

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.model.AppointmentCalendarResponse
import com.lasso.lassoapp.model.AppointmentWriteRequest
import com.lasso.lassoapp.model.CalendarAppointment
import com.lasso.lassoapp.model.Client
import com.lasso.lassoapp.model.ClientWriteRequest
import com.lasso.lassoapp.model.Employee
import com.lasso.lassoapp.model.EmployeeAppointmentSchedule
import com.lasso.lassoapp.model.Event
import com.lasso.lassoapp.model.Service
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class CalendarScreenViewModel(
    private val lassoApi: LassoApi,
) : ViewModel() {
    private val timeZone = TimeZone.currentSystemDefault()
    private val _state = MutableStateFlow(CalendarScreenState())
    val state: StateFlow<CalendarScreenState> = _state.asStateFlow()
    private var loadJob: Job? = null

    init {
        val today = Clock.System.now().toLocalDateTime(timeZone).date
        _state.value = _state.value.copy(selectedDate = today)
        loadEvents(today)
        loadAppointmentOptions()
    }

    fun onDateSelected(date: LocalDate) {
        _state.value = _state.value.copy(selectedDate = date, isDateDialogDisplayed = false)
        loadEvents(date)
    }

    fun showDateDialog() {
        _state.value = _state.value.copy(isDateDialogDisplayed = true)
    }

    fun hideDateDialog() {
        _state.value = _state.value.copy(isDateDialogDisplayed = false)
    }

    fun onViewModeSelected(viewMode: CalendarViewMode) {
        _state.value = _state.value.copy(viewMode = viewMode, isDateDialogDisplayed = false)
        loadEvents(_state.value.selectedDate)
    }

    fun retry() = loadEvents(_state.value.selectedDate)

    fun loadNextThreeDays() = loadAdjacentThreeDays(CalendarRangeDirection.Next)

    fun loadPreviousThreeDays() = loadAdjacentThreeDays(CalendarRangeDirection.Previous)

    private fun loadAdjacentThreeDays(direction: CalendarRangeDirection) {
        val current = _state.value
        if (current.viewMode != CalendarViewMode.ThreeDays || current.isLoading) return
        loadEvents(current.selectedDate.plus(direction.dayOffset, DateTimeUnit.DAY), rangeDirection = direction)
    }

    fun showNewAppointmentDialog(
        employeeId: Int? = null,
        date: LocalDate = _state.value.selectedDate,
        startMinutes: Int? = null,
    ) {
        _state.value = _state.value.copy(
            isAppointmentDialogDisplayed = true,
            selectedAppointment = null,
            selectedAppointmentEmployeeId = employeeId,
            initialAppointmentDate = date,
            initialAppointmentStartMinutes = startMinutes,
            mutationError = null,
            createdClient = null,
        )
    }

    fun hideAppointmentDialog() {
        if (_state.value.isSavingAppointment || _state.value.isDeleting) return
        resetState()
    }

    fun showEditAppointmentDialog(appointment: CalendarAppointment, employeeId: Int) {
        _state.value = _state.value.copy(
            isAppointmentDialogDisplayed = true,
            selectedAppointment = appointment,
            selectedAppointmentEmployeeId = employeeId,
            initialAppointmentDate = null,
            initialAppointmentStartMinutes = null,
            mutationError = null,
            createdClient = null,
        )
    }

    fun showNewClientDialog() {
        _state.value = _state.value.copy(
            isNewClientDialogDisplayed = true,
            clientSaveError = null,
            createdClient = null,
        )
    }

    fun hideNewClientDialog() {
        if (_state.value.isSavingClient) return
        _state.value = _state.value.copy(
            isNewClientDialogDisplayed = false,
            clientSaveError = null,
        )
    }

    fun createAppointment(request: AppointmentWriteRequest) {
        if (_state.value.isSavingAppointment) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isSavingAppointment = true, mutationError = null)
            try {
                lassoApi.createAppointment(request)
                resetState()
                loadEvents(_state.value.selectedDate)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _state.value = _state.value.copy(
                    isSavingAppointment = false,
                    mutationError = exception.message ?: "No se pudo guardar la cita",
                )
            }
        }
    }

    fun saveAppointment(request: AppointmentWriteRequest) {
        val appointment = _state.value.selectedAppointment
        if (appointment == null) {
            createAppointment(request)
        } else {
            editAppointment(appointment.id, request)
        }
    }

    fun createClient(request: ClientWriteRequest) {
        if (_state.value.isSavingClient) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isSavingClient = true, clientSaveError = null)
            try {
                val client = lassoApi.registerClient(request)
                _state.value = _state.value.copy(
                    isSavingClient = false,
                    clients = (_state.value.clients + client).distinctBy(Client::id).sortedBy(Client::name),
                    createdClient = client,
                    isNewClientDialogDisplayed = false,
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _state.value = _state.value.copy(
                    isSavingClient = false,
                    clientSaveError = exception.message ?: "No se pudo guardar el cliente",
                )
            }
        }
    }

    fun editAppointment(id: Int, request: AppointmentWriteRequest) {
        if (_state.value.isSavingAppointment) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isSavingAppointment = true, mutationError = null)
            try {
                lassoApi.editAppointment(id, request)
                resetState()
                loadEvents(_state.value.selectedDate)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _state.value = _state.value.copy(
                    isSavingAppointment = false,
                    mutationError = exception.message ?: "No se pudo guardar la cita",
                )
            }
        }
    }

    fun deleteAppointment(id: Int) {
        if (_state.value.isDeleting) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isDeleting = true, mutationError = null)
            try {
                lassoApi.deleteAppointment(id)
                resetState()
                loadEvents(_state.value.selectedDate)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _state.value = _state.value.copy(
                    isDeleting = false,
                    mutationError = exception.message ?: "No se pudo eliminar la cita",
                )
            }
        }
    }

    private fun resetState() {
        _state.value = _state.value.copy(
            isSavingAppointment = false,
            isDeleting = false,
            isAppointmentDialogDisplayed = false,
            isNewClientDialogDisplayed = false,
            selectedAppointment = null,
            selectedAppointmentEmployeeId = null,
            initialAppointmentDate = null,
            initialAppointmentStartMinutes = null,
            mutationError = null,
            clientSaveError = null,
            createdClient = null,
        )
    }

    private fun loadEvents(date: LocalDate, rangeDirection: CalendarRangeDirection? = null) {
        loadJob?.cancel()
        val viewMode = _state.value.viewMode
        val originalDate = _state.value.selectedDate
        _state.value = _state.value.copy(
            isLoading = true,
            isLoadingNextRange = rangeDirection == CalendarRangeDirection.Next,
            isLoadingPreviousRange = rangeDirection == CalendarRangeDirection.Previous,
            error = null,
        )
        loadJob = viewModelScope.launch {
            val range = appointmentDayRange(date, timeZone, viewMode.dayCount)
            try {
                val response = lassoApi.getAppointmentCalendar(range.first, range.second)
                if (_state.value.selectedDate != originalDate || _state.value.viewMode != viewMode) return@launch
                _state.value = _state.value.copy(
                    isLoading = false,
                    isLoadingNextRange = false,
                    isLoadingPreviousRange = false,
                    selectedDate = date,
                    employeeSchedules = response.employees,
                    events = response.toEvents(timeZone),
                    error = null,
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                if (_state.value.selectedDate != originalDate || _state.value.viewMode != viewMode) return@launch
                _state.value = _state.value.copy(
                    isLoading = false,
                    isLoadingNextRange = false,
                    isLoadingPreviousRange = false,
                    employeeSchedules = if (rangeDirection != null) _state.value.employeeSchedules else emptyList(),
                    events = if (rangeDirection != null) _state.value.events else emptyList(),
                    error = exception.message ?: "No se pudieron cargar las citas",
                )
            }
        }
    }

    private fun loadAppointmentOptions() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingOptions = true, optionsError = null)
            try {
                val clients = lassoApi.getClients()
                val employees = lassoApi.getEmployees()
                val services = lassoApi.getServices()
                _state.value = _state.value.copy(
                    isLoadingOptions = false,
                    clients = clients.sortedBy(Client::name),
                    employees = employees.sortedBy(Employee::name),
                    services = services.sortedBy(Service::name),
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _state.value = _state.value.copy(
                    isLoadingOptions = false,
                    optionsError = exception.message ?: "No se pudieron cargar los datos de la cita",
                )
            }
        }
    }
}

data class CalendarScreenState(
    val isLoading: Boolean = false,
    val isLoadingNextRange: Boolean = false,
    val isLoadingPreviousRange: Boolean = false,
    val viewMode: CalendarViewMode = CalendarViewMode.Day,
    val isDateDialogDisplayed: Boolean = false,
    val isSavingAppointment: Boolean = false,
    val isDeleting: Boolean = false,
    val isLoadingOptions: Boolean = false,
    val isSavingClient: Boolean = false,
    val isAppointmentDialogDisplayed: Boolean = false,
    val isNewClientDialogDisplayed: Boolean = false,
    val error: String? = null,
    val mutationError: String? = null,
    val optionsError: String? = null,
    val clientSaveError: String? = null,
    val clients: List<Client> = emptyList(),
    val employees: List<Employee> = emptyList(),
    val services: List<Service> = emptyList(),
    val createdClient: Client? = null,
    val selectedAppointment: CalendarAppointment? = null,
    val selectedAppointmentEmployeeId: Int? = null,
    val initialAppointmentDate: LocalDate? = null,
    val initialAppointmentStartMinutes: Int? = null,
    val employeeSchedules: List<EmployeeAppointmentSchedule> = emptyList(),
    val events: List<Event> = emptyList(),
    val selectedDate: LocalDate = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date,
)

fun appointmentDayRange(date: LocalDate, timeZone: TimeZone, dayCount: Int = 1): Pair<Long, Long> {
    require(dayCount > 0)
    val startEpoch = date
        .atStartOfDayIn(timeZone)
        .toEpochMilliseconds()
    val endEpoch = date
        .plus(dayCount, DateTimeUnit.DAY)
        .atStartOfDayIn(timeZone)
        .toEpochMilliseconds()
    return startEpoch to endEpoch
}

fun AppointmentCalendarResponse.toEvents(timeZone: TimeZone): List<Event> {
    val colors = listOf(Color(0xFFAFBBF2), Color(0xFF1B998B), Color(0xFFFFC857), Color(0xFFFF8A80))
    return employees.flatMapIndexed { employeeIndex, employee ->
        employee.appointments.map { appointment ->
            Event(
                appointmentId = appointment.id,
                name = appointment.client?.name?.takeIf { it.isNotBlank() },
                color = colors[employeeIndex % colors.size],
                start = Instant.fromEpochMilliseconds(appointment.startsAt).toLocalDateTime(timeZone),
                end = Instant.fromEpochMilliseconds(appointment.endsAt).toLocalDateTime(timeZone),
                description = appointment.services.map { it.name }.filter { it.isNotBlank() }
                    .joinToString(", ").takeIf { it.isNotBlank() },
                employeeId = employee.id,
            )
        }
    }.sortedBy(Event::start)
}
