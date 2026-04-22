package com.lasso.lassoapp.screens.product_catalog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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

    if (state.value.productsServices.isDialogDisplayed) {
        com.lasso.lassoapp.screens.product_service.dialog.ProductDialogScreen(
            lassoItem = state.value.productsServices.selectedItem,
            onDismiss = viewModel::hideProductServiceDialog,
        ) {
            viewModel.onProductServiceSaved()
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
