package com.lasso.lassoapp.screens.reports.sales_by_product_category

import androidx.compose.material3.rememberDateRangePickerState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.model.Product
import com.lasso.lassoapp.model.ProductCategory
import com.lasso.lassoapp.model.SalesByProductCategoryItemApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class SalesByProductCategoryViewModel(private val lassoApi: LassoApi) : ViewModel() {
    private val _state = MutableStateFlow(SalesByProductCategoryScreenState())
    val state: StateFlow<SalesByProductCategoryScreenState> = _state.asStateFlow()

    init {
        _state.value = _state.value.copy(isLoading = true)
        getProductCategories()
    }

    @OptIn(ExperimentalTime::class)
    fun getSalesBetweenDates(start: Long, end: Long, categoryId: Int) {
        _state.value = _state.value.copy(
            isLoading = true
        )

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
            lassoApi.getSalesByProductCategory(startUtc, endUtc, categoryId)?.let { sales ->
                _state.value = _state.value.copy(
                    total = sales.totalRevenue,
                    totalQuantity = sales.totalQuantity,
                    products = sales.products,
                    isLoading = false
                )
            }
        }
    }

    fun setSelectedDateRange(start: Long?, end: Long?) {
        _state.value = _state.value.copy(selectedDateStart = start, selectedDateEnd = end)
    }

    fun getProductCategories() {
        viewModelScope.launch {
            try {
                val categories = lassoApi.getProductCategories()
                _state.value = _state.value.copy(productCategories = categories, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun setSelectedCategory(category: ProductCategory) {
        _state.value = _state.value.copy(categoryId = category.id)

        // Get current month start and end in epoch millis
        val timeZone = TimeZone.currentSystemDefault()
        val today = Clock.System.now().toLocalDateTime(timeZone).date
        val firstDay = LocalDate(today.year, today.monthNumber, 1)
        val lastDay = firstDay.plus(DatePeriod(months = 1)).minus(DatePeriod(days = 1))

        val firstDayMillis = firstDay.atStartOfDayIn(timeZone).toEpochMilliseconds()
        val lastDayMillis = lastDay.atStartOfDayIn(timeZone).toEpochMilliseconds()

        setSelectedDateRange(firstDayMillis, lastDayMillis)
        category.id?.let {
            getSalesBetweenDates(firstDayMillis, lastDayMillis, category.id)
        }
    }
}

data class SalesByProductCategoryScreenState(
    val total: String? = null,
    val totalQuantity: Int? = null,
    val categoryId: Int? = null,
    val products: List<SalesByProductCategoryItemApiResponse> = emptyList(),
    val isLoading: Boolean = false,
    val productCategories: List<ProductCategory> = emptyList(),
    val selectedDateStart: Long? = null,
    val selectedDateEnd: Long? = null
)