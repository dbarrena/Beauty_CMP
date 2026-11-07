package com.lasso.lassoapp.screens.utils

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.ui.theme.LassoLightGray
import com.lasso.lassoapp.ui.theme.LassoSurfaceVariant

@Composable
internal fun rememberLoadingShimmer(): Brush {
    val transition = rememberInfiniteTransition(label = "Loading shimmer")
    val progress by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Restart),
        label = "Shimmer position",
    )
    val travel = with(LocalDensity.current) { 400.dp.toPx() }
    return Brush.linearGradient(
        colors = listOf(LassoLightGray, LassoSurfaceVariant, LassoLightGray),
        start = Offset(progress * travel, 0f),
        end = Offset(progress * travel + travel * 0.5f, travel * 0.25f),
    )
}
