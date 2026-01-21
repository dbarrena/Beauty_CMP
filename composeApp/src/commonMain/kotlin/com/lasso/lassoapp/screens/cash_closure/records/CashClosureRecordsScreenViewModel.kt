package com.lasso.lassoapp.screens.cash_closure.records

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.model.CashClosureRecordsResponse
import com.lasso.lassoapp.model.SaleApiResponse
import com.lasso.lassoapp.screens.utils.toLocalDateTimeString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CashClosureRecordsScreenViewModel(private val lassoApi: LassoApi): ViewModel() {
    private val _state = MutableStateFlow(CashClosureRecordsScreenState())
    val state: StateFlow<CashClosureRecordsScreenState> = _state.asStateFlow()

    init {
        _state.value = _state.value.copy(isLoading = true)
        getCashClosureRecords()
    }

    fun getCashClosureRecords() {
        _state.value = _state.value.copy(isLoading = true)

        viewModelScope.launch {
            val cashClosures = lassoApi.getCashClosureRecords()
                .sortedByDescending { it.id }
                .map {
                    it.copy(
                        formattedDate = it.closedAt.toLocalDateTimeString()
                    )
                }

            _state.value = _state.value.copy(
                cashClosures = cashClosures,
                isLoading = false
            )

            _state.value = _state.value.copy(isLoading = false)
        }
    }

    fun setSelectedCashClosure(cashClosureRecordsResponse: CashClosureRecordsResponse) {
        _state.value = _state.value.copy(selectedCashClosure = cashClosureRecordsResponse)
    }
}

data class CashClosureRecordsScreenState(
    val cashClosures: List<CashClosureRecordsResponse> = emptyList(),
    val selectedCashClosure: CashClosureRecordsResponse? = null,
    val isLoading: Boolean = false,
    val selectedDateStart: Long? = null,
    val selectedDateEnd: Long? = null
)