package com.lasso.lassoapp.screens.pos.v2

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .heightIn(max = 320.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Carrito (${selectedItems.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight(700),
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    modifier = Modifier
                        .clickable {
                            onClear()
                        },
                    text = "Limpiar",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight(500),
                    ),
                    color = MaterialTheme.colorScheme.tertiary,
                )
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
