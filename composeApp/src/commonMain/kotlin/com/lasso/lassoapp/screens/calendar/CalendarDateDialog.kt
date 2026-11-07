package com.lasso.lassoapp.screens.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ViewColumn
import androidx.compose.material.icons.filled.ViewDay
import androidx.compose.material.icons.filled.ViewWeek
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.screens.utils.LassoDialogFrame
import com.lasso.lassoapp.utils.formatFullDateSpanish
import kotlinx.datetime.LocalDate

@Composable
internal fun CalendarDateDialog(
    selectedDate: LocalDate,
    viewMode: CalendarViewMode,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onViewModeSelected: (CalendarViewMode) -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedView by remember(viewMode) {
        mutableStateOf(CalendarViewOption.entries.first { it.viewMode == viewMode })
    }

    LassoDialogFrame(
        onDismiss = onDismiss,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Calendario",
                    modifier = Modifier.fillMaxWidth().align(Alignment.Center).padding(horizontal = 48.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF353D3C),
                )
                IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.CenterEnd)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            OutlinedCard(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Fecha seleccionada",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9CA3AF),
                    )
                    Text(
                        text = selectedDate.formatFullDateSpanish(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF353D3C),
                        textAlign = TextAlign.Center,
                    )
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth().selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CalendarViewOption.entries.forEach { option ->
                    val selected = selectedView == option
                    val contentColor = if (selected) Color.White else Color.Black
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(50))
                            .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .selectable(
                                selected = selected,
                                onClick = {
                                    selectedView = option
                                    option.viewMode?.let(onViewModeSelected)
                                },
                                role = Role.RadioButton,
                            )
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                    ) {
                        Icon(
                            imageVector = option.icon,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = if (selected) Color.White else MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = option.label,
                            style = MaterialTheme.typography.bodyLarge,
                            color = contentColor,
                        )
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        CalendarPickerDialog(
            initialDate = selectedDate,
            onDismiss = { showDatePicker = false },
            onConfirm = { date ->
                showDatePicker = false
                onDateSelected(date)
            },
        )
    }
}

private enum class CalendarViewOption(
    val label: String,
    val icon: ImageVector,
    val viewMode: CalendarViewMode? = null,
) {
    Day("Día", Icons.Default.ViewDay, CalendarViewMode.Day),
    ThreeDays("3 días", Icons.Default.ViewColumn, CalendarViewMode.ThreeDays),
    Week("Semana", Icons.Default.ViewWeek),
}
