package com.lasso.lassoapp.screens.product_catalog.categories

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.model.ProductCategory
import com.lasso.lassoapp.screens.product_catalog.CategoriesTabState
import com.lasso.lassoapp.screens.utils.FullScreenLoading

@Composable
fun CategoriesTabContent(
    state: CategoriesTabState,
    onAdd: () -> Unit,
    onItemClick: (ProductCategory) -> Unit,
) {
    if (state.isLoading) {
        FullScreenLoading()
        return
    }

    Button(
        onClick = onAdd,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Nueva categoria",
        )
        Text(
            text = "Nueva Categoria",
            modifier = Modifier.padding(start = 8.dp),
            style = MaterialTheme.typography.titleMedium,
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(state.availableItems) { category ->
            val itemCount = state.categoryItemCounts[category.name.trim().lowercase()] ?: 0
            CategoryCard(
                name = category.name,
                itemCount = itemCount,
                onClick = { onItemClick(category) },
            )
        }
    }
}

@Composable
private fun CategoryCard(
    name: String,
    itemCount: Int,
    onClick: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "$itemCount items",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 12.dp),
            )
        }
    }
}
