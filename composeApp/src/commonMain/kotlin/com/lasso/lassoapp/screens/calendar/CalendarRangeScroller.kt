package com.lasso.lassoapp.screens.calendar

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
internal fun CalendarRangeScroller(
    scrollState: ScrollState,
    enabled: Boolean,
    isLoading: Boolean,
    isLoadingNextRange: Boolean,
    isLoadingPreviousRange: Boolean,
    onNextRange: () -> Unit,
    onPreviousRange: () -> Unit,
    modifier: Modifier = Modifier,
    userScrollEnabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    val density = LocalDensity.current
    val threshold = with(density) { 72.dp.toPx() }
    val loadingWidth = with(density) { 56.dp.toPx() }
    val pullState = remember(threshold) { CalendarEndPullState(threshold) }
    val startPullState = remember(threshold) { CalendarEndPullState(threshold) }
    val currentEnabled by rememberUpdatedState(enabled)
    val currentLoading by rememberUpdatedState(isLoading)
    val currentOnNextRange by rememberUpdatedState(onNextRange)
    val currentOnPreviousRange by rememberUpdatedState(onPreviousRange)

    LaunchedEffect(enabled, isLoadingNextRange) {
        if (!enabled || !isLoadingNextRange) pullState.reset()
    }
    LaunchedEffect(enabled, isLoadingPreviousRange) {
        if (!enabled || !isLoadingPreviousRange) startPullState.reset()
    }

    val connection = remember(scrollState, pullState, startPullState) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (source != NestedScrollSource.UserInput || !currentEnabled) return Offset.Zero
                val consumed = if (available.x > 0f) pullState.retract(available.x)
                    else -startPullState.retract(-available.x)
                return Offset(consumed, 0f)
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                if (source != NestedScrollSource.UserInput) return Offset.Zero
                val consumedX = if (available.x < 0f) pullState.pull(
                    deltaX = available.x,
                    atEnd = !scrollState.canScrollForward,
                    enabled = currentEnabled && !currentLoading,
                ) else -startPullState.pull(
                    deltaX = -available.x,
                    atEnd = !scrollState.canScrollBackward,
                    enabled = currentEnabled && !currentLoading,
                )
                return Offset(consumedX, 0f)
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                val wasPulling = pullState.distance > 0f || startPullState.distance > 0f
                val shouldLoad = pullState.release()
                val shouldLoadPrevious = startPullState.release()
                if (currentEnabled && !currentLoading) {
                    if (shouldLoad) currentOnNextRange()
                    else if (shouldLoadPrevious) currentOnPreviousRange()
                }
                return if (wasPulling) Velocity(available.x, 0f) else Velocity.Zero
            }
        }
    }

    val reveal by animateFloatAsState(
        targetValue = if (enabled && isLoadingNextRange) loadingWidth else pullState.distance,
        animationSpec = spring(stiffness = 600f),
        label = "Calendar end pull",
    )
    val progress = (reveal / threshold).coerceIn(0f, 1f)
    val startReveal by animateFloatAsState(
        targetValue = if (enabled && isLoadingPreviousRange) loadingWidth else startPullState.distance,
        animationSpec = spring(stiffness = 600f),
        label = "Calendar start pull",
    )
    val startProgress = (startReveal / threshold).coerceIn(0f, 1f)

    Box(modifier.clipToBounds().nestedScroll(connection)) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset((startReveal - reveal).roundToInt(), 0) }
                .horizontalScroll(scrollState, enabled = userScrollEnabled, overscrollEffect = null),
            content = content,
        )
        if (enabled && startReveal > 0.5f) {
            Box(
                modifier = Modifier.align(Alignment.CenterStart).width(with(density) { startReveal.toDp() }),
                contentAlignment = Alignment.Center,
            ) {
                Crossfade(targetState = isLoadingPreviousRange, label = "Calendar previous arrow to spinner") { loading ->
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (startPullState.isArmed) "Soltar para cargar los 3 días anteriores"
                                else "Cargar los 3 días anteriores",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp).graphicsLayer {
                                alpha = startProgress
                                scaleX = 0.5f + startProgress * 0.5f
                                scaleY = scaleX
                            },
                        )
                    }
                }
            }
        }
        if (enabled && reveal > 0.5f) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(with(density) { reveal.toDp() }),
                contentAlignment = Alignment.Center,
            ) {
                Crossfade(targetState = isLoadingNextRange, label = "Calendar arrow to spinner") { loading ->
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = if (pullState.isArmed) "Soltar para cargar los próximos 3 días"
                                else "Cargar los próximos 3 días",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp).graphicsLayer {
                                alpha = progress
                                scaleX = 0.5f + progress * 0.5f
                                scaleY = scaleX
                            },
                        )
                    }
                }
            }
        }
    }
}
