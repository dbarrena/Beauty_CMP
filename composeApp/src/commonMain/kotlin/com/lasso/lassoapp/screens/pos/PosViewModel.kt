package com.lasso.lassoapp.screens.pos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.model.LassoItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class PosViewModel(private val lassoApi: LassoApi) : ViewModel() {
    private val _state = MutableStateFlow(PosModelState())
    val state: StateFlow<PosModelState> = _state.asStateFlow()

    init {
        getAvailableItems()
    }

    fun getAvailableItems() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isAvailableItemsLoading = true)
            val products = lassoApi.getProducts()
            val services = lassoApi.getServices()
            val items: List<LassoItem> = products + services
            _state.value =
                _state.value.copy(availableItems = items, isAvailableItemsLoading = false)
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
        println("Removing item: $item")
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
}

data class PosModelState(
    val isAvailableItemsLoading: Boolean = false,
    val availableItems: List<LassoItem> = emptyList(),
    val selectedPosItems: MutableList<SelectedPosItem> = mutableListOf<SelectedPosItem>(),
    val totalPrice: Double = 0.0,
    val selectedItemToEdit: SelectedPosItem? = null
)

@OptIn(ExperimentalUuidApi::class)
data class SelectedPosItem(
    val instanceId: String = Uuid.random().toString(),
    var lassoItem: LassoItem,
    var quantity: Int = 1,
    var price: Double = lassoItem.price.toDoubleOrNull() ?: 0.0
)