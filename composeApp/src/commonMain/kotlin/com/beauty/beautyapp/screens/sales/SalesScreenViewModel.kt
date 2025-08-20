package com.beauty.beautyapp.screens.sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beauty.beautyapp.data.remote.BeautyApi
import com.beauty.beautyapp.model.SaleApiResponse
import com.beauty.beautyapp.screens.utils.toLocalDateTimeString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.roundToLong
import kotlin.time.ExperimentalTime
import kotlinx.datetime.*

class SalesScreenViewModel(private val beautyApi: BeautyApi) : ViewModel() {
    private val _state = MutableStateFlow(SalesScreenState())
    val state: StateFlow<SalesScreenState> = _state.asStateFlow()

    init {
        _state.value = _state.value.copy(isLoading = true)
        getSales()
    }

    @OptIn(ExperimentalTime::class)
    fun getSales() {
        viewModelScope.launch {
            val sales = beautyApi.getThisMonthSales()
                .sortedByDescending { it.id }
                .map {
                    it.copy(
                        formattedDate = it.createdAt.toLocalDateTimeString()
                    )
                }

            val total = sales.sumOf { it.total.replace("$", "").toDoubleOrNull() ?: 0.0 }

            _state.value = _state.value.copy(
                sales = sales,
                total = total.roundTo2Decimals(),
                isLoading = false
            )
        }
    }

    @OptIn(ExperimentalTime::class)
    fun getSalesBetweenDates(start: Long, end: Long) {
        val tz = TimeZone.currentSystemDefault() // adapts to device's timezone

        // Convert picker epoch (UTC midnight) into LocalDate
        val startLocalDate = Instant.fromEpochMilliseconds(start).toLocalDateTime(TimeZone.UTC).date
        val endLocalDate = Instant.fromEpochMilliseconds(end).toLocalDateTime(TimeZone.UTC).date

        // Re-anchor those dates at midnight in the device's local timezone
        val startUtc = startLocalDate.atStartOfDayIn(tz).toEpochMilliseconds()
        val endUtc = endLocalDate
            .plus(1, DateTimeUnit.DAY) // include full end day
            .atStartOfDayIn(tz)
            .toEpochMilliseconds()

        viewModelScope.launch {
            val sales = beautyApi.getSalesBetweenDates(startUtc, endUtc)
                .sortedByDescending { it.id }
                .map {
                    it.copy(
                        formattedDate = it.createdAt.toLocalDateTimeString()
                    )
                }

            val total = sales.sumOf { it.total.replace("$", "").toDoubleOrNull() ?: 0.0 }

            _state.value = _state.value.copy(
                sales = sales,
                total = total.roundTo2Decimals(),
                isLoading = false
            )
        }
    }

    fun setSelectedSale(sale: SaleApiResponse) {
        _state.value = _state.value.copy(selectedSale = sale)
    }

    fun Double.roundTo2Decimals(): Double {
        return (this * 100).roundToLong() / 100.0
    }
}

data class SalesScreenState(
    val total: Double = 0.0,
    val sales: List<SaleApiResponse> = emptyList(),
    val selectedSale: SaleApiResponse? = null,
    val isLoading: Boolean = false,
)