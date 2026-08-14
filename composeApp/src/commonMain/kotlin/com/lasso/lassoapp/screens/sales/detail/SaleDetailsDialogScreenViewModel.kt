package com.lasso.lassoapp.screens.sales.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.model.Client
import com.lasso.lassoapp.model.Employee
import com.lasso.lassoapp.model.SaleApiResponse
import com.lasso.lassoapp.model.SaleDetailApiResponse
import com.lasso.lassoapp.model.SaleDetailEditApiRequest
import com.lasso.lassoapp.model.SaleEditApiRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

class SaleDetailsDialogScreenViewModel(private val lassoApi: LassoApi) : ViewModel() {
    private val _state = MutableStateFlow(SalesDetailScreenState())
    val state: StateFlow<SalesDetailScreenState> = _state.asStateFlow()

    fun setSale(sale: SaleApiResponse) {
        if (_state.value.sale?.id == sale.id) return
        _state.value = SalesDetailScreenState(
            sale = sale,
            draft = sale.toDraft(),
            isLoadingOptions = true,
        )
        loadOptions()
    }

    private fun loadOptions() {
        viewModelScope.launch {
            runCatching {
                val clients = async { lassoApi.getClients() }
                val employees = async { lassoApi.getEmployees() }
                val refreshedSale = async { _state.value.sale?.id?.let { lassoApi.getSale(it) } }
                Triple(clients.await(), employees.await(), refreshedSale.await())
            }.onSuccess { (clients, employees, refreshedSale) ->
                _state.update {
                    val authoritativeSale = refreshedSale ?: it.sale
                    it.copy(
                        sale = authoritativeSale,
                        draft = authoritativeSale?.toDraft(),
                        clients = clients.sortedBy(Client::name),
                        employees = employees.sortedBy(Employee::name),
                        isLoadingOptions = false,
                        error = null,
                    )
                }
            }.onFailure { throwable ->
                _state.update {
                    it.copy(
                        isLoadingOptions = false,
                        error = throwable.message ?: "No se pudieron cargar clientes y empleados.",
                    )
                }
            }
        }
    }

    fun selectClient(clientId: Int?) {
        _state.update { state -> state.copy(draft = state.draft?.copy(clientId = clientId)) }
    }

    fun selectEmployee(employeeId: Int) {
        _state.update { state -> state.copy(draft = state.draft?.copy(employeeId = employeeId)) }
    }

    fun selectDate(date: LocalDate) {
        _state.update { state -> state.copy(draft = state.draft?.copy(date = date)) }
    }

    @OptIn(ExperimentalTime::class)
    fun saveParent(onSaved: () -> Unit) {
        val currentState = _state.value
        val sale = currentState.sale ?: return
        val draft = currentState.draft ?: return
        val employeeId = draft.employeeId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isSavingParent = true, error = null) }
            runCatching {
                lassoApi.editSale(
                    saleId = sale.id,
                    request = SaleEditApiRequest(
                        createdAt = sale.createdAt.withDate(draft.date),
                        clientId = draft.clientId,
                        employeeId = employeeId,
                    ),
                )
            }.onSuccess { updatedSale ->
                _state.update {
                    it.copy(
                        sale = updatedSale,
                        draft = updatedSale.toDraft(),
                        isSavingParent = false,
                        dismissShouldReload = true,
                        error = null,
                    )
                }
                onSaved()
            }.onFailure { throwable ->
                _state.update {
                    it.copy(
                        isSavingParent = false,
                        error = throwable.message ?: "No se pudieron guardar los cambios.",
                    )
                }
            }
        }
    }

    fun editSaleDetail(request: SaleDetailEditApiRequest, onEdited: () -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isDetailOperationRunning = true, error = null) }
            runCatching {
                lassoApi.editSaleDetail(request) ?: error("No se pudo editar el artículo.")
            }
                .onSuccess {
                    _state.update { state -> state.copy(dismissShouldReload = true) }
                    refreshSale(onFinished = onEdited)
                }
                .onFailure { throwable ->
                    _state.update {
                        it.copy(
                            isDetailOperationRunning = false,
                            error = throwable.message ?: "No se pudo editar el artículo.",
                        )
                    }
                }
        }
    }

    fun deleteSaleDetail(detail: SaleDetailApiResponse, onDeleted: () -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isDetailOperationRunning = true, error = null) }
            runCatching {
                lassoApi.deleteSaleDetail(detail.id) ?: error("No se pudo eliminar el artículo.")
            }
                .onSuccess {
                    _state.update { state -> state.copy(dismissShouldReload = true) }
                    refreshSale(onFinished = onDeleted)
                }
                .onFailure { throwable ->
                    _state.update {
                        it.copy(
                            isDetailOperationRunning = false,
                            error = throwable.message ?: "No se pudo eliminar el artículo.",
                        )
                    }
                }
        }
    }

    fun deleteSale(onDeleted: () -> Unit) {
        val saleId = _state.value.sale?.id ?: return
        viewModelScope.launch {
            _state.update { it.copy(isDeletingSale = true, error = null) }
            runCatching {
                lassoApi.deleteSale(saleId) ?: error("No se pudo eliminar la venta.")
            }
                .onSuccess {
                    onDeleted()
                }
                .onFailure { throwable ->
                    _state.update {
                        it.copy(
                            isDeletingSale = false,
                            error = throwable.message ?: "No se pudo eliminar la venta.",
                        )
                    }
                }
        }
    }

    private suspend fun refreshSale(onFinished: () -> Unit) {
        val saleId = _state.value.sale?.id ?: return
        runCatching {
            lassoApi.getSale(saleId) ?: error("No se pudo actualizar la venta.")
        }
            .onSuccess { updatedSale ->
                _state.update {
                    it.copy(
                        sale = updatedSale,
                        isDetailOperationRunning = false,
                        error = null,
                    )
                }
                onFinished()
            }
            .onFailure { throwable ->
                _state.update {
                    it.copy(
                        isDetailOperationRunning = false,
                        error = throwable.message ?: "No se pudo actualizar la venta.",
                    )
                }
            }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}

data class SaleParentDraft(
    val date: LocalDate,
    val clientId: Int?,
    val employeeId: Int?,
)

data class SalesDetailScreenState(
    val sale: SaleApiResponse? = null,
    val draft: SaleParentDraft? = null,
    val clients: List<Client> = emptyList(),
    val employees: List<Employee> = emptyList(),
    val isLoadingOptions: Boolean = false,
    val isSavingParent: Boolean = false,
    val isDetailOperationRunning: Boolean = false,
    val isDeletingSale: Boolean = false,
    val dismissShouldReload: Boolean = false,
    val error: String? = null,
) {
    val isBusy: Boolean
        get() = isSavingParent || isDetailOperationRunning || isDeletingSale

    val hasParentChanges: Boolean
        get() {
            val persisted = sale ?: return false
            val pending = draft ?: return false
            return pending.date != persisted.createdAt.toLocalDateTime(TimeZone.currentSystemDefault()).date ||
                pending.clientId != persisted.clientId ||
                pending.employeeId != persisted.commonEmployeeId()
        }

    val canSaveParent: Boolean
        get() = !isBusy && !isLoadingOptions && draft?.employeeId != null && hasParentChanges
}

@OptIn(ExperimentalTime::class)
private fun SaleApiResponse.toDraft(): SaleParentDraft = SaleParentDraft(
    date = createdAt.toLocalDateTime(TimeZone.currentSystemDefault()).date,
    clientId = clientId,
    employeeId = commonEmployeeId(),
)

private fun SaleApiResponse.commonEmployeeId(): Int? {
    if (saleDetails.isEmpty()) return null
    val ids = saleDetails.map { it.employeeId }.distinct()
    return ids.singleOrNull()
}

@OptIn(ExperimentalTime::class)
private fun Long.toLocalDateTime(timeZone: TimeZone): LocalDateTime =
    Instant.fromEpochMilliseconds(this).toLocalDateTime(timeZone)

@OptIn(ExperimentalTime::class)
private fun Long.withDate(date: LocalDate): Long {
    val timeZone = TimeZone.currentSystemDefault()
    val original = toLocalDateTime(timeZone)
    return LocalDateTime(
        date.year,
        date.monthNumber,
        date.dayOfMonth,
        original.hour,
        original.minute,
        original.second,
        original.nanosecond,
    ).toInstant(timeZone).toEpochMilliseconds()
}
