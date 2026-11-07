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
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.model.Event
import com.lasso.lassoapp.model.formatEventTime
import com.lasso.lassoapp.model.minutesBetween
import com.lasso.lassoapp.ui.theme.LassoLightGray

@Composable
fun CalendarEventItem(
    event: Event,
    onEdit: () -> Unit = {},
    modifier: Modifier = Modifier,
    isPlaceholder: Boolean = false,
    isDragging: Boolean = false,
) {
    val duration = minutesBetween(event.start, event.end)
    val isShort = duration <= 30
    val isCalendarBlock = event.calendarBlockId != null
    val background = if (isCalendarBlock) LassoLightGray else event.color.copy(alpha = 0.15f).compositeOver(Color.White)

    Card(
        onClick = onEdit,
        enabled = !isPlaceholder,
        modifier = modifier
            .then(if (isCalendarBlock) Modifier.semantics {
                contentDescription = "Bloqueo de calendario, ${event.start.formatEventTime()} - ${event.end.formatEventTime()}"
            } else Modifier)
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(
                horizontal = 4.dp,
                vertical = if (isShort) 2.dp else 4.dp
            ),
        shape = RoundedCornerShape(if (isShort) 12.dp else 18.dp),
        border = BorderStroke(2.dp, event.color.copy(alpha = if (isPlaceholder) 0.25f else 1f)),
        colors = CardDefaults.cardColors(
            containerColor = background,
            disabledContainerColor = background.copy(alpha = 0.5f),
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDragging) 8.dp else 0.dp)
    ) {
        if (!isPlaceholder && !isCalendarBlock) Column(
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
                        color = event.color,
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
                        color = event.color,
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
                    color = event.color,
                    fontSize = if (isShort) 9.sp else 12.sp,
                    lineHeight = if (isShort) 9.sp else 16.sp,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
