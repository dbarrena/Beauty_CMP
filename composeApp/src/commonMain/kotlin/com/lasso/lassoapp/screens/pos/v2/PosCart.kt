package com.lasso.lassoapp.screens.pos.v2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.screens.pos.SelectedPosItem

@Composable
fun PosCart(
    selectedItems: List<SelectedPosItem>,
    onClear: () -> Unit,
    onLineClick: (SelectedPosItem) -> Unit,
    onRemove: (SelectedPosItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 320.dp)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Carrito (${selectedItems.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                TextButton(
                    onClick = onClear,
                    enabled = selectedItems.isNotEmpty(),
                ) {
                    Text(
                        text = "Limpiar",
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                }
            }
        }
        items(selectedItems, key = { it.instanceId }) { line ->
            PosCartLine(
                item = line,
                onClick = { onLineClick(line) },
                onRemove = { onRemove(line) },
            )
        }
    }
}
