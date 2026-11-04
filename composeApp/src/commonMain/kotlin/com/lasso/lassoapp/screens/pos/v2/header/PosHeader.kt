package com.lasso.lassoapp.screens.pos.v2.header

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.screens.utils.AdaptiveActionRow
import com.lasso.lassoapp.screens.utils.labelWidth

@Composable
fun PosHeader(onNuevoClick: () -> Unit, isAdmin: Boolean, modifier: Modifier = Modifier) {
    val titleStyle =
        MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold)
    val buttonStyle = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight(600))
    val titleWidth = labelWidth("Punto de Venta", titleStyle)
    val buttonWidth = labelWidth("Nuevo", buttonStyle) + 48.dp
    val heading: @Composable (Modifier) -> Unit = { headingModifier ->
        Column(headingModifier) {
            Text(
                text = "Punto de Venta",
                style =
                    MaterialTheme.typography.titleLarge.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight(700),
                    ),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "Registra una venta",
                style =
                    MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight(400),
                    ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
    if (isAdmin) {
        AdaptiveActionRow(
            modifier = modifier,
            minFieldWidth = titleWidth,
            secondMinFieldWidth = buttonWidth,
            verticalAlignment = Alignment.CenterVertically,
            first = heading,
            second = { buttonModifier ->
                OutlinedButton(
                    modifier = buttonModifier,
                    onClick = onNuevoClick,
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    colors =
                        ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                ) {
                    Text(
                        text = "Nuevo",
                        maxLines = 1,
                        softWrap = false,
                        style =
                            MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight(600)),
                    )
                }
            },
        )
    } else {
        heading(modifier.fillMaxWidth())
    }
}
