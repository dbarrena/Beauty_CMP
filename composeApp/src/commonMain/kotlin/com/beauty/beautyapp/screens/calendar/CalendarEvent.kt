package com.beauty.beautyapp.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.beauty.beautyapp.model.Event
import com.beauty.beautyapp.model.formatEventTimeAmPm

@Composable
fun CalendarEventItem(
    event: Event
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(end = 2.dp, bottom = 2.dp)
            .background(event.color, shape = RoundedCornerShape(4.dp))
            .padding(4.dp)
    ) {
        Text(
            text = "${event.start.formatEventTimeAmPm()} - ${event.end.formatEventTimeAmPm()}",
            style = MaterialTheme.typography.bodyMedium,
        )

        Text(
            text = event.name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
        )

        if (event.description != null) {
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

fun Modifier.eventData(event: Event) = this.then(EventDataModifier(event))

private class EventDataModifier(
    val event: Event,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = event
}