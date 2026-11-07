package com.lasso.lassoapp.screens.calendar

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.ui.theme.LassoLightGray
import com.lasso.lassoapp.ui.theme.LassoSurfaceVariant

@Composable
internal fun rememberCalendarShimmer(): Brush {
    val transition = rememberInfiniteTransition(label = "Calendar loading shimmer")
    val progress by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Restart),
        label = "Calendar shimmer position",
    )
    val travel = with(LocalDensity.current) { 400.dp.toPx() }
    return Brush.linearGradient(
        colors = listOf(LassoLightGray, LassoSurfaceVariant, LassoLightGray),
        start = Offset(progress * travel, 0f),
        end = Offset(progress * travel + travel * 0.5f, travel * 0.25f),
    )
}

@Composable
internal fun CalendarLoadingPills(brush: Brush, modifier: Modifier = Modifier) {
    val pillHeight = with(LocalDensity.current) { MaterialTheme.typography.bodySmall.lineHeight.toDp() } + 14.dp
    Row(
        modifier.fillMaxWidth().padding(horizontal = 12.dp).clearAndSetSemantics {},
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        listOf(76.dp, 96.dp, 88.dp).forEach { width ->
            Box(Modifier.width(width).height(pillHeight).background(brush, RoundedCornerShape(50)))
        }
    }
}

@Composable
internal fun CalendarLoadingColumns(
    brush: Brush,
    columnWidth: Dp,
    headerHeight: Dp,
    hourHeight: Dp,
    verticalScrollState: ScrollState,
) {
    repeat(2) {
        Column(Modifier.width(columnWidth).clearAndSetSemantics {}) {
            Column(
                Modifier.height(headerHeight).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            ) {
                Box(Modifier.width(100.dp).height(16.dp).background(brush, RoundedCornerShape(4.dp)))
                Box(Modifier.width(52.dp).height(12.dp).background(brush, RoundedCornerShape(4.dp)))
            }
            CalendarEvents(hourHeight, verticalScrollState, emptyList(), modifier = Modifier.fillMaxSize())
        }
    }
}
