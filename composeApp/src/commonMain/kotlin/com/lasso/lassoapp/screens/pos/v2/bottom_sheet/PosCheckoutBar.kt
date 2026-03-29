package com.lasso.lassoapp.screens.pos.v2.bottom_sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.screens.pos.v2.toPosMoneyString

@Composable
fun PosCheckoutBar(
    totalPrice: Double,
    onCheckout: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        ColumnWithTotal(totalPrice)
        Button(
            onClick = onCheckout,
            enabled = enabled,
            modifier = Modifier.height(52.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
        ) {
            Text(
                text = "Cobrar",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun ColumnWithTotal(totalPrice: Double) {
    Column {
        Text(
            text = "Total:",
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight(400),
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "$${totalPrice.toPosMoneyString()}",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight(700),
            ),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
