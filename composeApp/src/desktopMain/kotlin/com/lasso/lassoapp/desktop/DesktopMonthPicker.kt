package com.lasso.lassoapp.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.ui.theme.*
import com.lasso.lassoapp.utils.formatMonthYearSpanish
import kotlinx.datetime.*

/** A Monday-first month grid; dates outside the displayed month stay selectable. */
internal fun monthGridDates(month: LocalDate): List<LocalDate> {
    val first = LocalDate(month.year, month.month, 1)
    val start = first.minus(first.dayOfWeek.ordinal, DateTimeUnit.DAY)
    return List(42) { start.plus(it, DateTimeUnit.DAY) }
}

@Composable
internal fun DesktopMonthPicker(selected: LocalDate, today: LocalDate, enabled: Boolean, onDateSelected: (LocalDate) -> Unit) {
    var month by remember { mutableStateOf(LocalDate(selected.year, selected.month, 1)) }
    LaunchedEffect(selected) { month = LocalDate(selected.year, selected.month, 1) }
    Column {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(month.formatMonthYearSpanish(), style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            IconButton(onClick = { month = month.minus(1, DateTimeUnit.MONTH) }, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.ChevronLeft, "Mes anterior", modifier = Modifier.size(18.dp))
            }
            IconButton(onClick = { month = month.plus(1, DateTimeUnit.MONTH) }, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.ChevronRight, "Mes siguiente", modifier = Modifier.size(18.dp))
            }
        }
        Spacer(Modifier.height(12.dp))
        Row {
            listOf("L", "M", "M", "J", "V", "S", "D").forEach { day ->
                Box(Modifier.weight(1f).height(28.dp), contentAlignment = Alignment.Center) {
                    Text(day, style = MaterialTheme.typography.labelSmall, color = LassoGray)
                }
            }
        }
        monthGridDates(month).chunked(7).forEach { week -> Row {
            week.forEach { date ->
                val background = when (date) { selected -> LassoPrimary; today -> LassoPrimary.copy(alpha = .12f); else -> Color.Transparent }
                Box(Modifier.weight(1f).aspectRatio(1f).padding(1.dp).clip(CircleShape).background(background)
                    .clickable(enabled = enabled) { onDateSelected(date) }, contentAlignment = Alignment.Center) {
                    Text(date.day.toString(), style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (date == selected || date == today) FontWeight.Bold else FontWeight.Normal,
                        color = when { date == selected -> Color.White; date.month != month.month -> LassoTextMuted; else -> LassoTextPrimary })
                }
            }
        } }
    }
}
