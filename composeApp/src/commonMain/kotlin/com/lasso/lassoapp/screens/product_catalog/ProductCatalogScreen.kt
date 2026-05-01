package com.lasso.lassoapp.screens.product_catalog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.lasso.lassoapp.screens.product_catalog.dialog.delete.DeleteProductConfirmationDialog
import com.lasso.lassoapp.screens.product_catalog.dialog.edit.EditProductServiceDialog
import com.lasso.lassoapp.screens.product_catalog.dialog.new.NewProductServiceDialog
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProductCatalogScreen() {
    val viewModel = koinViewModel<ProductCatalogViewModel>()
    val state = viewModel.state.collectAsState()

    ProductCatalogContent(
        state = state.value,
        onSelectTab = viewModel::setSelectedTab,
        onProductsSearchChange = viewModel::onProductServicesSearchChange,
        onAddProductService = { viewModel.showProductServiceDialog() },
        onProductServiceClick = viewModel::showProductServiceDialog,
        onAddCategory = { viewModel.showCategoryDialog() },
        onCategoryClick = viewModel::showCategoryDialog,
    )

    if (state.value.productsServices.isDeleteConfirmationDisplayed) {
        DeleteProductConfirmationDialog(
            onDismiss = viewModel::hideDeleteProductServiceConfirmation,
            onConfirm = viewModel::deleteProductService
        )
    }

    if (state.value.productsServices.isDialogDisplayed) {
        val selectedItem = state.value.productsServices.selectedItem
        if (selectedItem != null) {
            EditProductServiceDialog(
                lassoItem = selectedItem,
                onDismiss = viewModel::hideProductServiceDialog,
                onResult = { viewModel.onProductServiceSaved() }
            )
        } else {
            NewProductServiceDialog(
                onDismiss = viewModel::hideProductServiceDialog,
                onResult = { viewModel.onProductServiceSaved() }
            )
        }
    }

    if (state.value.categories.isDialogDisplayed) {
        com.lasso.lassoapp.screens.product_categories.dialog.ProductCategoryModal(
            productCategory = state.value.categories.selectedItem,
            onDismiss = viewModel::hideCategoryDialog,
        ) {
            viewModel.onCategorySaved()
        }
    }
}
