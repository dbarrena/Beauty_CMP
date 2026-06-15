package com.lasso.lassoapp.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lasso.lassoapp.data.local.session.SessionRepository
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.model.Home
import com.lasso.lassoapp.model.TopSellersResponse
import com.lasso.lassoapp.utils.atEndOfDayEpochMilliseconds
import com.lasso.lassoapp.utils.atEndOfMonth
import com.lasso.lassoapp.utils.atEndOfWeek
import com.lasso.lassoapp.utils.atStartOfMonth
import com.lasso.lassoapp.utils.atStartOfWeek
import com.lasso.lassoapp.utils.toEpochMilliseconds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
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
                        isAdmin = session.isAdmin
                    )
            }

            _state.value = _state.value.copy(isLoading = true)

            try {
                val tz = TimeZone.currentSystemDefault()
                val today = Clock.System.todayIn(tz)

                val startDayEpoch = today.toEpochMilliseconds(tz)
                val endDayEpoch = today.plus(1, DateTimeUnit.DAY).toEpochMilliseconds(tz)

                val startMonth = today.atStartOfMonth()
                val startMonthEpoch = startMonth.toEpochMilliseconds(tz)
                val endMonth = today.atEndOfMonth()
                val endMonthEpoch = endMonth.plus(1, DateTimeUnit.DAY).toEpochMilliseconds(tz)

                val startWeek = today.atStartOfWeek()
                val startWeekEpoch = startWeek.toEpochMilliseconds(tz)
                val endWeek = today.atEndOfWeek()
                val endWeekEpoch = endWeek.plus(1, DateTimeUnit.DAY).toEpochMilliseconds(tz)

                // Home payload with new required date range parameters (Day, Week, Month)
                val home = lassoApi.getHome(
                    startMonthEpoch = startMonthEpoch,
                    endMonthEpoch = endMonthEpoch,
                    startDayEpoch = startDayEpoch,
                    endDayEpoch = endDayEpoch,
                    startWeekEpoch = startWeekEpoch,
                    endWeekEpoch = endWeekEpoch
                )
                // v2 dashboard does not show top sellers; skip getHomeTopSellers to save traffic.
                _state.value =
                    _state.value.copy(
                        home = home,
                        topSellers = null,
                        todayEarningsFormatted = home?.todayEarnings ?: "$0.00",
                        todayTransactionCount = home?.todayTransactionCount ?: 0,
                        weekEarningsFormatted = home?.weekEarnings ?: "$0.00",
                        appointmentsToday = 0,
                        appointmentsPending = 0,
                        lastSevenDays = home?.last7DaysEarnings?.map {
                            HomeDailySalesBar(
                                shortDayLabel = try {
                                    spanishDayShort(LocalDate.parse(it.date))
                                } catch (e: Exception) {
                                    ""
                                },
                                valueFormatted = it.earnings,
                                yValue = it.earningsNumber,
                            )
                        } ?: emptyList(),
                    )
            } catch (e: Exception) {
                _state.value =
                    _state.value.copy(
                        error = e.message,
                        todayEarningsFormatted = "$0.00",
                        todayTransactionCount = 0,
                        weekEarningsFormatted = "$0.00",
                        appointmentsToday = 0,
                        appointmentsPending = 0,
                        lastSevenDays = emptyList(),
                    )
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
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
    val isAdmin: Boolean = false,
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
