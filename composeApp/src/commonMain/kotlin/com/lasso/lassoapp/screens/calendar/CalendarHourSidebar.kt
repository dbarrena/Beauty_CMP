package com.lasso.lassoapp.screens.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.LocalTime
import com.lasso.lassoapp.ui.theme.LassoPrimary

@Composable
fun CalendarHourSidebar(
    hourHeight: Dp,
    modifier: Modifier = Modifier,
    label: @Composable (time: LocalTime) -> Unit = { BasicSidebarLabel(time = it) },
    draggedStartMinutes: Int? = null,
    draggedColor: Color = LassoPrimary,
) {
    Box(modifier = modifier) {
      Column {
        for (i in 7..20) {
            val time = LocalTime(hour = i, minute = 0)
            Box(
                modifier = Modifier
                    .height(hourHeight)
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopStart
            ) {
                label(time)
            }
        }
      }
      if (draggedStartMinutes != null) {
          val tagOffset = maxOf(0.dp, hourHeight * ((draggedStartMinutes - 7 * 60) / 60f) - 12.dp)
          Box(
              Modifier.offset(y = tagOffset).fillMaxWidth().height(24.dp)
                  .background(draggedColor, RoundedCornerShape(6.dp)),
              contentAlignment = Alignment.Center,
          ) {
              Text(
                  "${(draggedStartMinutes / 60).toString().padStart(2, '0')}:${(draggedStartMinutes % 60).toString().padStart(2, '0')}",
                  color = Color.White,
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
              )
          }
      }
    }
}

@Composable
fun BasicSidebarLabel(
    time: LocalTime,
    modifier: Modifier = Modifier,
) {
    Text(
        text = time.toHourAmPm(),
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Medium,
            color = Color(0xFF4B5563),
            fontSize = 13.sp
        ),
        modifier = modifier
            .padding(top = 2.dp, end = 4.dp)
    )
}

private fun LocalTime.toHourAmPm(): String {
    val hour12 = when {
        hour == 0 || hour == 12 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    val period = if (hour < 12) "AM" else "PM"
    return "$hour12 $period"
}
