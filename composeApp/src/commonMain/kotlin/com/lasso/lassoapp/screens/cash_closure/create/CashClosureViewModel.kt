package com.lasso.lassoapp.screens.cash_closure.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.model.CashClosure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CashClosureViewModel(private val lassoApi: LassoApi) : ViewModel() {
    private val _state = MutableStateFlow(CashClosureModelState())
    val state: StateFlow<CashClosureModelState> = _state.asStateFlow()

    init {
        _state.value = _state.value.copy(isLoading = true)
        getCashClosure()
    }

    private fun getCashClosure() {
        viewModelScope.launch {
            val cashClosure = lassoApi.getOpenCashClosure()
            _state.value = _state.value.copy(openCashClosure = cashClosure, isLoading = false)
        }
    }

    fun createCashClosure() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            lassoApi.createCashClosure()
            getCashClosure()
        }
    }
}

data class CashClosureModelState(
    val openCashClosure: CashClosure? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)