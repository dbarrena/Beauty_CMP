package com.lasso.lassoapp.screens.home.v2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.ui.theme.LassoOnPrimary
import com.lasso.lassoapp.ui.theme.LassoPrimary
import com.lasso.lassoapp.ui.theme.LassoPrimaryDark
import com.lasso.lassoapp.ui.theme.LassoSecondary
import com.lasso.lassoapp.ui.theme.LassoTextMuted
import com.lasso.lassoapp.ui.theme.LassoTertiary

@Composable
fun HomeMetricsCards(
    todayEarningsFormatted: String,
    todayTransactionCount: Int,
    weekEarningsFormatted: String,
    appointmentsToday: Int,
    appointmentsPending: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MetricCardGradientPrimary(
                label = "Ventas hoy",
                value = todayEarningsFormatted,
                icon = Icons.Default.AttachMoney,
                modifier = Modifier.weight(1f),
            )
            MetricCardLight(
                label = "Citas",
                value = appointmentsToday.toString(),
                sublabel = null,
                icon = Icons.Default.CalendarMonth,
                iconTint = LassoPrimary,
                iconBackground = LassoPrimary.copy(alpha = 0.1f),
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MetricCardLight(
                label = "Transacciones",
                value = todayTransactionCount.toString(),
                sublabel = null,
                icon = Icons.Default.ShoppingCart,
                iconTint = LassoTertiary,
                iconBackground = LassoTertiary.copy(alpha = 0.1f),
                modifier = Modifier.weight(1f),
            )
            MetricCardGradientWarm(
                label = "Semana",
                value = weekEarningsFormatted,
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun MetricCardGradientPrimary(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(
                        brush =
                            Brush.verticalGradient(
                                colors = listOf(LassoPrimary, LassoPrimaryDark),
                            ),
                    )
                    .padding(16.dp),
        ) {
            Column(
                modifier = Modifier
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = LassoOnPrimary,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = LassoOnPrimary.copy(alpha = 0.95f),
                    modifier = Modifier.padding(top = 28.dp),
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = LassoOnPrimary,
                )
            }
        }
    }
}

@Composable
private fun MetricCardGradientWarm(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(
                        brush =
                            Brush.verticalGradient(
                                colors = listOf(LassoTertiary, LassoSecondary),
                            ),
                    )
                    .padding(16.dp),
        ) {
            Column(
                modifier = Modifier
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = LassoOnPrimary,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = LassoOnPrimary.copy(alpha = 0.95f),
                    modifier = Modifier.padding(top = 28.dp),
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = LassoOnPrimary,
                )
            }
        }
    }
}

@Composable
private fun MetricCardLight(
    label: String,
    value: String,
    sublabel: String?,
    icon: ImageVector,
    iconTint: Color,
    iconBackground: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier =
                    Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(iconBackground),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = LassoTextMuted,
                modifier = Modifier.padding(top = 28.dp),
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (sublabel != null) {
                Text(
                    text = sublabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = LassoTextMuted,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}
