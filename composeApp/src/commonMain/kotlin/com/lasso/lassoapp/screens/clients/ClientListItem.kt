package com.lasso.lassoapp.screens.clients

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.model.Client
import com.lasso.lassoapp.ui.theme.LassoPrimary
import com.lasso.lassoapp.ui.theme.LassoTextMuted
import com.lasso.lassoapp.ui.theme.LassoTextPrimary
import lassoapp.composeapp.generated.resources.Res
import lassoapp.composeapp.generated.resources.pencil_icon
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun ClientListItem(
    client: Client,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onEditClick: ((Client) -> Unit)? = null,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.verticalGradient(listOf(LassoPrimary, Color(0xFF00B999)))),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    client.name.firstOrNull()?.uppercase() ?: "",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(client.name, color = LassoTextPrimary, fontWeight = FontWeight.SemiBold)
                client.phone?.let { Text(it, color = LassoTextMuted, fontSize = 14.sp) }
                client.email?.let { Text(it, color = LassoTextMuted, fontSize = 13.sp) }
                client.notes?.takeIf { it.isNotBlank() }?.let {
                    Text(it, color = LassoTextMuted, fontSize = 12.sp, maxLines = 1)
                }
            }
            onEditClick?.let { editClient ->
                IconButton(onClick = { editClient(client) }) {
                    Icon(
                        painter = painterResource(Res.drawable.pencil_icon),
                        contentDescription = "Editar cliente",
                        tint = LassoPrimary,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        }
    }
}
