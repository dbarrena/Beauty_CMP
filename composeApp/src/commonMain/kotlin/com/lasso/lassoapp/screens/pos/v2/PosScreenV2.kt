package com.lasso.lassoapp.screens.pos.v2

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults.shape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CheckboxDefaults.colors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.screens.pos.DisplaySaleAnimation
import com.lasso.lassoapp.screens.pos.PosViewModel
import com.lasso.lassoapp.screens.pos.checkout.CheckoutDialogScreen
import com.lasso.lassoapp.screens.pos.edit_dialog.PosEditDialogScreen
import com.lasso.lassoapp.screens.product_service.dialog.ProductDialogScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PosScreenV2() {
    val viewModel = koinViewModel<PosViewModel>()
    val state = viewModel.state.collectAsState()

    val isCheckoutDialogDisplayed = remember { mutableStateOf(false) }
    val isSaleRegisteredDisplayed = remember { mutableStateOf(false) }
    val isNuevoDialogDisplayed = remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(Modifier.fillMaxSize()) {
            PosHeader()
            PosSearchFilterBar(
                searchQuery = state.value.searchQuery,
                onSearchQueryChange = viewModel::setSearchQuery,
                catalogFilter = state.value.catalogFilter,
                onCatalogFilterChange = viewModel::setCatalogFilter,
                onNuevoClick = { isNuevoDialogDisplayed.value = true },
            )
            PosCatalogGrid(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                isLoading = state.value.isAvailableItemsLoading,
                items = viewModel.filteredCatalogItems(),
                onItemClick = { viewModel.addSelectedItem(it) },
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 50.dp,
                        spotColor = Color(0x40000000),
                        ambientColor = Color(0x40000000)
                    )
                    .border(
                        width = 0.75394.dp,
                        color = Color(0x1A353D3C),
                        shape = RoundedCornerShape(size = 24.dp)
                    )
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(size = 24.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            ) {
                PosCart(
                    modifier = Modifier.fillMaxWidth(),
                    selectedItems = state.value.selectedPosItems.toList(),
                    onClear = { viewModel.restartPos() },
                    onLineClick = { viewModel.selectItemToEdit(it) },
                    onRemove = { viewModel.removeItem(it) },
                )
                PosCheckoutBar(
                    totalPrice = state.value.totalPrice,
                    onCheckout = { isCheckoutDialogDisplayed.value = true },
                    enabled = state.value.selectedPosItems.isNotEmpty(),
                )
            }
        }
    }

    if (isCheckoutDialogDisplayed.value) {
        CheckoutDialogScreen(
            items = state.value.selectedPosItems,
            totalPrice = state.value.totalPrice,
            onDismiss = { isSuccess ->
                isCheckoutDialogDisplayed.value = false
                if (isSuccess) {
                    isSaleRegisteredDisplayed.value = true
                    viewModel.restartPos()
                }
            },
        )
    }

    if (isSaleRegisteredDisplayed.value) {
        DisplaySaleAnimation { isSaleRegisteredDisplayed.value = false }
    }

    state.value.selectedItemToEdit?.let { item ->
        PosEditDialogScreen(item) { updated ->
            updated?.let { viewModel.updateSelectedItem(it) }
            viewModel.restartSelectedItemToEdit()
        }
    }

    if (isNuevoDialogDisplayed.value) {
        ProductDialogScreen(
            lassoItem = null,
            onDismiss = { isNuevoDialogDisplayed.value = false },
        ) { product ->
            isNuevoDialogDisplayed.value = false
            viewModel.addSelectedItem(product)
            viewModel.getAvailableItems()
        }
    }
}
