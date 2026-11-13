package com.lasso.lassoapp.screens.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
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
    compactShortEvents: Boolean = false,
) {
    val duration = minutesBetween(event.start, event.end)
    val isShort = duration <= 30
    val isCalendarBlock = event.calendarBlockId != null
    val hasNotes = !event.notes.isNullOrBlank()
    val compactText = isShort || (hasNotes && duration <= 60)
    val background = if (isCalendarBlock) LassoLightGray else event.color.copy(alpha = 0.15f).compositeOver(Color.White)

    Card(
        onClick = onEdit,
        enabled = !isPlaceholder,
        modifier = modifier
            .then(if (isCalendarBlock) Modifier.semantics {
                contentDescription = "${event.name ?: "Bloqueo de calendario"}, ${event.start.formatEventTime()} - ${event.end.formatEventTime()}"
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
        if (!isPlaceholder && isCalendarBlock) Column(
            Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = if (isShort) 2.dp else 8.dp),
        ) {
            Text(event.name ?: "Bloqueo de calendario", color = com.lasso.lassoapp.ui.theme.LassoTextPrimary,
                fontWeight = FontWeight.SemiBold, fontSize = if (isShort) 10.sp else 13.sp,
                lineHeight = if (isShort) 11.sp else 17.sp, softWrap = true)
            if (duration > 15) Text("${event.start.formatEventTime()} - ${event.end.formatEventTime()}",
                color = com.lasso.lassoapp.ui.theme.LassoGray, fontSize = if (isShort) 9.sp else 11.sp,
                lineHeight = if (isShort) 10.sp else 15.sp, maxLines = 1)
        } else if (!isPlaceholder && hasNotes && isShort) Column(
            Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = if (duration <= 15) 0.dp else 2.dp),
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(event.name ?: event.description ?: "Cita", Modifier.weight(1f), color = event.color,
                    fontWeight = FontWeight.SemiBold, fontSize = if (duration <= 15) 8.sp else 10.sp,
                    lineHeight = if (duration <= 15) 8.sp else 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${event.start.formatEventTime()} - ${event.end.formatEventTime()}", color = event.color,
                    fontWeight = FontWeight.Bold, fontSize = if (duration <= 15) 7.sp else 9.sp,
                    lineHeight = if (duration <= 15) 8.sp else 11.sp, maxLines = 1)
            }
            CalendarAppointmentNotes(event, duration)
        } else if (!isPlaceholder && isShort && compactShortEvents) Column(
            Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 2.dp),
        ) {
            Text(event.name ?: event.description ?: "Cita", color = event.color,
                fontWeight = FontWeight.SemiBold, fontSize = if (duration <= 15) 10.sp else 11.sp,
                lineHeight = if (duration <= 15) 10.sp else 13.sp,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (duration > 15) Text("${event.start.formatEventTime()} - ${event.end.formatEventTime()}",
                color = event.color, fontSize = 10.sp, lineHeight = 12.sp, maxLines = 1)
        } else if (!isPlaceholder && !isCalendarBlock) Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.dp,
                    vertical = if (compactText) 3.dp else 8.dp,
                ),
        ) {
            event.name?.let { clientName ->
                Text(
                    text = clientName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = event.color,
                        fontSize = if (compactText) 10.sp else 14.sp,
                        lineHeight = if (compactText) 10.sp else 18.sp,
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
                        fontSize = if (compactText) 9.sp else 12.sp,
                        lineHeight = if (compactText) 9.sp else 16.sp,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (!compactText) {
                Spacer(modifier = Modifier.height(3.dp))
            }
            Text(
                text = "${event.start.formatEventTime()} - ${event.end.formatEventTime()}",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = event.color,
                    fontSize = if (compactText) 9.sp else 12.sp,
                    lineHeight = if (compactText) 9.sp else 16.sp,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (hasNotes) CalendarAppointmentNotes(event, duration)
        }
    }
}

@Composable
private fun CalendarAppointmentNotes(event: Event, duration: Long) {
    val notes = event.notes?.trim()?.takeIf { it.isNotEmpty() } ?: return
    if (duration > 30) Spacer(Modifier.height(3.dp))
    Text(
        text = notes,
        modifier = Modifier.fillMaxWidth().background(event.color, RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = if (duration <= 30) 0.dp else 3.dp),
        color = Color.White,
        fontSize = if (duration <= 15) 7.sp else if (duration <= 30) 9.sp else 11.sp,
        lineHeight = if (duration <= 15) 8.sp else if (duration <= 30) 10.sp else 14.sp,
        maxLines = if (duration <= 60) 1 else 3,
        overflow = TextOverflow.Ellipsis,
    )
}
