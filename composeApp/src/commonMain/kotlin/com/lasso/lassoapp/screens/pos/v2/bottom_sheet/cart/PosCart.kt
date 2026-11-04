package com.lasso.lassoapp.screens.pos.v2.bottom_sheet.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.screens.pos.SelectedPosItem

private enum class CartSlot {
    HeaderMeasure,
    RowMeasure,
    Content,
}

@Composable
fun PosCart(
    selectedItems: List<SelectedPosItem>,
    onClear: () -> Unit,
    onLineClick: (SelectedPosItem) -> Unit,
    onRemove: (SelectedPosItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(selectedItems.size) {
        if (selectedItems.isNotEmpty()) listState.animateScrollToItem(selectedItems.lastIndex)
    }
    // Measure a real two-line preview without the viewport's height constraint. Unplaced probes
    // have no visible semantics; they avoid guessed font metrics and resize/line-count feedback.
    SubcomposeLayout(modifier.fillMaxWidth()) { constraints ->
        val natural = Constraints.fixedWidth(constraints.maxWidth)
        val header =
            subcompose(CartSlot.HeaderMeasure) {
                    Box(Modifier.clearAndSetSemantics {}) {
                        CartHeader(selectedItems.size, onClear)
                    }
                }
                .single()
                .measure(natural)
        val rowHeight =
            selectedItems.lastOrNull()?.let { item ->
                subcompose(CartSlot.RowMeasure) {
                        Box(Modifier.clearAndSetSemantics {}) {
                            PosCartLine(item, {}, {}, nameMaxLines = 2)
                        }
                    }
                    .single()
                    .measure(natural)
                    .height
            } ?: 0
        val gap = 16.dp.roundToPx()
        val height =
            constraints.constrainHeight(maxOf(140.dp.roundToPx(), header.height + gap + rowHeight))
        val nameLines = if (height - header.height - gap >= rowHeight) 2 else 1
        val content =
            subcompose(CartSlot.Content) {
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        CartHeader(selectedItems.size, onClear)
                        LazyColumn(
                            Modifier.weight(1f).fillMaxWidth(),
                            state = listState,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(selectedItems, key = { it.instanceId }) { item ->
                                PosCartLine(
                                    item,
                                    { onLineClick(item) },
                                    { onRemove(item) },
                                    nameMaxLines = nameLines,
                                )
                            }
                        }
                    }
                }
                .single()
                .measure(Constraints.fixed(constraints.maxWidth, height))
        layout(content.width, height) { content.placeRelative(0, 0) }
    }
}

@Composable
private fun CartHeader(count: Int, onClear: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            "Carrito ($count)",
            modifier = Modifier.weight(1f),
            style =
                MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                ),
        )
        TextButton(onClick = onClear) {
            Text(
                "Limpiar",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.tertiary,
            )
        }
    }
}
