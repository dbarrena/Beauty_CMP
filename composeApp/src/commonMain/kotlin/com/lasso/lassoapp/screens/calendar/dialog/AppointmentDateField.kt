package com.lasso.lassoapp.screens.calendar.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.screens.utils.formatDdMmYyyy
import com.lasso.lassoapp.ui.theme.LassoSurfaceVariant
import com.lasso.lassoapp.ui.theme.LassoTextMuted
import com.lasso.lassoapp.ui.theme.LassoTextPrimary
import kotlinx.datetime.LocalDate

@Composable
internal fun AppointmentDateField(
    date: LocalDate,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    AppointmentFieldLabel("Fecha")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(LassoSurfaceVariant, RoundedCornerShape(24.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(date.formatDdMmYyyy(), color = LassoTextPrimary, fontSize = 16.sp)
            Icon(
                Icons.Default.CalendarMonth,
                contentDescription = "Seleccionar fecha",
                tint = LassoTextMuted,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}
