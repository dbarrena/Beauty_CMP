package com.beauty.beautyapp.screens.sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beauty.beautyapp.data.remote.BeautyApi
import com.beauty.beautyapp.model.SaleApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToLong

class SalesScreenViewModel(private val beautyApi: BeautyApi): ViewModel() {
    private val _state = MutableStateFlow(SalesScreenState())
    val state: StateFlow<SalesScreenState> = _state.asStateFlow()

    init {
        getSales()
    }

    fun getSales() {
        viewModelScope.launch {
            val sales = beautyApi.getSales().sortedByDescending { it.id }
            val total = sales.sumOf { it.total.replace("$", "").toDoubleOrNull() ?: 0.0 }

            _state.value = _state.value.copy(sales = sales, total = total.roundTo2Decimals())
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
    val selectedSale: SaleApiResponse? = null
)