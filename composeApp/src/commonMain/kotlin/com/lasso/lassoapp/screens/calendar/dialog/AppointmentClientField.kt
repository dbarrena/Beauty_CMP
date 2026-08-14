package com.lasso.lassoapp.screens.calendar.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.model.Client
import com.lasso.lassoapp.ui.theme.LassoPrimary

@Composable
internal fun AppointmentClientField(
    selectedClient: Client?,
    clients: List<Client>,
    enabled: Boolean,
    onClientSelected: (Client) -> Unit,
    onNewClient: () -> Unit,
) {
    AppointmentFieldLabel("Cliente")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(modifier = Modifier.weight(1f)) {
            AppointmentPickerField(
                value = selectedClient?.name,
                placeholder = "Nombre del cliente",
                options = clients,
                optionLabel = Client::name,
                enabled = enabled,
                onSelected = onClientSelected,
            )
        }
        Button(
            onClick = onNewClient,
            enabled = enabled,
            modifier = Modifier.height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LassoPrimary),
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Text("Nuevo", fontSize = 16.sp)
        }
    }
}
