package com.beauty.beautyapp.screens.sales.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beauty.beautyapp.data.remote.BeautyApi
import com.beauty.beautyapp.model.SaleApiResponse
import com.beauty.beautyapp.model.SaleDetailApiResponse
import com.beauty.beautyapp.model.SaleDetailEditApiRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SaleDetailsDialogScreenViewModel(private val beautyApi: BeautyApi) : ViewModel() {
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
            beautyApi.editSaleDetail(saleDetailEditApiRequest)
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
                val resultMessage = beautyApi.deleteSaleDetail(it.id)

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
                val resultMessage = beautyApi.deleteSale(it.id)

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

                beautyApi.getSale(id)?.let { updatedSale ->
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