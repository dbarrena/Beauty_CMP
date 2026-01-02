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

class CalendarScreenViewModel(
    private val lassoApi: LassoApi
) : ViewModel() {
    private val _state = MutableStateFlow(CalendarScreenState())
    val state: StateFlow<CalendarScreenState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = _state.value.copy(events = sampleEvents)
        }
    }
}

data class CalendarScreenState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val events: List<Event> = emptyList()
)