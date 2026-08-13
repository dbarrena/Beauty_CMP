package com.lasso.lassoapp.screens.clients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.model.Client
import com.lasso.lassoapp.model.ClientWriteRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ClientsState(
    val clients: List<Client> = emptyList(),
    val filteredClients: List<Client> = emptyList(),
    val searchQuery: String = "",
    val selectedClient: Client? = null,
    val isDialogDisplayed: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val loadError: String? = null,
    val saveError: String? = null,
)

fun filterClients(clients: List<Client>, query: String): List<Client> {
    val normalizedQuery = query.trim()
    if (normalizedQuery.isEmpty()) return clients
    return clients.filter { client ->
        client.name.contains(normalizedQuery, ignoreCase = true) ||
            client.phone?.contains(normalizedQuery, ignoreCase = true) == true ||
            client.email?.contains(normalizedQuery, ignoreCase = true) == true
    }
}

class ClientsViewModel(private val lassoApi: LassoApi) : ViewModel() {
    private val _state = MutableStateFlow(ClientsState())
    val state: StateFlow<ClientsState> = _state.asStateFlow()

    init {
        loadClients()
    }

    fun loadClients() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, loadError = null)
            try {
                refreshClients()
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    loadError = exception.message ?: "No se pudieron cargar los clientes",
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(
            searchQuery = query,
            filteredClients = filterClients(_state.value.clients, query),
        )
    }

    fun showCreateDialog() {
        _state.value = _state.value.copy(
            selectedClient = null,
            isDialogDisplayed = true,
            saveError = null,
        )
    }

    fun showEditDialog(client: Client) {
        _state.value = _state.value.copy(
            selectedClient = client,
            isDialogDisplayed = true,
            saveError = null,
        )
    }

    fun hideDialog() {
        if (_state.value.isSaving) return
        _state.value = _state.value.copy(
            selectedClient = null,
            isDialogDisplayed = false,
            saveError = null,
        )
    }

    fun saveClient(request: ClientWriteRequest) {
        val selectedClient = _state.value.selectedClient
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, saveError = null)
            try {
                if (selectedClient == null) {
                    lassoApi.registerClient(request)
                } else {
                    lassoApi.editClient(selectedClient.id, request)
                }
                refreshClients()
                _state.value = _state.value.copy(
                    isSaving = false,
                    isDialogDisplayed = false,
                    selectedClient = null,
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _state.value = _state.value.copy(
                    isSaving = false,
                    saveError = exception.message ?: "No se pudo guardar el cliente",
                )
            }
        }
    }

    private suspend fun refreshClients() {
        val clients = lassoApi.getClients()
        val query = _state.value.searchQuery
        _state.value = _state.value.copy(
            clients = clients,
            filteredClients = filterClients(clients, query),
            isLoading = false,
            loadError = null,
        )
    }
}
