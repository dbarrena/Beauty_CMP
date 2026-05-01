package com.lasso.lassoapp.screens.product_catalog

import com.lasso.lassoapp.model.LassoItem
import com.lasso.lassoapp.model.ProductCategory

enum class ProductCatalogTab {
    PRODUCTS_SERVICES,
    CATEGORIES
}

data class ProductCatalogState(
    val selectedTab: ProductCatalogTab = ProductCatalogTab.PRODUCTS_SERVICES,
    val productsServices: ProductServicesTabState = ProductServicesTabState(),
    val categories: CategoriesTabState = CategoriesTabState(),
)

data class ProductServicesTabState(
    val availableItems: List<LassoItem> = emptyList(),
    val filteredItems: List<LassoItem> = emptyList(),
    val searchQuery: String = "",
    val selectedItem: LassoItem? = null,
    val isDialogDisplayed: Boolean = false,
    val isDeleteConfirmationDisplayed: Boolean = false,
    val isLoading: Boolean = false,
)

data class CategoriesTabState(
    val availableItems: List<ProductCategory> = emptyList(),
    val categoryItemCounts: Map<String, Int> = emptyMap(),
    val selectedItem: ProductCategory? = null,
    val isDialogDisplayed: Boolean = false,
    val isLoading: Boolean = false,
)
