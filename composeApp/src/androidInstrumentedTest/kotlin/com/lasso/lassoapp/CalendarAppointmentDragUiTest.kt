package com.lasso.lassoapp

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lasso.lassoapp.model.Event
import com.lasso.lassoapp.screens.calendar.CalendarAppointmentDrag
import com.lasso.lassoapp.screens.calendar.CalendarEvents
import com.lasso.lassoapp.screens.calendar.CalendarHourSidebar
import com.lasso.lassoapp.screens.calendar.CalendarRangeScroller
import kotlinx.datetime.LocalDateTime
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CalendarAppointmentDragUiTest {
    @get:Rule val compose = createComposeRule()
    private val drag = mutableStateOf<CalendarAppointmentDrag?>(null)
    private var haptics = 0
    private var droppedStart: Int? = null
    private var edits = 0
    private var halfHourPixels = 0f

    private fun showCalendar() {
        compose.setContent {
            halfHourPixels = with(LocalDensity.current) { 45.dp.toPx() }
            CompositionLocalProvider(LocalHapticFeedback provides object : HapticFeedback {
                override fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) { haptics++ }
            }) {
                MaterialTheme {
                    val scroll = rememberScrollState()
                    Row(Modifier.width(320.dp).height(500.dp)) {
                        CalendarHourSidebar(90.dp, Modifier.width(60.dp).verticalScroll(scroll),
                            draggedStartMinutes = drag.value?.startMinutes)
                        CalendarRangeScroller(
                            scrollState = rememberScrollState(),
                            enabled = drag.value == null,
                            isLoading = false,
                            isLoadingNextRange = false,
                            isLoadingPreviousRange = false,
                            onNextRange = { error("Dragging an appointment must not change dates") },
                            onPreviousRange = { error("Dragging an appointment must not change dates") },
                            modifier = Modifier.width(260.dp),
                            userScrollEnabled = drag.value == null,
                        ) {
                          CalendarEvents(
                            hourHeight = 90.dp,
                            verticalScrollState = scroll,
                            events = listOf(Event(10, "Ana", Color(0xFF00D1AD),
                                LocalDateTime(2026, 11, 6, 10, 0), LocalDateTime(2026, 11, 6, 11, 0), employeeId = 1)),
                            onEditAppointment = { edits++ },
                            modifier = Modifier.width(260.dp),
                            dragEnabled = true,
                            onDragChanged = { drag.value = it },
                            onAppointmentMoved = { _, start -> droppedStart = start },
                          )
                        }
                    }
                }
            }
        }
    }

    @Test
    fun longPressGivesHapticAndShowsSnappedPreviewTimeAndOriginalPlaceholder() {
        showCalendar()
        compose.onNodeWithText("Ana").performTouchInput {
            down(center)
            advanceEventTime(700)
            moveBy(Offset(0f, halfHourPixels))
        }
        compose.runOnIdle {
            assertEquals(1, haptics)
            assertEquals(630, drag.value?.startMinutes)
        }
        compose.onNodeWithText("10:30").assertIsDisplayed()
        compose.onNodeWithText("10:30 - 11:30").assertIsDisplayed()
        compose.onAllNodes(isNotEnabled()).assertCountEquals(1)
        compose.onNodeWithText("Ana").performTouchInput { up() }
        compose.runOnIdle {
            assertEquals(630, droppedStart)
            assertNull(drag.value)
            assertEquals(0, edits)
        }
        compose.onAllNodes(isNotEnabled()).assertCountEquals(0)
    }

    @Test
    fun cancelingDragRestoresOriginalAndDoesNotSave() {
        showCalendar()
        compose.onNodeWithText("Ana").performTouchInput {
            down(center)
            advanceEventTime(700)
            moveBy(Offset(0f, halfHourPixels))
            cancel()
        }
        compose.runOnIdle {
            assertNull(drag.value)
            assertNull(droppedStart)
            assertEquals(0, edits)
        }
        compose.onNodeWithText("10:00 - 11:00").assertIsDisplayed()
    }

    @Test
    fun tapStillOpensAppointmentWithoutStartingDrag() {
        showCalendar()
        compose.onNodeWithText("Ana").performTouchInput { click() }
        compose.runOnIdle {
            assertEquals(1, edits)
            assertEquals(0, haptics)
            assertNull(droppedStart)
        }
    }

    @Test
    fun ordinaryVerticalSwipeScrollsWithoutMovingAppointment() {
        showCalendar()
        compose.onNodeWithText("Ana").performTouchInput {
            swipe(start = center, end = center - Offset(0f, halfHourPixels), durationMillis = 150)
        }
        compose.runOnIdle {
            assertEquals(0, haptics)
            assertNull(drag.value)
            assertNull(droppedStart)
            assertEquals(0, edits)
        }
    }
}
