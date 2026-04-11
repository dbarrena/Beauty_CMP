package com.lasso.lassoapp.screens.home.v2

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.ui.theme.LassoOnPrimary
import com.lasso.lassoapp.ui.theme.LassoTextMuted

@Composable
fun HomeQuickActionsRow(
    onNuevaVenta: () -> Unit,
    onAgendar: () -> Unit,
    onVentas: () -> Unit,
    onCorteCaja: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Acciones rápidas",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            QuickActionCell(
                label = "Nueva venta",
                icon = Icons.Default.Add,
                containerColor = MaterialTheme.colorScheme.primary,
                iconTint = LassoOnPrimary,
                labelColor = LassoOnPrimary,
                isPrimaryAction = true,
                onClick = onNuevaVenta,
                modifier = Modifier.weight(1f),
            )
            QuickActionCell(
                label = "Agendar",
                icon = Icons.Default.CalendarMonth,
                containerColor = MaterialTheme.colorScheme.surface,
                iconTint = MaterialTheme.colorScheme.onSurface,
                labelColor = MaterialTheme.colorScheme.onSurface,
                isPrimaryAction = false,
                onClick = onAgendar,
                modifier = Modifier.weight(1f),
            )
            QuickActionCell(
                label = "Ventas",
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                containerColor = MaterialTheme.colorScheme.surface,
                iconTint = MaterialTheme.colorScheme.onSurface,
                labelColor = MaterialTheme.colorScheme.onSurface,
                isPrimaryAction = false,
                onClick = onVentas,
                modifier = Modifier.weight(1f),
            )
            QuickActionCell(
                label = "Corte de caja",
                icon = Icons.Default.AccountBalanceWallet,
                containerColor = MaterialTheme.colorScheme.surface,
                iconTint = MaterialTheme.colorScheme.onSurface,
                labelColor = MaterialTheme.colorScheme.onSurface,
                isPrimaryAction = false,
                onClick = onCorteCaja,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun QuickActionCell(
    label: String,
    icon: ImageVector,
    containerColor: Color,
    iconTint: Color,
    labelColor: Color,
    isPrimaryAction: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(12.dp))
                .background(containerColor)
                .clickable(onClick = onClick)
                .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconTint,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isPrimaryAction) labelColor else LassoTextMuted,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
    }
}
