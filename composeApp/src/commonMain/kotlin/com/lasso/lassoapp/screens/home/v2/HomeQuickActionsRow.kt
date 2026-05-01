package com.lasso.lassoapp.screens.home.v2

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.ui.theme.LassoOnPrimary
import com.lasso.lassoapp.ui.theme.LassoTextMuted
import com.lasso.lassoapp.ui.theme.LassoTextPrimary

@Composable
fun HomeQuickActionsRow(
    onNuevaVenta: () -> Unit,
    onAgendar: () -> Unit,
    onVentas: () -> Unit,
    onCorteCaja: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
        Text(
            text = "Acciones rápidas",
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight(600),
            ),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
                .horizontalScroll(scrollState),
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
            )
            QuickActionCell(
                label = "Agendar",
                icon = Icons.Default.CalendarMonth,
                containerColor = MaterialTheme.colorScheme.surface,
                iconTint = MaterialTheme.colorScheme.onSurface,
                labelColor = MaterialTheme.colorScheme.onSurface,
                isPrimaryAction = false,
                onClick = onAgendar,
            )
            QuickActionCell(
                label = "Ventas",
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                containerColor = MaterialTheme.colorScheme.surface,
                iconTint = MaterialTheme.colorScheme.onSurface,
                labelColor = MaterialTheme.colorScheme.onSurface,
                isPrimaryAction = false,
                onClick = onVentas,
            )
            QuickActionCell(
                label = "Corte de caja",
                icon = Icons.Default.AccountBalanceWallet,
                containerColor = MaterialTheme.colorScheme.surface,
                iconTint = MaterialTheme.colorScheme.onSurface,
                labelColor = MaterialTheme.colorScheme.onSurface,
                isPrimaryAction = false,
                onClick = onCorteCaja,
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
                //.shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .background(containerColor)
                .clickable(onClick = onClick)
                .padding(vertical = 8.dp, horizontal = 4.dp)
                .width(75.dp)
                .fillMaxHeight(),
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
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight(500),
            ),
            color = if (isPrimaryAction) labelColor else LassoTextPrimary,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
    }
}
