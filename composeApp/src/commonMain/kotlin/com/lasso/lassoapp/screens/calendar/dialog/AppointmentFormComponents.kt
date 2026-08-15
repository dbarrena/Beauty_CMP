package com.lasso.lassoapp.screens.calendar.dialog

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.ui.theme.LassoPrimary
import com.lasso.lassoapp.ui.theme.LassoSurfaceVariant
import com.lasso.lassoapp.ui.theme.LassoTextMuted
import com.lasso.lassoapp.ui.theme.LassoTextPrimary

@Composable
internal fun AppointmentFieldLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(bottom = 7.dp),
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Medium,
            color = LassoTextPrimary,
            fontSize = 14.sp,
        ),
    )
}

@Composable
internal fun AppointmentFormSpacer() = Spacer(modifier = Modifier.height(14.dp))

@Composable
internal fun AppointmentNotesField(
    notes: String,
    enabled: Boolean,
    onNotesChanged: (String) -> Unit,
) {
    AppointmentFieldLabel("Notas (opcional)")
    TextField(
        value = notes,
        onValueChange = onNotesChanged,
        modifier = Modifier.fillMaxWidth().height(80.dp),
        placeholder = { Text("Notas adicionales", color = LassoTextMuted) },
        shape = RoundedCornerShape(20.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = LassoSurfaceVariant,
            unfocusedContainerColor = LassoSurfaceVariant,
            disabledContainerColor = LassoSurfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = LassoPrimary,
            focusedTextColor = LassoTextPrimary,
            unfocusedTextColor = LassoTextPrimary,
        ),
        enabled = enabled,
    )
}
