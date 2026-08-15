package com.lasso.lassoapp.screens.calendar.dialog

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.ui.theme.LassoPrimary
import com.lasso.lassoapp.ui.theme.LassoTextPrimary

@Composable
internal fun AppointmentTimeRangeField(
    startMinutes: Int,
    endMinutes: Int,
    enabled: Boolean,
    onStartSelected: (Int) -> Unit,
    onEndSelected: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            AppointmentFieldLabel("Hora inicio")
            AppointmentTimePickerField(startMinutes, enabled, onStartSelected)
        }
        Column(modifier = Modifier.weight(1f)) {
            AppointmentFieldLabel("Hora fin")
            AppointmentTimePickerField(endMinutes, enabled, onEndSelected)
        }
    }
}

@Composable
private fun AppointmentTimePickerField(
    minutes: Int,
    enabled: Boolean,
    onSelected: (Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val menuScrollState = rememberScrollState()
    val itemHeightPx = with(LocalDensity.current) { 48.dp.roundToPx() }
    val selectedIndex = ((minutes - FIRST_APPOINTMENT_MINUTES) / TIME_INTERVAL_MINUTES)
        .coerceIn(0, APPOINTMENT_TIME_OPTIONS.lastIndex)

    LaunchedEffect(expanded, selectedIndex) {
        if (expanded) {
            withFrameNanos { }
            menuScrollState.scrollTo(selectedIndex * itemHeightPx)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .border(1.5.dp, LassoPrimary, RoundedCornerShape(24.dp))
            .clickable(enabled = enabled) { expanded = true }
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(formatAppointmentTime(minutes), color = LassoTextPrimary, fontSize = 14.sp)
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            scrollState = menuScrollState,
        ) {
            APPOINTMENT_TIME_OPTIONS.forEach { option ->
                DropdownMenuItem(
                    text = { Text(formatAppointmentTime(option)) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

private fun formatAppointmentTime(minutes: Int): String {
    val hour24 = minutes / 60
    val minute = minutes % 60
    val hour12 = when (val hour = hour24 % 12) {
        0 -> 12
        else -> hour
    }
    val suffix = if (hour24 < 12) "a.m." else "p.m."
    return "$hour12:${minute.toString().padStart(2, '0')} $suffix"
}

private const val FIRST_APPOINTMENT_MINUTES = 7 * 60
private const val LAST_APPOINTMENT_MINUTES = 20 * 60
private const val TIME_INTERVAL_MINUTES = 30
private val APPOINTMENT_TIME_OPTIONS =
    (FIRST_APPOINTMENT_MINUTES..LAST_APPOINTMENT_MINUTES step TIME_INTERVAL_MINUTES).toList()
