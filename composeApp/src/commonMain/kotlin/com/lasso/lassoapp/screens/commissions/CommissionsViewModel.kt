package com.lasso.lassoapp.screens.commissions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.model.CommissionCalculationResponse
import com.lasso.lassoapp.model.Employee
import com.lasso.lassoapp.screens.sales.v2.SalesPeriodFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class CommissionsState(
    val isLoading: Boolean = false,
    val employees: List<Employee> = emptyList(),
    val selectedEmployee: Employee? = null,
    val commissions: List<CommissionCalculationResponse> = emptyList(),
    val periodFilter: SalesPeriodFilter = SalesPeriodFilter.Today,
    val selectedDateStart: Long? = null,
    val selectedDateEnd: Long? = null,
    val totalCommission: Double = 0.0,
    val serviceCommission: Double = 0.0,
    val productCommission: Double = 0.0,
    val transactionCount: Int = 0,
    val reportPeriodStart: LocalDate? = null,
    val reportPeriodEnd: LocalDate? = null,
)

class CommissionsViewModel(private val lassoApi: LassoApi) : ViewModel() {
    private var calculationJob: Job? = null
    private val _state = MutableStateFlow(CommissionsState())
    val state: StateFlow<CommissionsState> = _state.asStateFlow()

    init {
        loadEmployees()
    }

    private fun loadEmployees() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val employees = lassoApi.getEmployees()
            _state.value = _state.value.copy(
                employees = employees,
                selectedEmployee = employees.firstOrNull(),
                isLoading = false
            )
            loadCommissions()
        }
    }

    fun setSelectedEmployee(employee: Employee) {
        _state.value = _state.value.copy(selectedEmployee = employee)
        loadCommissions()
    }

    fun loadForPeriod(period: SalesPeriodFilter) {
        val clearCustomDates = period != SalesPeriodFilter.Custom
        _state.value = _state.value.copy(
            periodFilter = period,
            selectedDateStart = if (clearCustomDates) null else _state.value.selectedDateStart,
            selectedDateEnd = if (clearCustomDates) null else _state.value.selectedDateEnd,
        )
        loadCommissions()
    }

    @OptIn(ExperimentalTime::class)
    fun applyCustomDateRange(start: Long?, end: Long?) {
        if (start == null || end == null) return
        _state.value = _state.value.copy(
            periodFilter = SalesPeriodFilter.Custom,
            selectedDateStart = start,
            selectedDateEnd = end,
        )
        loadCommissions()
    }

    fun reloadCommissions() {
        loadCommissions()
    }

    private fun loadCommissions() {
        val employeeId = _state.value.selectedEmployee?.id ?: return
        val period = _state.value.periodFilter
        val range = getEpochRangeForPeriod(period)
        calculationJob?.cancel()
        _state.value = _state.value.copy(
            isLoading = true, commissions = emptyList(), reportPeriodStart = null, reportPeriodEnd = null,
            totalCommission = 0.0, serviceCommission = 0.0, productCommission = 0.0, transactionCount = 0,
        )
        
        calculationJob = viewModelScope.launch {
            runCatching {
                lassoApi.calculateCommissions(employeeId, range.first, range.second)
            }.onSuccess { result ->
                val totalComm = result.sumOf { it.totalCommission }
                val serviceComm = result.sumOf { it.serviceCommission }
                val productComm = result.sumOf { it.productCommission }
                val transCount = result.sumOf { it.products.size + it.services.size }
                
                _state.value = _state.value.copy(
                    commissions = result,
                    totalCommission = totalComm,
                    serviceCommission = serviceComm,
                    productCommission = productComm,
                    transactionCount = transCount,
                    reportPeriodStart = Instant.fromEpochMilliseconds(range.first).toLocalDateTime(TimeZone.currentSystemDefault()).date,
                    reportPeriodEnd = Instant.fromEpochMilliseconds(range.second - 1).toLocalDateTime(TimeZone.currentSystemDefault()).date,
                    isLoading = false
                )
            }.onFailure {
                if (it is CancellationException) throw it
                it.printStackTrace()
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun getEpochRangeForPeriod(period: SalesPeriodFilter): Pair<Long, Long> {
        val tz = TimeZone.currentSystemDefault()
        return when (period) {
            SalesPeriodFilter.Today -> {
                val today = todayLocal()
                Pair(today.atStartOfDayIn(tz).toEpochMilliseconds(), today.plus(1, DateTimeUnit.DAY).atStartOfDayIn(tz).toEpochMilliseconds())
            }
            SalesPeriodFilter.ThisWeek -> {
                val today = todayLocal()
                val monday = today.minus(DatePeriod(days = daysSinceMonday(today.dayOfWeek)))
                val nextMonday = monday.plus(DatePeriod(days = 7))
                Pair(monday.atStartOfDayIn(tz).toEpochMilliseconds(), nextMonday.atStartOfDayIn(tz).toEpochMilliseconds())
            }
            SalesPeriodFilter.ThisMonth -> {
                val today = todayLocal()
                val firstDay = LocalDate(today.year, today.month, 1)
                val firstDayNextMonth = if (today.month == Month.DECEMBER) {
                    LocalDate(today.year + 1, Month.JANUARY, 1)
                } else {
                    LocalDate(today.year, Month.entries[today.month.ordinal + 1], 1)
                }
                Pair(firstDay.atStartOfDayIn(tz).toEpochMilliseconds(), firstDayNextMonth.atStartOfDayIn(tz).toEpochMilliseconds())
            }
            SalesPeriodFilter.Custom -> {
                val start = _state.value.selectedDateStart
                val end = _state.value.selectedDateEnd
                if (start != null && end != null) {
                    val startLocalDate = Instant.fromEpochMilliseconds(start).toLocalDateTime(TimeZone.UTC).date
                    val endLocalDate = Instant.fromEpochMilliseconds(end).toLocalDateTime(TimeZone.UTC).date
                    Pair(
                        startLocalDate.atStartOfDayIn(tz).toEpochMilliseconds(),
                        endLocalDate.plus(1, DateTimeUnit.DAY).atStartOfDayIn(tz).toEpochMilliseconds()
                    )
                } else {
                    val today = todayLocal()
                    Pair(today.atStartOfDayIn(tz).toEpochMilliseconds(), today.plus(1, DateTimeUnit.DAY).atStartOfDayIn(tz).toEpochMilliseconds())
                }
            }
        }
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
