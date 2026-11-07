package com.lasso.lassoapp

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lasso.lassoapp.screens.calendar.CalendarAppointmentAutoScroll
import com.lasso.lassoapp.screens.calendar.CalendarAppointmentFocus
import com.lasso.lassoapp.screens.calendar.CalendarViewMode
import com.lasso.lassoapp.screens.calendar.firstAppointmentFocus
import com.lasso.lassoapp.model.Event
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CalendarAppointmentFocusUiTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun forwardDateGestureFocusesAfterRangeAnimationFinishes() {
        assertFocusAfterRangeTransition(backward = false)
    }

    @Test
    fun backwardDateGestureKeepsLastColumnAndUsesEarliestTimeAcrossRange() {
        assertFocusAfterRangeTransition(backward = true)
    }

    private fun assertFocusAfterRangeTransition(backward: Boolean) {
        val revision = mutableStateOf(0)
        val transitioning = mutableStateOf(true)
        val outgoingHorizontal = ScrollState(400)
        val incomingHorizontal = ScrollState(800)
        val activeHorizontal = mutableStateOf(outgoingHorizontal)
        val vertical = ScrollState(0)
        val dates = listOf(LocalDate(2026, 11, 7), LocalDate(2026, 11, 8), LocalDate(2026, 11, 9))
        val events = dates.mapIndexed { index, date ->
            val start = LocalDateTime(date.year, date.month, date.day, 11 + index * 2, 0)
            Event(appointmentId = index + 1, color = Color.White, start = start, end = start)
        }
        compose.setContent {
            Box(Modifier.size(200.dp).horizontalScroll(activeHorizontal.value).verticalScroll(vertical)) {
                Box(Modifier.size(1500.dp))
            }
            CalendarAppointmentAutoScroll(
                loadRevision = revision.value,
                selectedEmployeeId = null,
                enabled = !transitioning.value,
                target = firstAppointmentFocus(events, dates, emptyList(), CalendarViewMode.ThreeDays),
                columnWidthPx = 200f,
                hourHeightPx = 100f,
                horizontalScrollState = activeHorizontal.value,
                verticalScrollState = vertical,
                keepLastColumn = backward,
            )
        }
        compose.mainClock.autoAdvance = false
        // The appointments have loaded, but the date range still owns the horizontal animation.
        compose.runOnIdle { revision.value = 1 }
        compose.mainClock.advanceTimeBy(700)
        compose.runOnIdle {
            assertEquals(400, outgoingHorizontal.value)
            assertEquals(0, vertical.value)
            if (backward) activeHorizontal.value = incomingHorizontal
            transitioning.value = false
        }
        compose.mainClock.advanceTimeBy(160)
        compose.runOnIdle {
            assertTrue(vertical.value > 0)
            assertTrue(vertical.value < 375)
        }
        compose.mainClock.advanceTimeBy(600)
        compose.runOnIdle {
            assertEquals(if (backward) activeHorizontal.value.maxValue else 0, activeHorizontal.value.value)
            assertEquals(375, vertical.value)
            assertEquals(1, revision.value)
        }
    }

    @Test
    fun delayedLoadAnimatesToAppointmentTimeEvenInFirstColumnAndRefocusesOnReload() {
        val revision = mutableStateOf(0)
        val target = mutableStateOf(CalendarAppointmentFocus(0, 10 * 60))
        val horizontal = ScrollState(0)
        val vertical = ScrollState(0)
        compose.setContent {
            Box(Modifier.size(200.dp).horizontalScroll(horizontal).verticalScroll(vertical)) {
                Box(Modifier.size(1500.dp))
            }
            CalendarAppointmentAutoScroll(
                loadRevision = revision.value,
                selectedEmployeeId = null,
                enabled = revision.value > 0,
                target = target.value,
                columnWidthPx = 200f,
                hourHeightPx = 100f,
                horizontalScrollState = horizontal,
                verticalScrollState = vertical,
            )
        }
        compose.runOnIdle { assertEquals(0, vertical.value) }
        compose.mainClock.autoAdvance = false
        compose.runOnIdle { revision.value = 1 }
        compose.mainClock.advanceTimeBy(160)
        compose.runOnIdle {
            assertTrue(vertical.value > 0)
            assertTrue(vertical.value < 275)
        }
        compose.mainClock.advanceTimeBy(600)
        compose.runOnIdle {
            assertEquals(0, horizontal.value)
            assertEquals(275, vertical.value)
            target.value = CalendarAppointmentFocus(2, 12 * 60)
            revision.value = 2
        }
        compose.mainClock.advanceTimeBy(700)
        compose.runOnIdle {
            assertEquals(400, horizontal.value)
            assertEquals(475, vertical.value)
        }
    }
}
