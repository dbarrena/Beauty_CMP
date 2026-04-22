package com.lasso.lassoapp.screens.product_catalog.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.screens.product_catalog.ProductCatalogTab

@Composable
fun ProductCatalogTabs(
    selectedTab: ProductCatalogTab,
    onSelectTab: (ProductCatalogTab) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TabPillButton(
            text = "Productos y Servicios",
            selected = selectedTab == ProductCatalogTab.PRODUCTS_SERVICES,
            modifier = Modifier.weight(1f).fillMaxHeight(),
        ) {
            onSelectTab(ProductCatalogTab.PRODUCTS_SERVICES)
        }
        TabPillButton(
            text = "Categorías",
            selected = selectedTab == ProductCatalogTab.CATEGORIES,
            modifier = Modifier.weight(1f).fillMaxHeight(),
        ) {
            onSelectTab(ProductCatalogTab.CATEGORIES)
        }
    }
}

@Composable
private fun TabPillButton(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 44.dp),
        shape = RoundedCornerShape(100.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (selected) 6.dp else 0.dp,
            pressedElevation = if (selected) 2.dp else 0.dp,
        ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
            textAlign = TextAlign.Center,
        )
    }
}
