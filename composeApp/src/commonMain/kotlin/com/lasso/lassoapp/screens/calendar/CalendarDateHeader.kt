package com.lasso.lassoapp.screens.calendar

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.ui.theme.LassoPrimary
import com.lasso.lassoapp.ui.theme.LassoTextPrimary
import com.lasso.lassoapp.utils.formatFullDateSpanish
import com.lasso.lassoapp.utils.formatMonthYearSpanish
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

@Composable
internal fun CalendarDateHeader(
    date: LocalDate,
    viewMode: CalendarViewMode,
    enabled: Boolean,
    loadingDirection: CalendarRangeDirection?,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        CalendarDateNavigationButton(CalendarRangeDirection.Previous, enabled,
            loadingDirection == CalendarRangeDirection.Previous, onPrevious)
        Text(
            text = calendarDateHeaderLabel(date, viewMode),
            modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = LassoTextPrimary,
            textAlign = TextAlign.Center,
        )
        CalendarDateNavigationButton(CalendarRangeDirection.Next, enabled,
            loadingDirection == CalendarRangeDirection.Next, onNext)
    }
}

@Composable
internal fun CalendarDateNavigationButton(
    direction: CalendarRangeDirection,
    enabled: Boolean,
    loading: Boolean,
    onClick: () -> Unit,
) {
    // Keep a full touch target around the small visible circle.
    IconButton(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = Modifier.size(48.dp).semantics {
            contentDescription = if (direction == CalendarRangeDirection.Previous) "Rango anterior" else "Rango siguiente"
            if (loading) stateDescription = "Cargando calendario"
        },
    ) {
        Box(
            modifier = Modifier.size(30.dp).background(LassoPrimary, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Crossfade(targetState = loading) { isLoading ->
                Box(Modifier.size(20.dp), contentAlignment = Alignment.Center) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp,
                        )
                    } else {
                        Icon(
                            imageVector = if (direction == CalendarRangeDirection.Previous)
                                Icons.AutoMirrored.Filled.KeyboardArrowLeft else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color.White,
                        )
                    }
                }
            }
        }
    }
}

internal fun calendarDateHeaderLabel(date: LocalDate, viewMode: CalendarViewMode): String {
    if (viewMode == CalendarViewMode.Day) return date.formatFullDateSpanish()
    val end = date.plus(viewMode.dayCount - 1, DateTimeUnit.DAY)
    return when {
        date.year != end.year -> "${date.dayOfMonth} de ${date.formatMonthYearSpanish()} – ${end.dayOfMonth} de ${end.formatMonthYearSpanish()}"
        date.month != end.month -> "${date.dayOfMonth} de ${date.formatMonthYearSpanish().substringBefore(" de ")} – ${end.dayOfMonth} de ${end.formatMonthYearSpanish()}"
        else -> "${date.dayOfMonth}–${end.dayOfMonth} de ${end.formatMonthYearSpanish()}"
    }
}
