package com.lasso.lassoapp.screens.pos.v2.bottom_sheet.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.screens.pos.SelectedPosItem
import com.lasso.lassoapp.screens.pos.v2.toPosMoneyString
import lassoapp.composeapp.generated.resources.Res
import lassoapp.composeapp.generated.resources.trash_icon
import org.jetbrains.compose.resources.painterResource

@Composable
fun PosCartLine(
    item: SelectedPosItem,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val lineTotal = item.price * item.quantity
    val qtyLabel = if (item.quantity > 1) " × ${item.quantity}" else ""
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "${item.lassoItem.name}$qtyLabel",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight(600),
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "$${lineTotal.toPosMoneyString()}",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight(700),
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )
            IconButton(
                modifier = Modifier.size(28.dp),
                onClick = onRemove
            ) {
                Icon(
                    painter = painterResource(Res.drawable.trash_icon),
                    contentDescription = "Eliminar",
                    tint = Color.Unspecified
                )
            }
        }
    }
}
