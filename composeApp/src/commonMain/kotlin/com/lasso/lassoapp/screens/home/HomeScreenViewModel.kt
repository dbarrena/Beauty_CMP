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

class HomeScreenViewModel(
    private val lassoApi: LassoApi,
    private val sessionRepository: SessionRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HomeScreenState())
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            sessionRepository.getSession()?.let { session ->
                _state.value = _state.value.copy(
                    partnerName = session.partnerName,
                    employeeName = session.employeeName
                )
            }

            _state.value = _state.value.copy(isLoading = true,)

            try {
                val home = lassoApi.getHome()
                val topSellers = lassoApi.getHomeTopSellers()
                _state.value = _state.value.copy(home = home, topSellers = topSellers)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }
}

data class HomeScreenState(
    val isLoading: Boolean = false,
    val partnerName: String = "",
    val employeeName: String = "",
    val home: Home? = null,
    val error: String? = null,
    val topSellers: TopSellersResponse? = null
)