package com.lasso.lassoapp.screens.calendar.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.model.Client
import com.lasso.lassoapp.ui.theme.LassoPrimary
import com.lasso.lassoapp.ui.theme.LassoSurfaceVariant
import com.lasso.lassoapp.ui.theme.LassoTextMuted
import com.lasso.lassoapp.utils.buildWhatsAppUrl
import lassoapp.composeapp.generated.resources.Res
import lassoapp.composeapp.generated.resources.whatsapp_icon
import org.jetbrains.compose.resources.painterResource

private val WhatsAppGreen = Color(0xFF25D366)

@Composable
internal fun AppointmentClientField(
    selectedClient: Client?,
    enabled: Boolean,
    onSearchClick: () -> Unit,
    onClientRemoved: () -> Unit,
    onNewClient: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    AppointmentFieldLabel("Cliente (opcional)")
    if (selectedClient != null) {
        val whatsAppUrl = selectedClient.phone?.let {
            buildWhatsAppUrl(phone = it, message = "Hola ${selectedClient.name}")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(24.dp),
                color = LassoPrimary,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = selectedClient.name,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(onClick = onClientRemoved, enabled = enabled) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Quitar cliente",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
            if (whatsAppUrl != null) {
                OutlinedButton(
                    onClick = { uriHandler.openUri(whatsAppUrl) },
                    enabled = enabled,
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    border = BorderStroke(1.dp, WhatsAppGreen),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = WhatsAppGreen),
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.whatsapp_icon),
                        contentDescription = "Abrir WhatsApp",
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }
        return
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(modifier = Modifier.weight(2f)) {
            Surface(
                onClick = onSearchClick,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(24.dp),
                color = LassoSurfaceVariant,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = LassoTextMuted,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = "Buscar...",
                        color = LassoTextMuted,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
        }
        Button(
            onClick = onNewClient,
            enabled = enabled,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LassoPrimary),
            contentPadding = PaddingValues(horizontal = 12.dp),
        ) {
            Text("Nuevo", fontSize = 14.sp)
        }
    }
}
