package com.lasso.lassoapp.screens.product_catalog.products_services

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.model.LassoItem
import com.lasso.lassoapp.screens.pos.search.SearchDialogItem
import com.lasso.lassoapp.screens.product_catalog.ProductServicesTabState
import com.lasso.lassoapp.screens.utils.FullScreenLoading

@Composable
fun ProductServicesTabContent(
    state: ProductServicesTabState,
    onSearchChange: (String) -> Unit,
    onAdd: () -> Unit,
    onItemClick: (LassoItem) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    if (state.isLoading) {
        FullScreenLoading()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProductCatalogSearchBar(
            searchQuery = state.searchQuery,
            onSearchQueryChange = onSearchChange,
        )

        Button(
            onClick = {
                keyboardController?.hide()
                onAdd()
            },
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Nuevo",
            )
            Text(
                text = "Nuevo",
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.titleMedium,
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            items(state.filteredItems) { item ->
                SearchDialogItem(item) { selectedItem ->
                    onItemClick(selectedItem)
                }
            }
        }
    }
}
