package com.lasso.lassoapp.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.ui.theme.LassoPrimary

/** Persistent application navigation, independent of the calendar's month and team sidebar. */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun DesktopNavigationRail() {
    Column(
        Modifier.width(64.dp).fillMaxHeight().background(Color(0xFFEEF0F2)).padding(top = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TooltipBox(
            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
            tooltip = { PlainTooltip { Text("Calendario") } },
            state = rememberTooltipState(),
        ) {
        Box(
            Modifier.size(44.dp).clip(RoundedCornerShape(12.dp))
                .background(LassoPrimary.copy(alpha = .12f))
                .selectable(selected = true, role = Role.Tab, onClick = {}),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Default.CalendarMonth, "Calendario", tint = LassoPrimary, modifier = Modifier.size(24.dp))
        }
        }
    }
}
