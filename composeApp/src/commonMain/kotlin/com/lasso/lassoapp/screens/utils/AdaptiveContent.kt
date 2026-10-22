package com.lasso.lassoapp.screens.utils

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

internal data class ContentPlacement(val pinHeader: Boolean, val pinActions: Boolean)

internal fun contentPlacement(
    available: Int,
    header: Int,
    actions: Int,
    minimumBody: Int,
): ContentPlacement {
    val pinActions = available - actions >= minimumBody
    return ContentPlacement(pinActions && available - header - actions >= minimumBody, pinActions)
}

/**
 * Natural height stays independent of viewport constraints. Movable content retains field
 * state/focus.
 */
internal class MeasuredContent {
    internal var hasFocus = false
    var size by mutableStateOf(IntSize.Zero)
        private set

    lateinit var content: @Composable () -> Unit
        internal set

    internal fun measured(size: IntSize) {
        if (this.size != size) this.size = size
    }
}

@Composable
internal fun rememberMeasuredContent(content: @Composable () -> Unit): MeasuredContent {
    val current by rememberUpdatedState(content)
    val measured = remember { MeasuredContent() }
    val focus = remember { FocusRequester() }
    val movable = remember {
        movableContentOf {
            Box(
                Modifier.fillMaxWidth()
                    .wrapContentHeight(unbounded = true)
                    .onSizeChanged(measured::measured)
                    .focusRequester(focus)
                    .onFocusChanged { measured.hasFocus = it.hasFocus }
                    .focusGroup()
            ) {
                current()
            }
        }
    }
    measured.content = {
        // Moving layout nodes detaches their focus target. Restore only if this header owned focus
        // before relocation; never take focus from a different field in the body.
        val restoreFocus = measured.hasFocus
        LaunchedEffect(Unit) { if (restoreFocus) focus.requestFocus() }
        movable()
    }
    return measured
}

/**
 * Header/action placement depends on natural measurements, never on its shrinking scroll viewport.
 */
@Composable
internal fun AdaptiveScrollContent(
    header: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    actions: (@Composable () -> Unit)? = null,
    scroll: ScrollState = rememberScrollState(),
    body: @Composable ColumnScope.() -> Unit,
) {
    val measuredHeader = rememberMeasuredContent(header)
    val measuredActions = rememberMeasuredContent { actions?.invoke() }
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val placement =
            contentPlacement(
                constraints.maxHeight,
                measuredHeader.size.height,
                measuredActions.size.height,
                with(androidx.compose.ui.platform.LocalDensity.current) { 96.dp.roundToPx() },
            )
        Column {
            if (placement.pinHeader) measuredHeader.content()
            Column(Modifier.fillMaxWidth().weight(1f, fill = false).verticalScroll(scroll)) {
                if (!placement.pinHeader) measuredHeader.content()
                body()
                if (!placement.pinActions) measuredActions.content()
            }
            if (placement.pinActions) measuredActions.content()
        }
    }
}
