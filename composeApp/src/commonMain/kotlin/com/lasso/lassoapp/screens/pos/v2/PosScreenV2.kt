package com.lasso.lassoapp.screens.pos.v2

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.screens.pos.DisplaySaleAnimation
import com.lasso.lassoapp.screens.pos.PosViewModel
import com.lasso.lassoapp.screens.pos.v2.checkout.CheckoutDialogScreenV2
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

    val cartTransitionMs = 280

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        ) {
            PosHeader(modifier = Modifier.padding(top = 16.dp))
            Spacer(Modifier.height(8.dp))

            PosSearchFilterBar(
                searchQuery = state.value.searchQuery,
                onSearchQueryChange = viewModel::setSearchQuery,
                catalogFilter = state.value.catalogFilter,
                onCatalogFilterChange = viewModel::setCatalogFilter,
                onNuevoClick = { isNuevoDialogDisplayed.value = true },
            )
            Spacer(Modifier.height(8.dp))

            PosCatalogGrid(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                isLoading = state.value.isAvailableItemsLoading,
                items = viewModel.filteredCatalogItems(),
                onItemClick = { viewModel.addSelectedItem(it) },
            )

            Column(
                Modifier
                    .fillMaxWidth()
                    .animateContentSize(animationSpec = tween(cartTransitionMs)),
            ) {
                if (state.value.selectedPosItems.isNotEmpty()) {
                    PosBottomSheet(
                        selectedItems = state.value.selectedPosItems.toList(),
                        totalPrice = state.value.totalPrice,
                        onClear = { viewModel.restartPos() },
                        onLineClick = { viewModel.selectItemToEdit(it) },
                        onRemove = { viewModel.removeItem(it) },
                        onCheckout = { isCheckoutDialogDisplayed.value = true },
                        enabled = state.value.selectedPosItems.isNotEmpty(),
                    )
                }
            }
        }
    }

    if (isCheckoutDialogDisplayed.value) {
        CheckoutDialogScreenV2(
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
