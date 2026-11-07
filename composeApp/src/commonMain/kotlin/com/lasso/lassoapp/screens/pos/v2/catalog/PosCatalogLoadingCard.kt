package com.lasso.lassoapp.screens.pos.v2.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp

@Composable
internal fun PosCatalogLoadingCard(brush: Brush, modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    val nameHeight = with(density) { MaterialTheme.typography.titleMedium.lineHeight.toDp() }
    val categoryHeight = with(density) { MaterialTheme.typography.labelSmall.lineHeight.toDp() }
    val priceHeight = with(density) { MaterialTheme.typography.titleLarge.lineHeight.toDp() }
    // Preserve the item card's text heights, spacing, and vertical padding.
    val cardHeight = nameHeight + categoryHeight + priceHeight + 36.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(cardHeight)
            .background(brush, RoundedCornerShape(12.dp))
            .clearAndSetSemantics {},
    )
}
