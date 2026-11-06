package com.lasso.lassoapp

import com.lasso.lassoapp.screens.calendar.CalendarEndPullState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CalendarEndPullTest {
    @Test
    fun scrollingInsideGridOrWhileLoadingCannotRevealArrow() {
        val state = CalendarEndPullState(72f)
        assertEquals(0f, state.pull(-200f, atEnd = false, enabled = true))
        assertEquals(0f, state.pull(-200f, atEnd = true, enabled = false))
        assertEquals(0f, state.distance)
        assertFalse(state.isArmed)
    }

    @Test
    fun partialPullRevealsArrowAndReleaseCancelsIt() {
        val state = CalendarEndPullState(72f)
        state.pull(-60f, atEnd = true, enabled = true)
        assertTrue(state.distance > 0f && state.distance < 72f)
        assertFalse(state.isArmed)
        assertFalse(state.release())
        assertEquals(0f, state.distance)
        assertFalse(state.isArmed)
    }

    @Test
    fun reversingDirectionRetractsBeforeGridScrollsBack() {
        val state = CalendarEndPullState(72f)
        state.pull(-60f, atEnd = true, enabled = true)
        assertEquals(60f, state.retract(100f))
        assertEquals(0f, state.distance)
        assertFalse(state.isArmed)
    }

    @Test
    fun crossingThresholdOnlyArmsAndReleaseRequestsOnce() {
        val state = CalendarEndPullState(72f)
        state.pull(-200f, atEnd = true, enabled = true)
        assertTrue(state.isArmed)
        assertTrue(state.release())
        assertFalse(state.release())
    }

    @Test
    fun userCanPullBeyondThresholdAndReverseToCancelBeforeRelease() {
        val state = CalendarEndPullState(72f)
        state.pull(-300f, atEnd = true, enabled = true)
        assertTrue(state.distance > 72f)
        assertTrue(state.isArmed)
        state.retract(200f)
        assertFalse(state.isArmed)
        assertFalse(state.release())
    }

    @Test
    fun canceledGestureResetsWithoutRequestingEvenWhenArmed() {
        val state = CalendarEndPullState(72f)
        state.pull(-200f, atEnd = true, enabled = true)
        state.reset()
        assertFalse(state.release())
    }
}
