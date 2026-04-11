package com.lasso.lassoapp.screens.sales.v2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.model.SaleApiResponse
import com.lasso.lassoapp.screens.utils.toLocalDateTimeString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.math.roundToLong
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

enum class SalesPeriodFilter {
    Today,
    ThisWeek,
    ThisMonth,
    Custom,
}

/**
 * ViewModel for [SalesScreenV2]: period presets (Hoy / semana / mes / personalizado),
 * aggregates-driven UI, list delete, and reload after detail edits.
 */
class SalesScreenViewModelV2(private val lassoApi: LassoApi) : ViewModel() {
    private val _state = MutableStateFlow(SalesScreenStateV2())
    val state: StateFlow<SalesScreenStateV2> = _state.asStateFlow()

    init {
        loadForPeriod(SalesPeriodFilter.Today)
    }

    fun loadForPeriod(period: SalesPeriodFilter) {
        val clearCustomDates = period != SalesPeriodFilter.Custom
        _state.value = _state.value.copy(
            periodFilter = period,
            selectedDateStart = if (clearCustomDates) null else _state.value.selectedDateStart,
            selectedDateEnd = if (clearCustomDates) null else _state.value.selectedDateEnd,
        )
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            runCatching { fetchSalesForPeriod(period) }
                .onSuccess { applySalesResult(it) }
                .onFailure { it.printStackTrace() }
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    @OptIn(ExperimentalTime::class)
    fun applyCustomDateRange(start: Long?, end: Long?) {
        if (start == null || end == null) return
        _state.value = _state.value.copy(
            periodFilter = SalesPeriodFilter.Custom,
            selectedDateStart = start,
            selectedDateEnd = end,
            isLoading = true,
        )
        viewModelScope.launch {
            runCatching { fetchSalesFromPickerRange(start, end) }
                .onSuccess { applySalesResult(it) }
                .onFailure { it.printStackTrace() }
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    fun getSalesBetweenDates(start: Long, end: Long) {
        applyCustomDateRange(start, end)
    }

    fun reloadSales() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            runCatching { fetchSalesForPeriod(_state.value.periodFilter) }
                .onSuccess { applySalesResult(it) }
                .onFailure { it.printStackTrace() }
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    fun deleteSale(saleId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val message = lassoApi.deleteSale(saleId)
            if (message != null) {
                if (_state.value.selectedSale?.id == saleId) {
                    _state.value = _state.value.copy(selectedSale = null)
                }
                runCatching { fetchSalesForPeriod(_state.value.periodFilter) }
                    .onSuccess { applySalesResult(it) }
                    .onFailure { it.printStackTrace() }
            }
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    fun setSelectedSale(sale: SaleApiResponse) {
        _state.value = _state.value.copy(selectedSale = sale)
    }

    fun clearSelectedSale() {
        _state.value = _state.value.copy(selectedSale = null)
    }

    fun Double.roundTo2Decimals(): Double {
        return (this * 100).roundToLong() / 100.0
    }

    fun setSelectedDateRange(start: Long?, end: Long?) {
        _state.value = _state.value.copy(selectedDateStart = start, selectedDateEnd = end)
    }

    private fun applySalesResult(sales: List<SaleApiResponse>) {
        val total = sales.sumOf { it.total.replace("$", "").toDoubleOrNull() ?: 0.0 }
        _state.value = _state.value.copy(
            sales = sales,
            total = total.roundTo2Decimals(),
        )
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun fetchSalesForPeriod(period: SalesPeriodFilter): List<SaleApiResponse> {
        return when (period) {
            SalesPeriodFilter.Today -> fetchSalesForLocalRange(todayLocal(), todayLocal())
            SalesPeriodFilter.ThisWeek -> {
                val today = todayLocal()
                val monday = today.minus(DatePeriod(days = daysSinceMonday(today.dayOfWeek)))
                val sunday = monday.plus(DatePeriod(days = 6))
                fetchSalesForLocalRange(monday, sunday)
            }
            SalesPeriodFilter.ThisMonth -> {
                lassoApi.getThisMonthSales()
                    .sortedByDescending { it.id }
                    .map { it.copy(formattedDate = it.createdAt.toLocalDateTimeString()) }
            }
            SalesPeriodFilter.Custom -> {
                val start = _state.value.selectedDateStart
                val end = _state.value.selectedDateEnd
                if (start != null && end != null) fetchSalesFromPickerRange(start, end)
                else fetchSalesForLocalRange(todayLocal(), todayLocal())
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun fetchSalesFromPickerRange(start: Long, end: Long): List<SaleApiResponse> {
        val tz = TimeZone.currentSystemDefault()
        val startLocalDate = Instant.fromEpochMilliseconds(start).toLocalDateTime(TimeZone.UTC).date
        val endLocalDate = Instant.fromEpochMilliseconds(end).toLocalDateTime(TimeZone.UTC).date
        val startUtc = startLocalDate.atStartOfDayIn(tz).toEpochMilliseconds()
        val endUtc = endLocalDate
            .plus(1, DateTimeUnit.DAY)
            .atStartOfDayIn(tz)
            .toEpochMilliseconds()
        return lassoApi.getSalesBetweenDates(startUtc, endUtc)
            .sortedByDescending { it.id }
            .map { it.copy(formattedDate = it.createdAt.toLocalDateTimeString()) }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun fetchSalesForLocalRange(start: LocalDate, endInclusive: LocalDate): List<SaleApiResponse> {
        val tz = TimeZone.currentSystemDefault()
        val startUtc = start.atStartOfDayIn(tz).toEpochMilliseconds()
        val endUtc = endInclusive
            .plus(1, DateTimeUnit.DAY)
            .atStartOfDayIn(tz)
            .toEpochMilliseconds()
        return lassoApi.getSalesBetweenDates(startUtc, endUtc)
            .sortedByDescending { it.id }
            .map { it.copy(formattedDate = it.createdAt.toLocalDateTimeString()) }
    }

    @OptIn(ExperimentalTime::class)
    private fun todayLocal(): LocalDate {
        val ms = Clock.System.now().toEpochMilliseconds()
        return Instant.fromEpochMilliseconds(ms).toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    private fun daysSinceMonday(day: DayOfWeek): Int =
        when (day) {
            DayOfWeek.MONDAY -> 0
            DayOfWeek.TUESDAY -> 1
            DayOfWeek.WEDNESDAY -> 2
            DayOfWeek.THURSDAY -> 3
            DayOfWeek.FRIDAY -> 4
            DayOfWeek.SATURDAY -> 5
            DayOfWeek.SUNDAY -> 6
        }
}

data class SalesScreenStateV2(
    val total: Double = 0.0,
    val sales: List<SaleApiResponse> = emptyList(),
    val selectedSale: SaleApiResponse? = null,
    val isLoading: Boolean = false,
    val selectedDateStart: Long? = null,
    val selectedDateEnd: Long? = null,
    val periodFilter: SalesPeriodFilter = SalesPeriodFilter.Today,
)
