package com.lasso.lassoapp.screens.calendar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.model.Event
import com.lasso.lassoapp.model.formatEventTime
import com.lasso.lassoapp.model.minutesBetween
import lassoapp.composeapp.generated.resources.Res
import lassoapp.composeapp.generated.resources.edit_icon
import lassoapp.composeapp.generated.resources.trash_icon
import org.jetbrains.compose.resources.painterResource

@Composable
fun CalendarEventItem(
    event: Event,
    modifier: Modifier = Modifier
) {
    val duration = minutesBetween(event.start, event.end)
    val isShort = duration <= 30

    Card(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(
                horizontal = if (isShort) 4.dp else 8.dp,
                vertical = if (isShort) 2.dp else 4.dp
            ),
        shape = RoundedCornerShape(if (isShort) 8.dp else 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isShort) 1.dp else 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isShort) 8.dp else 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = event.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF353D3C),
                            fontSize = if (isShort) 11.sp else 16.sp,
                            lineHeight = if (isShort) 11.sp else 16.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!isShort) {
                        event.description?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF9CA3AF),
                                    fontSize = 14.sp,
                                    lineHeight = 14.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Color(0xFF00D1AD),
                            modifier = Modifier.size(if (isShort) 12.dp else 16.dp)
                        )
                        Spacer(modifier = Modifier.width(if (isShort) 2.dp else 4.dp))
                        Text(
                            text = "${event.start.formatEventTime()} - ${event.end.formatEventTime()}",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF00D1AD),
                                fontSize = if (isShort) 9.sp else 12.sp,
                                lineHeight = if (isShort) 9.sp else 12.sp
                            )
                        )
                    }

                    if (!isShort) {
                        Spacer(modifier = Modifier.size(8.dp))

                        Row {
                            Image(
                                painter = painterResource(Res.drawable.edit_icon),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                contentScale = ContentScale.Fit,
                            )
                            Spacer(modifier = Modifier.width(20.dp))
                            Image(
                                painter = painterResource(Res.drawable.trash_icon),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                contentScale = ContentScale.Fit,
                            )
                        }
                    }
                }
            }
        }
    }
}
