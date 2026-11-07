package com.lasso.lassoapp.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.data.local.session.SessionRepository
import com.lasso.lassoapp.model.AppointmentCalendarResponse
import com.lasso.lassoapp.model.AppointmentWriteRequest
import com.lasso.lassoapp.model.CalendarAppointment
import com.lasso.lassoapp.model.CalendarBlockWriteRequest
import com.lasso.lassoapp.model.CalendarBlock
import com.lasso.lassoapp.model.validationError
import com.lasso.lassoapp.model.Client
import com.lasso.lassoapp.model.ClientWriteRequest
import com.lasso.lassoapp.model.Employee
import com.lasso.lassoapp.model.EmployeeAppointmentSchedule
import com.lasso.lassoapp.model.Event
import com.lasso.lassoapp.model.Service
import com.lasso.lassoapp.ui.theme.calendarColorOrPrimary
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
import kotlinx.datetime.toInstant
import kotlin.time.Clock

class CalendarScreenViewModel(
    private val lassoApi: LassoApi,
    private val sessionRepository: SessionRepository,
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

    fun loadNextRange() = loadAdjacentRange(CalendarRangeDirection.Next)

    fun loadPreviousRange() = loadAdjacentRange(CalendarRangeDirection.Previous)

    private fun loadAdjacentRange(direction: CalendarRangeDirection) {
        val current = _state.value
        if (current.isLoading || current.isSavingAppointment || current.isDeleting) return
        loadEvents(current.selectedDate.plus(direction.dayOffset(current.viewMode.dayCount), DateTimeUnit.DAY), rangeDirection = direction)
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

    fun showCalendarBlockDialog(blockId: Int? = null) {
        if (_state.value.isCalendarBlockDialogDisplayed || _state.value.isSavingCalendarBlock) return
        val block = blockId?.let { id -> _state.value.employeeSchedules.flatMap { it.blockedHours }.firstOrNull { it.id == id } }
        if (blockId != null && block == null) return
        _state.value = _state.value.copy(isCalendarBlockDialogDisplayed = true, calendarBlockError = null, selectedCalendarBlock = block)
        loadCalendarBlockEmployees()
    }

    fun hideCalendarBlockDialog() {
        if (_state.value.isSavingCalendarBlock || _state.value.isDeletingCalendarBlock) return
        _state.value = _state.value.copy(isCalendarBlockDialogDisplayed = false, calendarBlockError = null, selectedCalendarBlock = null)
    }

    fun loadCalendarBlockEmployees() {
        if (_state.value.isLoadingCalendarBlockEmployees || _state.value.isSavingCalendarBlock) return
        _state.value = _state.value.copy(isLoadingCalendarBlockEmployees = true, calendarBlockError = null)
        viewModelScope.launch {
            try {
                val session = sessionRepository.getSession() ?: error("No se encontró una sesión activa.")
                val employees = lassoApi.getEmployees().filter { session.isAdmin || it.id == session.employeeId }
                _state.value = _state.value.copy(isLoadingCalendarBlockEmployees = false,
                    calendarBlockEmployees = employees.sortedBy(Employee::name))
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _state.value = _state.value.copy(isLoadingCalendarBlockEmployees = false,
                    calendarBlockEmployees = emptyList(), calendarBlockError = exception.message ?: "No se pudieron cargar los empleados.")
            }
        }
    }

    fun createCalendarBlock(request: CalendarBlockWriteRequest) {
        val current = _state.value
        if (!current.isCalendarBlockDialogDisplayed || current.isSavingCalendarBlock || current.isDeletingCalendarBlock ||
            current.isLoadingCalendarBlockEmployees || current.selectedCalendarBlock != null) return
        val error = request.validationError() ?: if (current.calendarBlockEmployees.none { it.id == request.employeeId })
            "Selecciona un empleado disponible." else null
        if (error != null) {
            _state.value = current.copy(calendarBlockError = error)
            return
        }
        _state.value = current.copy(isSavingCalendarBlock = true, calendarBlockError = null)
        viewModelScope.launch {
            try {
                lassoApi.createCalendarBlock(request)
                _state.value = _state.value.copy(isSavingCalendarBlock = false,
                    isCalendarBlockDialogDisplayed = false, calendarBlockNotice = "Bloqueo de calendario agregado.")
                loadEvents(_state.value.selectedDate)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _state.value = _state.value.copy(isSavingCalendarBlock = false,
                    calendarBlockError = exception.message ?: "No se pudo agregar el bloqueo de calendario.")
            }
        }
    }

    fun clearCalendarBlockNotice() {
        _state.value = _state.value.copy(calendarBlockNotice = null)
    }

    fun deleteCalendarBlock() {
        val current = _state.value
        val block = current.selectedCalendarBlock ?: return
        if (!current.isCalendarBlockDialogDisplayed || current.isSavingCalendarBlock || current.isDeletingCalendarBlock) return
        _state.value = current.copy(isDeletingCalendarBlock = true, calendarBlockError = null)
        viewModelScope.launch {
            try {
                lassoApi.deleteCalendarBlock(block.id)
                _state.value = _state.value.copy(isDeletingCalendarBlock = false,
                    isCalendarBlockDialogDisplayed = false, selectedCalendarBlock = null,
                    calendarBlockNotice = "Bloqueo de calendario eliminado.")
                loadEvents(_state.value.selectedDate)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _state.value = _state.value.copy(isDeletingCalendarBlock = false,
                    calendarBlockError = exception.message ?: "No se pudo eliminar el bloqueo de calendario.")
            }
        }
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
            editingClient = null,
            clientSaveError = null,
            createdClient = null,
        )
    }

    fun showEditClientDialog(client: Client) {
        if (_state.value.isSavingClient) return
        _state.value = _state.value.copy(
            isNewClientDialogDisplayed = true,
            editingClient = client,
            clientSaveError = null,
            createdClient = null,
        )
    }

    fun hideNewClientDialog() {
        if (_state.value.isSavingClient) return
        _state.value = _state.value.copy(
            isNewClientDialogDisplayed = false,
            editingClient = null,
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

    fun saveClient(request: ClientWriteRequest) {
        if (_state.value.isSavingClient) return
        val editingClient = _state.value.editingClient
        viewModelScope.launch {
            _state.value = _state.value.copy(isSavingClient = true, clientSaveError = null)
            try {
                val client = if (editingClient == null) {
                    lassoApi.registerClient(request)
                } else {
                    lassoApi.editClient(editingClient.id, request)
                }
                _state.value = _state.value.copy(
                    isSavingClient = false,
                    clients = (_state.value.clients.filterNot { it.id == client.id } + client).sortedBy(Client::name),
                    createdClient = client,
                    isNewClientDialogDisplayed = false,
                    editingClient = null,
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

    fun moveAppointment(event: Event, startMinutes: Int) {
        val current = _state.value
        if (current.isSavingAppointment || current.isDeleting || current.isLoading || !event.canDragAppointment()) return
        val schedule = current.employeeSchedules.firstOrNull { it.id == event.employeeId } ?: return
        val appointment = schedule.appointments.firstOrNull { it.id == event.appointmentId } ?: return
        val original = current.events.firstOrNull { it.appointmentId == appointment.id } ?: return
        val duration = ((appointment.endsAt - appointment.startsAt) / 60_000).toInt()
        if (duration !in 1..840 || startMinutes % 15 != 0 ||
            startMinutes < 7 * 60 || startMinutes + duration > 21 * 60) return
        val moved = original.atStartMinutes(startMinutes, timeZone)
        val startsAt = moved.start.toInstant(timeZone).toEpochMilliseconds()
        val endsAt = startsAt + appointment.endsAt - appointment.startsAt
        if (startsAt == appointment.startsAt) return
        val request = AppointmentWriteRequest(
            employeeId = schedule.id,
            clientId = appointment.client?.id,
            serviceIds = appointment.services.map { it.id },
            startsAt = startsAt,
            endsAt = endsAt,
            notes = appointment.notes,
        )
        _state.value = current.copy(isSavingAppointment = true, error = null)
        updateAppointmentTimes(schedule.id, appointment.id, startsAt, endsAt)
        viewModelScope.launch {
            try {
                lassoApi.editAppointment(appointment.id, request)
                _state.value = _state.value.copy(isSavingAppointment = false)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                updateAppointmentTimes(schedule.id, appointment.id, appointment.startsAt, appointment.endsAt)
                _state.value = _state.value.copy(isSavingAppointment = false,
                    error = exception.message ?: "No se pudo cambiar el horario de la cita")
            }
        }
    }

    private fun updateAppointmentTimes(employeeId: Int, appointmentId: Int, startsAt: Long, endsAt: Long) {
        val schedules = _state.value.employeeSchedules.map { employee ->
            if (employee.id != employeeId) employee else employee.copy(appointments = employee.appointments.map {
                if (it.id == appointmentId) it.copy(startsAt = startsAt, endsAt = endsAt) else it
            })
        }
        _state.value = _state.value.copy(employeeSchedules = schedules,
            events = appointmentDayRange(_state.value.selectedDate, timeZone, _state.value.viewMode.dayCount).let {
                AppointmentCalendarResponse(it.first, it.second, schedules).toEvents(timeZone)
            })
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
            editingClient = null,
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
                val session = sessionRepository.getSession() ?: error("No se encontró una sesión activa.")
                val calendar = lassoApi.getAppointmentCalendar(range.first, range.second)
                val response = calendar.copy(employees = calendar.employees.filter {
                    session.isAdmin || it.id == session.employeeId
                })
                if (_state.value.selectedDate != originalDate || _state.value.viewMode != viewMode) return@launch
                _state.value = _state.value.copy(
                    isLoading = false,
                    isLoadingNextRange = false,
                    isLoadingPreviousRange = false,
                    selectedDate = date,
                    hasLoadedCalendar = true,
                    calendarLoadRevision = _state.value.calendarLoadRevision + 1,
                    calendarLoadDirection = rangeDirection,
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
                val session = sessionRepository.getSession() ?: error("No se encontró una sesión activa.")
                val employees = lassoApi.getEmployees().filter { session.isAdmin || it.id == session.employeeId }
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
    val hasLoadedCalendar: Boolean = false,
    val calendarLoadRevision: Int = 0,
    val calendarLoadDirection: CalendarRangeDirection? = null,
    val isCalendarBlockDialogDisplayed: Boolean = false,
    val isSavingCalendarBlock: Boolean = false,
    val isDeletingCalendarBlock: Boolean = false,
    val selectedCalendarBlock: CalendarBlock? = null,
    val isLoadingCalendarBlockEmployees: Boolean = false,
    val calendarBlockEmployees: List<Employee> = emptyList(),
    val calendarBlockError: String? = null,
    val calendarBlockNotice: String? = null,
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
    val editingClient: Client? = null,
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
    return employees.flatMap { employee ->
        employee.appointments.map { appointment ->
            Event(
                appointmentId = appointment.id,
                name = appointment.client?.name?.takeIf { it.isNotBlank() },
                color = calendarColorOrPrimary(employee.calendarColor),
                start = Instant.fromEpochMilliseconds(appointment.startsAt).toLocalDateTime(timeZone),
                end = Instant.fromEpochMilliseconds(appointment.endsAt).toLocalDateTime(timeZone),
                description = appointment.services.map { it.name }.filter { it.isNotBlank() }
                    .joinToString(", ").takeIf { it.isNotBlank() },
                employeeId = employee.id,
            )
        } + employee.blockEvents(startEpoch, endEpoch, timeZone)
    }.sortedBy(Event::start)
}
