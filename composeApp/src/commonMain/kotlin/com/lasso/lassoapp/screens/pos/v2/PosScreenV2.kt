package com.lasso.lassoapp.screens.pos.v2

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.screens.pos.PosViewModel
import com.lasso.lassoapp.screens.pos.edit_dialog.PosEditDialogScreen
import com.lasso.lassoapp.screens.pos.v2.bottom_sheet.PosBottomSheet
import com.lasso.lassoapp.screens.pos.v2.catalog.PosCatalogGrid
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.CheckoutDialogScreenV2
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.CheckoutDialogViewModelV2
import com.lasso.lassoapp.screens.pos.v2.header.PosHeader
import com.lasso.lassoapp.screens.pos.v2.search_bar.PosSearchFilterBar
import com.lasso.lassoapp.screens.product_catalog.dialog.new.NewProductServiceDialog
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PosScreenV2() {
    val viewModel = koinViewModel<PosViewModel>()
    val state = viewModel.state.collectAsState()
    val checkoutViewModel = koinViewModel<CheckoutDialogViewModelV2>()
    val checkoutState by checkoutViewModel.state.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val isCheckoutDialogDisplayed = remember { mutableStateOf(false) }
    val isNuevoDialogDisplayed = remember { mutableStateOf(false) }

    val gridState = rememberLazyGridState()
    val header: @Composable () -> Unit = {
        Column {
            PosHeader(
                modifier = Modifier.padding(top = 16.dp),
                isAdmin = state.value.isAdmin,
                onNuevoClick = { isNuevoDialogDisplayed.value = true },
            )
            Spacer(Modifier.height(8.dp))
            PosSearchFilterBar(
                state.value.searchQuery,
                viewModel::setSearchQuery,
                state.value.categories,
                state.value.catalogFilter,
                viewModel::setCatalogFilter,
            )
        }
    }
    LaunchedEffect(checkoutState.completion?.id) {
        checkoutState.completion?.let { completed ->
            viewModel.removeSubmittedItems(completed.items)
            checkoutViewModel.acknowledgeCompletion(completed.id, isCheckoutDialogDisplayed.value)
        }
    }
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val maxCartHeight = maxHeight * .65f
            Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                PosCatalogGrid(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    state = gridState,
                    header = header,
                    isLoading = state.value.isAvailableItemsLoading,
                    items = viewModel.filteredCatalogItems(),
                    onItemClick = { item ->
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        viewModel.addSelectedItem(item)
                    },
                )
                Column(Modifier.fillMaxWidth().animateContentSize(animationSpec = tween(280))) {
                    if (state.value.selectedPosItems.isNotEmpty())
                        PosBottomSheet(
                            modifier = Modifier.heightIn(max = maxCartHeight),
                            selectedItems = state.value.selectedPosItems.toList(),
                            totalPrice = state.value.totalPrice,
                            onClear = viewModel::restartPos,
                            onLineClick = viewModel::selectItemToEdit,
                            onRemove = viewModel::removeItem,
                            onCheckout = { isCheckoutDialogDisplayed.value = true },
                            enabled = true,
                        )
                }
            }
        }
    }
    if (isCheckoutDialogDisplayed.value) {
        CheckoutDialogScreenV2(
            items = state.value.selectedPosItems,
            totalPrice = state.value.totalPrice,
            onDismiss = { _ ->
                isCheckoutDialogDisplayed.value = false
                checkoutViewModel.dismissCheckout()
            },
        )
    }

    state.value.selectedItemToEdit?.let { item ->
        PosEditDialogScreen(item) { updated ->
            updated?.let { viewModel.updateSelectedItem(it) }
            viewModel.restartSelectedItemToEdit()
        }
    }

    if (isNuevoDialogDisplayed.value) {
        NewProductServiceDialog(
            categories = state.value.categories,
            onDismiss = { isNuevoDialogDisplayed.value = false },
        ) { registeredItem ->
            isNuevoDialogDisplayed.value = false
            viewModel.addSelectedItem(registeredItem)
            viewModel.getAvailableItems()
        }
    }
}
