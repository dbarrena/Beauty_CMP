package com.beauty.beautyapp.screens.cash_closure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beauty.beautyapp.data.remote.BeautyApi
import com.beauty.beautyapp.model.CashClosure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CashClosureViewModel(private val beautyApi: BeautyApi) : ViewModel() {
    private val _state = MutableStateFlow(CashClosureModelState())
    val state: StateFlow<CashClosureModelState> = _state.asStateFlow()

    init {
        _state.value = _state.value.copy(isLoading = true)
        getCashClosure()
    }

    private fun getCashClosure() {
        viewModelScope.launch {
            val cashClosure = beautyApi.getOpenCashClosure()
            _state.value = _state.value.copy(openCashClosure = cashClosure, isLoading = false)
        }
    }
}

data class CashClosureModelState(
    val openCashClosure: CashClosure? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)