package com.lasso.lassoapp.screens.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.model.Event
import com.lasso.lassoapp.model.formatEventTime
import com.lasso.lassoapp.model.minutesBetween

@Composable
fun CalendarEventItem(
    event: Event,
    onEdit: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val duration = minutesBetween(event.start, event.end)
    val isShort = duration <= 30

    Card(
        onClick = onEdit,
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(
                horizontal = 4.dp,
                vertical = if (isShort) 2.dp else 4.dp
            ),
        shape = RoundedCornerShape(if (isShort) 12.dp else 18.dp),
        border = BorderStroke(2.dp, Color(0xFF52E4C2)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFC9F7E8)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.dp,
                    vertical = if (isShort) 3.dp else 8.dp,
                ),
        ) {
            event.name?.let { clientName ->
                Text(
                    text = clientName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF353D3C),
                        fontSize = if (isShort) 10.sp else 14.sp,
                        lineHeight = if (isShort) 10.sp else 18.sp,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            event.description?.let { serviceName ->
                Text(
                    text = serviceName,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF8D9997),
                        fontSize = if (isShort) 9.sp else 12.sp,
                        lineHeight = if (isShort) 9.sp else 16.sp,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (!isShort) {
                Spacer(modifier = Modifier.height(3.dp))
            }
            Text(
                text = "${event.start.formatEventTime()} - ${event.end.formatEventTime()}",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00BFA5),
                    fontSize = if (isShort) 9.sp else 12.sp,
                    lineHeight = if (isShort) 9.sp else 16.sp,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
