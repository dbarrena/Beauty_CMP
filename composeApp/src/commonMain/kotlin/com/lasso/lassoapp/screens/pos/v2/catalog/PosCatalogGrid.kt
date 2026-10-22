package com.lasso.lassoapp.screens.pos.v2.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.model.LassoItem
import com.lasso.lassoapp.screens.pos.v2.toPosMoneyString
import com.lasso.lassoapp.screens.utils.contentPlacement
import com.lasso.lassoapp.screens.utils.rememberMeasuredContent

@Composable
fun PosCatalogGrid(
    isLoading: Boolean,
    items: List<LassoItem>,
    onItemClick: (LassoItem) -> Unit,
    modifier: Modifier = Modifier,
    header: (@Composable () -> Unit)? = null,
    state: LazyGridState = rememberLazyGridState(),
) {
    val measurer = rememberTextMeasurer()
    val priceStyle =
        MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold)
    val density = LocalDensity.current
    val resolver = LocalFontFamilyResolver.current
    val widestPricePx =
        remember(items, priceStyle, density, resolver, measurer) {
            items.maxOfOrNull {
                measurer
                    .measure(
                        "$${it.price.toDoubleOrNull()?.toPosMoneyString() ?: it.price}",
                        priceStyle,
                        softWrap = false,
                    )
                    .size
                    .width
            } ?: 0
        }
    val minCellWidth = maxOf(140.dp, with(LocalDensity.current) { widestPricePx.toDp() } + 24.dp)
    val measuredHeader = rememberMeasuredContent { header?.invoke() }
    BoxWithConstraints(modifier = modifier) {
        val pinned =
            contentPlacement(
                    constraints.maxHeight,
                    measuredHeader.size.height,
                    0,
                    with(density) { 96.dp.roundToPx() },
                )
                .pinHeader
        LazyVerticalGrid(
            state = state,
            columns = GridCells.Fixed(if ((maxWidth - 12.dp) / 2 >= minCellWidth) 2 else 1),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 8.dp),
        ) {
            if (header != null) {
                if (pinned)
                    stickyHeader(key = "catalog-header", contentType = "header") {
                        Box(
                            Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)
                        ) {
                            measuredHeader.content()
                        }
                    }
                else
                    item(
                        key = "catalog-header",
                        contentType = "header",
                        span = { GridItemSpan(maxLineSpan) },
                    ) {
                        Box(
                            Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)
                        ) {
                            measuredHeader.content()
                        }
                    }
            }
            when {
                isLoading ->
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(
                            Modifier.fillMaxSize().padding(24.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                items.isEmpty() ->
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            text = "No hay artículos que coincidan con tu búsqueda",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(24.dp),
                        )
                    }
                else -> {
                    items(items, key = { "${it.id}-${it.type}-${it.name}" }) { item ->
                        PosCatalogItemCard(item = item, onClick = { onItemClick(item) })
                    }

                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }
    }
}
