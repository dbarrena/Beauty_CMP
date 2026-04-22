package com.lasso.lassoapp.screens.product_catalog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.model.LassoItem
import com.lasso.lassoapp.model.ProductCategory
import com.lasso.lassoapp.screens.product_catalog.categories.CategoriesTabContent
import com.lasso.lassoapp.screens.product_catalog.products_services.ProductServicesTabContent
import com.lasso.lassoapp.screens.product_catalog.tabs.ProductCatalogTabs

@Composable
fun ProductCatalogContent(
    state: ProductCatalogState,
    onSelectTab: (ProductCatalogTab) -> Unit,
    onProductsSearchChange: (String) -> Unit,
    onAddProductService: () -> Unit,
    onProductServiceClick: (LassoItem) -> Unit,
    onAddCategory: () -> Unit,
    onCategoryClick: (ProductCategory) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(
            text = "Productos y Servicios",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            ),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = "Administra tu catalogo",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
        )

        ProductCatalogTabs(
            selectedTab = state.selectedTab,
            onSelectTab = onSelectTab,
        )

        when (state.selectedTab) {
            ProductCatalogTab.PRODUCTS_SERVICES -> {
                ProductServicesTabContent(
                    state = state.productsServices,
                    onSearchChange = onProductsSearchChange,
                    onAdd = onAddProductService,
                    onItemClick = onProductServiceClick,
                )
            }

            ProductCatalogTab.CATEGORIES -> {
                CategoriesTabContent(
                    state = state.categories,
                    onAdd = onAddCategory,
                    onItemClick = onCategoryClick,
                )
            }
        }
    }
}
