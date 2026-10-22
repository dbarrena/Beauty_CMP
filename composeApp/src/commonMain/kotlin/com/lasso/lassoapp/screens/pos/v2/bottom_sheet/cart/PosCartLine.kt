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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.screens.pos.SelectedPosItem
import com.lasso.lassoapp.screens.pos.v2.toPosMoneyString
import com.lasso.lassoapp.screens.utils.AdaptiveActionRow
import com.lasso.lassoapp.screens.utils.FittedAmountText
import com.lasso.lassoapp.screens.utils.labelWidth
import lassoapp.composeapp.generated.resources.Res
import lassoapp.composeapp.generated.resources.trash_icon
import org.jetbrains.compose.resources.painterResource

@Composable
fun PosCartLine(
    item: SelectedPosItem,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    nameMaxLines: Int = 2,
) {
    val lineTotal = item.price * item.quantity
    val qtyLabel = if (item.quantity > 1) " × ${item.quantity}" else ""
    val amountStyle =
        MaterialTheme.typography.titleSmall.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold)
    val amountWidth = labelWidth("$${lineTotal.toPosMoneyString()}", amountStyle)
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            // Match the trailing inset of the 24-dp icon inside its 48-dp touch target.
            modifier =
                Modifier.fillMaxWidth()
                    .padding(start = 20.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AdaptiveActionRow(
                modifier = Modifier.weight(1f),
                minFieldWidth = 100.dp,
                secondMinFieldWidth = amountWidth,
                verticalAlignment = Alignment.CenterVertically,
                first = { textModifier ->
                    Text(
                        text = "${item.lassoItem.name}$qtyLabel",
                        maxLines = nameMaxLines,
                        overflow = TextOverflow.Ellipsis,
                        style =
                            MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight(600),
                            ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = textModifier,
                    )
                },
                second = { textModifier ->
                    FittedAmountText(
                        text = "$${lineTotal.toPosMoneyString()}",
                        style =
                            MaterialTheme.typography.titleSmall.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight(700),
                            ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = textModifier,
                    )
                },
            )
            IconButton(modifier = Modifier.size(48.dp), onClick = onRemove) {
                Icon(
                    painter = painterResource(Res.drawable.trash_icon),
                    contentDescription = "Eliminar",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified,
                )
            }
        }
    }
}
