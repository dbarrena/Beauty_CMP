package com.lasso.lassoapp.screens.pos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lasso.lassoapp.data.local.session.SessionRepository
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.model.LassoItem
import com.lasso.lassoapp.model.ProductCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class PosViewModel(
    private val lassoApi: LassoApi,
    private val sessionRepository: SessionRepository
) : ViewModel() {
    private val _state = MutableStateFlow(PosModelState())
    val state: StateFlow<PosModelState> = _state.asStateFlow()

    init {
        getAvailableItems()
        viewModelScope.launch {
            sessionRepository.getSession()?.let { session ->
                _state.value = _state.value.copy(isAdmin = session.isAdmin)
            }
        }
    }

    fun getAvailableItems() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isAvailableItemsLoading = true)
            val products = lassoApi.getProducts()
            val services = lassoApi.getServices()
            val categories = lassoApi.getProductCategories()
            val items: List<LassoItem> = products + services
            _state.value = _state.value.copy(
                availableItems = items,
                categories = categories,
                isAvailableItemsLoading = false
            )
        }
    }

    fun addSelectedItem(item: LassoItem) {
        val instanceId = "${item.id}-${item.type}"
        val currentState = state.value
        val currentItems = currentState.selectedPosItems
        val index = currentItems.indexOfFirst { it.instanceId == instanceId }

        // If we add an item that already exists, increase the quantity
        val updatedItems = currentItems.toMutableList().apply {
            if (index != -1) {
                val newQuantity = this[index].quantity + 1

                this[index] = this[index].copy(
                    quantity = newQuantity,
                    price = this[index].price
                )
            } else {
                add(
                    SelectedPosItem(
                        instanceId = instanceId,
                        lassoItem = item
                    )
                )
            }
        }

        val newTotal = updatedItems.sumOf { it.price * it.quantity }

        _state.value = currentState.copy(
            selectedPosItems = updatedItems,
            totalPrice = newTotal
        )
    }

    fun updateSelectedItem(item: SelectedPosItem) {
        val currentState = state.value
        val currentItems = currentState.selectedPosItems
        val index = currentItems.indexOfFirst { it.instanceId == item.instanceId }

        if (index != -1) {
            val updatedItems = currentItems.toMutableList().apply {
                this[index] = item
            }

            val newTotal = updatedItems.sumOf { it.price * it.quantity }

            _state.value = currentState.copy(
                selectedPosItems = updatedItems,
                totalPrice = newTotal
            )
        }
    }


    fun restartPos() {
        _state.value = state.value.copy(
            selectedPosItems = mutableListOf<SelectedPosItem>(),
            totalPrice = 0.0,
            selectedItemToEdit = null
        )
    }

    fun removeItem(item: SelectedPosItem) {
        val updatedItems = state.value.selectedPosItems.filter {
            it.instanceId != item.instanceId
        }

        val newTotal = updatedItems.sumOf { it.price * it.quantity }

        _state.value = state.value.copy(
            selectedPosItems = updatedItems.toMutableList(),
            totalPrice = newTotal
        )
    }

    fun selectItemToEdit(item: SelectedPosItem) {
        _state.value = state.value.copy(selectedItemToEdit = item)
    }

    fun restartSelectedItemToEdit() {
        _state.value = state.value.copy(selectedItemToEdit = null)
    }

    fun setCatalogFilter(filter: ProductCategory?) {
        _state.value = state.value.copy(catalogFilter = filter)
    }

    fun setSearchQuery(query: String) {
        _state.value = state.value.copy(searchQuery = query)
    }

    fun filteredCatalogItems(): List<LassoItem> {
        val currentState = state.value
        var items = currentState.availableItems
        
        val filter = currentState.catalogFilter
        if (filter != null) {
            items = items.filter { it.categoryId == filter.id }
        }

        val query = currentState.searchQuery.trim()
        return if (query.isEmpty()) {
            items
        } else {
            items.filter { it.name.contains(query, ignoreCase = true) }
        }
    }
}

data class PosModelState(
    val isAvailableItemsLoading: Boolean = false,
    val availableItems: List<LassoItem> = emptyList(),
    val categories: List<ProductCategory> = emptyList(),
    val selectedPosItems: MutableList<SelectedPosItem> = mutableListOf<SelectedPosItem>(),
    val totalPrice: Double = 0.0,
    val selectedItemToEdit: SelectedPosItem? = null,
    val catalogFilter: ProductCategory? = null,
    val searchQuery: String = "",
    val isAdmin: Boolean = false,
)

@OptIn(ExperimentalUuidApi::class)
data class SelectedPosItem(
    val instanceId: String = Uuid.random().toString(),
    var lassoItem: LassoItem,
    var quantity: Int = 1,
    var price: Double = lassoItem.price.toDoubleOrNull() ?: 0.0
)