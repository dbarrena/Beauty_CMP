package com.lasso.lassoapp.screens.calendar.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.ui.theme.LassoPrimary
import com.lasso.lassoapp.ui.theme.LassoTertiary

@Composable
internal fun AppointmentDialogActions(
    isEditing: Boolean,
    isValid: Boolean,
    isSaving: Boolean,
    isDeleting: Boolean,
    onSave: () -> Unit,
    onDelete: () -> Unit,
) {
    val isBusy = isSaving || isDeleting
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(
            onClick = onSave,
            enabled = isValid && !isBusy,
            modifier = Modifier.weight(1f).height(48.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LassoPrimary),
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp,
                )
            } else {
                Text("Guardar", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
        }

        if (isEditing) {
            OutlinedButton(
                onClick = onDelete,
                enabled = !isBusy,
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                border = BorderStroke(1.dp, LassoTertiary),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = LassoTertiary),
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = LassoTertiary,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar cita",
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }
    }
}
