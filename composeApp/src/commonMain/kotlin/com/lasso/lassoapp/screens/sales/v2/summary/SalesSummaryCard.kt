package com.lasso.lassoapp.screens.sales.v2.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.screens.sales.v2.formatMoney
import com.lasso.lassoapp.screens.utils.AdaptiveEqualPair
import com.lasso.lassoapp.screens.utils.FittedAmountText
import com.lasso.lassoapp.screens.utils.labelWidth
import com.lasso.lassoapp.ui.theme.LassoTextPlaceholder

@Composable
fun SalesSummaryCard(
    total: Double,
    discounts: Double,
    transactionCount: Int,
    modifier: Modifier = Modifier,
) {
    val primary = MaterialTheme.colorScheme.primary
    val minColumnWidth =
        maxOf(
            labelWidth("Total de ventas", MaterialTheme.typography.bodySmall),
            labelWidth("Transacciones", MaterialTheme.typography.bodySmall),
        )
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier =
                Modifier.background(
                        brush =
                            Brush.linearGradient(
                                colors =
                                    listOf(primary.copy(alpha = 0.12f), primary.copy(alpha = 0.05f))
                            )
                    )
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                    .padding(16.dp)
                    .fillMaxWidth()
        ) {
            Column {
                AdaptiveEqualPair(
                    minFieldWidth = minColumnWidth,
                    first = { columnModifier ->
                        Column(columnModifier) {
                            Text(
                                "Total de ventas",
                                style = MaterialTheme.typography.bodySmall,
                                color = LassoTextPlaceholder,
                            )
                            FittedAmountText(
                                text = formatMoney(total),
                                style =
                                    MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                color = primary,
                            )
                        }
                    },
                    second = { columnModifier ->
                        Column(columnModifier, horizontalAlignment = Alignment.End) {
                            Text(
                                "Transacciones",
                                style = MaterialTheme.typography.bodySmall,
                                color = LassoTextPlaceholder,
                            )
                            FittedAmountText(
                                text = transactionCount.toString(),
                                style =
                                    MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    },
                )
                if (discounts > 0.0) {
                    Column {
                        Text(
                            text = "Descuentos",
                            style = MaterialTheme.typography.bodySmall,
                            color = LassoTextPlaceholder,
                        )
                        Text(
                            text = formatMoney(discounts),
                            style =
                                MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    }
                }
            }
        }
    }
}
