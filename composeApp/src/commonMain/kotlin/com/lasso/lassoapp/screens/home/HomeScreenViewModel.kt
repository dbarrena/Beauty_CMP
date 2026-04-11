package com.lasso.lassoapp.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lasso.lassoapp.data.local.session.SessionRepository
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.model.Home
import com.lasso.lassoapp.model.TopSellersResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.math.roundToInt
import kotlin.time.Clock

class HomeScreenViewModel(
    private val lassoApi: LassoApi,
    private val sessionRepository: SessionRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(HomeScreenState())
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            sessionRepository.getSession()?.let { session ->
                _state.value =
                    _state.value.copy(
                        partnerName = session.partnerName,
                        employeeName = session.employeeName,
                    )
            }

            _state.value = _state.value.copy(isLoading = true)

            try {
                // Legacy monthly home payload — still loaded for HomeScreen reference / future use.
                val home = lassoApi.getHome()
                // v2 dashboard does not show top sellers; skip getHomeTopSellers to save traffic.
                _state.value =
                    _state.value.copy(
                        home = home,
                        topSellers = null,
                        todayEarningsFormatted = HomeDashboardPlaceholders.todayEarningsFormatted(),
                        todayTransactionCount = HomeDashboardPlaceholders.todayTransactionCount(),
                        weekEarningsFormatted = HomeDashboardPlaceholders.weekEarningsFormatted(),
                        appointmentsToday = HomeDashboardPlaceholders.appointmentsToday(),
                        appointmentsPending = HomeDashboardPlaceholders.appointmentsPending(),
                        lastSevenDays = HomeDashboardPlaceholders.lastSevenDays(),
                    )
            } catch (e: Exception) {
                _state.value =
                    _state.value.copy(
                        error = e.message,
                        todayEarningsFormatted = HomeDashboardPlaceholders.todayEarningsFormatted(),
                        todayTransactionCount = HomeDashboardPlaceholders.todayTransactionCount(),
                        weekEarningsFormatted = HomeDashboardPlaceholders.weekEarningsFormatted(),
                        appointmentsToday = HomeDashboardPlaceholders.appointmentsToday(),
                        appointmentsPending = HomeDashboardPlaceholders.appointmentsPending(),
                        lastSevenDays = HomeDashboardPlaceholders.lastSevenDays(),
                    )
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }
}

data class HomeDailySalesBar(
    val shortDayLabel: String,
    val valueFormatted: String,
    val yValue: Float,
)

data class HomeScreenState(
    val isLoading: Boolean = false,
    val partnerName: String = "",
    val employeeName: String = "",
    val home: Home? = null,
    val error: String? = null,
    val topSellers: TopSellersResponse? = null,
    val todayEarningsFormatted: String = "",
    val todayTransactionCount: Int = 0,
    val weekEarningsFormatted: String = "",
    val appointmentsToday: Int = 0,
    val appointmentsPending: Int = 0,
    val lastSevenDays: List<HomeDailySalesBar> = emptyList(),
)

/**
 * Replace with real API-backed values when endpoints exist.
 */
object HomeDashboardPlaceholders {
    fun todayEarningsFormatted(): String = "$1,450"

    fun todayTransactionCount(): Int = 5

    fun weekEarningsFormatted(): String = "$5,890"

    fun appointmentsToday(): Int = 3

    fun appointmentsPending(): Int = 1

    /**
     * Rolling last 7 days, Spanish short labels; amounts are static placeholders.
     */
    fun lastSevenDays(): List<HomeDailySalesBar> {
        val tz = TimeZone.currentSystemDefault()
        val today = Clock.System.now().toLocalDateTime(tz).date
        val amounts =
            listOf(10_000f, 12_500f, 8_200f, 15_000f, 9_100f, 11_300f, 13_750f)
        return (0..6).map { index ->
            val date = today.minus(6 - index, DateTimeUnit.DAY)
            HomeDailySalesBar(
                shortDayLabel = spanishDayShort(date),
                valueFormatted = formatMoneyWhole(amounts[index].roundToInt()),
                yValue = amounts[index],
            )
        }
    }

    private fun spanishDayShort(date: LocalDate): String =
        when (date.dayOfWeek) {
            DayOfWeek.MONDAY -> "lun"
            DayOfWeek.TUESDAY -> "mar"
            DayOfWeek.WEDNESDAY -> "mié"
            DayOfWeek.THURSDAY -> "jue"
            DayOfWeek.FRIDAY -> "vie"
            DayOfWeek.SATURDAY -> "sáb"
            DayOfWeek.SUNDAY -> "dom"
        }

    private fun formatMoneyWhole(amount: Int): String {
        val s =
            amount
                .toString()
                .reversed()
                .chunked(3)
                .joinToString(",")
                .reversed()
        return "$$s"
    }
}
