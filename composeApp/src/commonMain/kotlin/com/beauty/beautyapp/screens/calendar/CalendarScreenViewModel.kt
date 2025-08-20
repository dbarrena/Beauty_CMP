package com.beauty.beautyapp.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beauty.beautyapp.data.remote.BeautyApi
import com.beauty.beautyapp.model.Event
import com.beauty.beautyapp.model.sampleEvents
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CalendarScreenViewModel(
    private val beautyApi: BeautyApi
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