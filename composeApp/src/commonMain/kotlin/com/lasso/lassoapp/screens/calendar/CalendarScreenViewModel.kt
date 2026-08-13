package com.lasso.lassoapp.screens.calendar

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.model.AppointmentCalendarResponse
import com.lasso.lassoapp.model.AppointmentWriteRequest
import com.lasso.lassoapp.model.EmployeeAppointmentSchedule
import com.lasso.lassoapp.model.Event
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
import kotlinx.datetime.minus
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
    }

    fun onDateSelected(date: LocalDate) {
        if (date == _state.value.selectedDate && _state.value.employeeSchedules.isNotEmpty()) return
        _state.value = _state.value.copy(selectedDate = date)
        loadEvents(date)
    }

    fun onPreviousDay() = onDateSelected(_state.value.selectedDate.minus(1, DateTimeUnit.DAY))

    fun onNextDay() = onDateSelected(_state.value.selectedDate.plus(1, DateTimeUnit.DAY))

    fun retry() = loadEvents(_state.value.selectedDate)

    fun createAppointment(request: AppointmentWriteRequest) {
        if (_state.value.isSaving) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, mutationError = null)
            try {
                lassoApi.createAppointment(request)
                _state.value = _state.value.copy(isSaving = false)
                loadEvents(_state.value.selectedDate)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _state.value = _state.value.copy(
                    isSaving = false,
                    mutationError = exception.message ?: "No se pudo guardar la cita",
                )
            }
        }
    }

    fun editAppointment(id: Int, request: AppointmentWriteRequest) {
        if (_state.value.isSaving) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, mutationError = null)
            try {
                lassoApi.editAppointment(id, request)
                _state.value = _state.value.copy(isSaving = false)
                loadEvents(_state.value.selectedDate)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _state.value = _state.value.copy(
                    isSaving = false,
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
                _state.value = _state.value.copy(isDeleting = false)
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

    fun clearMutationError() {
        _state.value = _state.value.copy(mutationError = null)
    }

    private fun loadEvents(date: LocalDate) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val range = appointmentDayRange(date, timeZone)
            try {
                val response = lassoApi.getAppointmentCalendar(range.first, range.second)
                if (_state.value.selectedDate != date) return@launch
                _state.value = _state.value.copy(
                    isLoading = false,
                    employeeSchedules = response.employees,
                    events = response.toEvents(timeZone),
                    error = null,
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                if (_state.value.selectedDate != date) return@launch
                _state.value = _state.value.copy(
                    isLoading = false,
                    employeeSchedules = emptyList(),
                    events = emptyList(),
                    error = exception.message ?: "No se pudieron cargar las citas",
                )
            }
        }
    }
}

data class CalendarScreenState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val error: String? = null,
    val mutationError: String? = null,
    val employeeSchedules: List<EmployeeAppointmentSchedule> = emptyList(),
    val events: List<Event> = emptyList(),
    val selectedDate: LocalDate = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date,
)

fun appointmentDayRange(date: LocalDate, timeZone: TimeZone): Pair<Long, Long> {
    val startEpoch = date
        .atStartOfDayIn(timeZone)
        .toEpochMilliseconds()
    val endEpoch = date
        .plus(1, DateTimeUnit.DAY)
        .atStartOfDayIn(timeZone)
        .toEpochMilliseconds()
    return startEpoch to endEpoch
}

fun AppointmentCalendarResponse.toEvents(timeZone: TimeZone): List<Event> {
    val colors = listOf(Color(0xFFAFBBF2), Color(0xFF1B998B), Color(0xFFFFC857), Color(0xFFFF8A80))
    return employees.flatMapIndexed { employeeIndex, employee ->
        employee.appointments.map { appointment ->
            Event(
                name = appointment.serviceName,
                color = colors[employeeIndex % colors.size],
                start = Instant.fromEpochMilliseconds(appointment.startsAt).toLocalDateTime(timeZone),
                end = Instant.fromEpochMilliseconds(appointment.endsAt).toLocalDateTime(timeZone),
                description = appointment.client.name,
            )
        }
    }.sortedBy(Event::start)
}
