package com.lasso.lassoapp.screens.sales.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.model.SaleApiResponse
import com.lasso.lassoapp.model.SaleDetailApiResponse
import com.lasso.lassoapp.model.SaleDetailEditApiRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SaleDetailsDialogScreenViewModel(private val lassoApi: LassoApi) : ViewModel() {
    private val _state = MutableStateFlow(SalesDetailScreenState())
    val state: StateFlow<SalesDetailScreenState> = _state.asStateFlow()

    fun setSaleDetail(saleDetail: SaleDetailApiResponse) {
        _state.value = _state.value.copy(selectedSaleDetail = saleDetail)
    }

    fun setSale(sale: SaleApiResponse) {
        _state.value = _state.value.copy(sale = sale)
    }

    fun editSaleDetail(
        saleDetailEditApiRequest: SaleDetailEditApiRequest,
        onSaleDetailEdited: () -> Unit
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            lassoApi.editSaleDetail(saleDetailEditApiRequest)
            _state.value = _state.value.copy(
                dismissShouldReload = true,
                isLoading = false
            )
            onSaleDetailEdited()
        }
    }

    fun deleteSaleDetail(onSaleDetailDeleted: () -> Unit) {
        viewModelScope.launch {
            _state.value.selectedSaleDetail?.let {
                _state.value = _state.value.copy(isLoading = true)
                val resultMessage = lassoApi.deleteSaleDetail(it.id)

                resultMessage?.let {
                    _state.value = _state.value.copy(
                        dismissShouldReload = true,
                        selectedSaleDetail = null,
                        isLoading = false
                    )
                    onSaleDetailDeleted()
                }
            }
        }
    }

    fun deleteSale(onSaleDeleted: () -> Unit) {
        viewModelScope.launch {
            _state.value.sale?.let {
                _state.value = _state.value.copy(isLoading = true)
                val resultMessage = lassoApi.deleteSale(it.id)

                resultMessage?.let {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        selectedSaleDetail = null,
                    )
                    onSaleDeleted()
                }
            }
        }
    }

    fun refreshSale() {
        viewModelScope.launch {
            _state.value.sale?.id?.let { id ->
                _state.value = _state.value.copy(isLoading = true)

                lassoApi.getSale(id)?.let { updatedSale ->
                    _state.value = _state.value.copy(
                        sale =  updatedSale,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun resetState() {
        _state.value = SalesDetailScreenState()
    }
}

data class SalesDetailScreenState(
    val sale: SaleApiResponse? = null,
    val selectedSaleDetail: SaleDetailApiResponse? = null,
    val isLoading: Boolean = false,
    val dismissShouldReload: Boolean = false,
)