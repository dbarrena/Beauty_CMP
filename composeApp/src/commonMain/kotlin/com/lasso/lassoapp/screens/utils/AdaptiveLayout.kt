package com.lasso.lassoapp.screens.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Cache measurements against the typography and environment used to render the label. */
@Composable
internal fun labelWidth(text: String, style: TextStyle): Dp = labelWidth(listOf(text), style)

@Composable
internal fun labelWidth(text: List<String>, style: TextStyle): Dp {
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val resolver = LocalFontFamilyResolver.current
    return remember(text, style, density, resolver, measurer) {
        with(density) {
            (text.maxOfOrNull { measurer.measure(it, style, softWrap = false).size.width } ?: 0)
                .toDp()
        }
    }
}

internal fun pairFits(width: Float, first: Float, second: Float, spacing: Float): Boolean =
    width >= first + second + spacing

@Composable
internal fun AdaptiveEqualPair(
    minFieldWidth: Dp,
    modifier: Modifier = Modifier,
    spacing: Dp = 12.dp,
    first: @Composable (Modifier) -> Unit,
    second: @Composable (Modifier) -> Unit,
) {
    BoxWithConstraints(modifier.fillMaxWidth()) {
        if (pairFits(maxWidth.value, minFieldWidth.value, minFieldWidth.value, spacing.value)) {
            Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                first(Modifier.weight(1f))
                second(Modifier.weight(1f))
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(spacing)) {
                first(Modifier.fillMaxWidth())
                second(Modifier.fillMaxWidth())
            }
        }
    }
}

/** Action gets its required width; content gets the rest. Stack if their minimums cannot fit. */
@Composable
internal fun AdaptiveActionRow(
    minFieldWidth: Dp,
    secondMinFieldWidth: Dp,
    modifier: Modifier = Modifier,
    spacing: Dp = 12.dp,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    first: @Composable (Modifier) -> Unit,
    second: @Composable (Modifier) -> Unit,
) {
    BoxWithConstraints(modifier.fillMaxWidth()) {
        if (
            pairFits(maxWidth.value, minFieldWidth.value, secondMinFieldWidth.value, spacing.value)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing),
                verticalAlignment = verticalAlignment,
            ) {
                first(Modifier.weight(1f))
                second(Modifier.width(secondMinFieldWidth))
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(spacing)) {
                first(Modifier.fillMaxWidth())
                second(Modifier.fillMaxWidth())
            }
        }
    }
}

/** Largest fitting size on the existing 1-sp grid. Minimum may wrap rather than lose digits. */
internal fun fittingFontSize(preferred: Float, fits: (Float) -> Boolean): Float {
    val minimum = minOf(16f, preferred)
    if (fits(preferred)) return preferred
    var low = 0
    var high = kotlin.math.ceil(preferred - minimum).toInt()
    while (low < high) {
        val steps = (low + high) / 2
        if (fits((preferred - steps).coerceAtLeast(minimum))) high = steps else low = steps + 1
    }
    return (preferred - low).coerceAtLeast(minimum)
}

/** Currency-only fitting shared by Android/iOS. Never apply ellipsis or a line limit to amounts. */
@Composable
internal fun FittedAmountText(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val resolver = LocalFontFamilyResolver.current
    BoxWithConstraints(modifier) {
        val width = constraints.maxWidth
        val bounded = constraints.hasBoundedWidth
        val fittedStyle =
            remember(text, style, width, density, resolver, measurer) {
                if (!bounded || !style.fontSize.isSp) style
                else {
                    val size =
                        fittingFontSize(style.fontSize.value) { candidate ->
                            measurer
                                .measure(
                                    text,
                                    style.copy(fontSize = candidate.sp),
                                    softWrap = false,
                                )
                                .size
                                .width <= width
                        }
                    style.copy(fontSize = size.sp)
                }
            }
        Text(text, style = fittedStyle, color = color)
    }
}
