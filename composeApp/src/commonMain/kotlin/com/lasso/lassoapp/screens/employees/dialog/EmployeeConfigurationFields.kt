package com.lasso.lassoapp.screens.employees.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.ui.theme.LassoDivider
import com.lasso.lassoapp.ui.theme.LassoGray
import com.lasso.lassoapp.ui.theme.LassoPrimary
import com.lasso.lassoapp.ui.theme.LassoPrimaryDark
import com.lasso.lassoapp.ui.theme.LassoPrimaryLight
import com.lasso.lassoapp.ui.theme.LassoSecondary
import com.lasso.lassoapp.ui.theme.LassoSecondaryDark
import com.lasso.lassoapp.ui.theme.LassoSecondaryLight
import com.lasso.lassoapp.ui.theme.LassoSurface
import com.lasso.lassoapp.ui.theme.LassoSurfaceVariant
import com.lasso.lassoapp.ui.theme.LassoTertiary
import com.lasso.lassoapp.ui.theme.LassoTertiaryDark
import com.lasso.lassoapp.ui.theme.LassoTertiaryLight
import com.lasso.lassoapp.ui.theme.LassoTextMuted
import com.lasso.lassoapp.ui.theme.LassoTextPrimary
import com.lasso.lassoapp.ui.theme.calendarColorOrPrimary

private data class EmployeeColorOption(val name: String, val color: Color) {
    val hex: String get() = "#" + (color.toArgb() and 0xFFFFFF).toString(16).padStart(6, '0').uppercase()
}

private val employeeColors = listOf(
    EmployeeColorOption("Turquesa", LassoPrimary),
    EmployeeColorOption("Turquesa oscuro", LassoPrimaryDark),
    EmployeeColorOption("Turquesa claro", LassoPrimaryLight),
    EmployeeColorOption("Amarillo", LassoSecondary),
    EmployeeColorOption("Mostaza", LassoSecondaryDark),
    EmployeeColorOption("Amarillo claro", LassoSecondaryLight),
    EmployeeColorOption("Coral", LassoTertiary),
    EmployeeColorOption("Coral oscuro", LassoTertiaryDark),
    EmployeeColorOption("Salmón", LassoTertiaryLight),
    EmployeeColorOption("Gris", LassoGray)
)

@Composable
fun EmployeeConfigurationFields(
    calendarColor: String?,
    onCalendarColorChange: (String?) -> Unit,
    isDisplayedOnCalendar: Boolean,
    onDisplayedOnCalendarChange: (Boolean) -> Unit,
    canSeeSales: Boolean,
    onCanSeeSalesChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    var showColorPicker by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Color en calendario", style = MaterialTheme.typography.labelLarge, color = LassoTextPrimary)
        Row(
            modifier = Modifier.fillMaxWidth()
                .background(LassoSurfaceVariant, RoundedCornerShape(14.dp))
                .clickable(enabled = enabled, role = Role.Button) { showColorPicker = true }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ColorSwatch(calendarColor)
            Text(
                text = if (calendarColor == null) "Predeterminado" else
                    employeeColors.firstOrNull { it.hex.equals(calendarColor, ignoreCase = true) }?.name ?: calendarColor,
                modifier = Modifier.weight(1f),
                color = LassoTextPrimary
            )
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = LassoTextMuted)
        }
        ConfigurationSwitch("Mostrar en calendario", isDisplayedOnCalendar, enabled, onDisplayedOnCalendarChange)
        ConfigurationSwitch("Puede ver ventas", canSeeSales, enabled, onCanSeeSalesChange)
    }
    if (showColorPicker && enabled) {
        EmployeeCalendarColorDialog(
            selectedColor = calendarColor,
            onDismiss = { showColorPicker = false },
            onSelect = {
                onCalendarColorChange(it)
                showColorPicker = false
            }
        )
    }
}

@Composable
private fun ConfigurationSwitch(label: String, checked: Boolean, enabled: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .toggleable(value = checked, enabled = enabled, role = Role.Switch, onValueChange = onChange)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(label, modifier = Modifier.weight(1f), color = LassoTextPrimary)
        Switch(
            checked = checked,
            onCheckedChange = null,
            enabled = enabled,
            colors = SwitchDefaults.colors(checkedTrackColor = LassoPrimary)
        )
    }
}

@Composable
fun EmployeeCalendarColorDialog(selectedColor: String?, onDismiss: () -> Unit, onSelect: (String?) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = LassoSurface,
        title = { Text("Color en calendario", color = LassoTextPrimary) },
        text = {
            Column(Modifier.fillMaxWidth().heightIn(max = 400.dp)
                .verticalScroll(rememberScrollState()).selectableGroup()) {
                ColorOptionRow("Predeterminado", null, selectedColor == null) { onSelect(null) }
                employeeColors.forEach { option ->
                    ColorOptionRow(option.name, option.hex, option.hex.equals(selectedColor, ignoreCase = true)) {
                        onSelect(option.hex)
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cancelar", color = LassoTextPrimary) } }
    )
}

@Composable
private fun ColorOptionRow(name: String, hex: String?, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ColorSwatch(hex)
        Text(name, modifier = Modifier.weight(1f), color = LassoTextPrimary)
        if (selected) Icon(Icons.Default.Check, contentDescription = null, tint = LassoPrimaryDark)
    }
}

@Composable
private fun ColorSwatch(hex: String?) {
    Box(Modifier.size(28.dp).background(calendarColorOrPrimary(hex), CircleShape).border(1.dp, LassoDivider, CircleShape))
}
