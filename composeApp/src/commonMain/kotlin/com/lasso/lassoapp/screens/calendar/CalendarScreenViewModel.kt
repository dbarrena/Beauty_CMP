package com.lasso.lassoapp.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.model.Event
import com.lasso.lassoapp.model.sampleEvents
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class CalendarScreenViewModel(
    private val lassoApi: LassoApi,
) : ViewModel() {
    private val _state = MutableStateFlow(CalendarScreenState())
    val state: StateFlow<CalendarScreenState> = _state.asStateFlow()

    init {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        _state.value = _state.value.copy(selectedDate = today)
        loadEvents(today)
    }

    fun onDateSelected(date: LocalDate) {
        _state.value = _state.value.copy(selectedDate = date)
        loadEvents(date)
    }

    fun onPreviousDay() {
        val prevDate = _state.value.selectedDate.minus(1, DateTimeUnit.DAY)
        onDateSelected(prevDate)
    }

    fun onNextDay() {
        val nextDate = _state.value.selectedDate.plus(1, DateTimeUnit.DAY)
        onDateSelected(nextDate)
    }

    private fun loadEvents(date: LocalDate) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            // Simulated API call using lassoApi in the future
            // For now, we just pass the sample events
            _state.value = _state.value.copy(
                events = sampleEvents, 
                isLoading = false
            )
            println("Loading events for $date using $lassoApi")
        }
    }
}

data class CalendarScreenState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val events: List<Event> = emptyList(),
    val selectedDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
)
