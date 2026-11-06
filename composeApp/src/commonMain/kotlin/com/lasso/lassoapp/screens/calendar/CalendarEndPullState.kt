package com.lasso.lassoapp.screens.calendar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue

/** Distances are pixels. Releasing requests another range only while the pull is armed. */
internal class CalendarEndPullState(private val threshold: Float) {
    init {
        require(threshold > 0f)
    }

    var distance by mutableFloatStateOf(0f)
        private set
    val isArmed: Boolean get() = distance >= threshold

    fun pull(deltaX: Float, atEnd: Boolean, enabled: Boolean): Float {
        if (!enabled || !atEnd || deltaX >= 0f) return 0f
        distance = (distance - deltaX * 0.5f).coerceAtMost(threshold * 2.5f)
        return deltaX
    }

    fun retract(deltaX: Float): Float {
        if (deltaX <= 0f || distance <= 0f) return 0f
        val consumed = minOf(deltaX, distance / 0.5f)
        distance = (distance - consumed * 0.5f).coerceAtLeast(0f)
        return consumed
    }

    fun release(): Boolean {
        val shouldLoad = isArmed
        reset()
        return shouldLoad
    }

    fun reset() {
        distance = 0f
    }
}
