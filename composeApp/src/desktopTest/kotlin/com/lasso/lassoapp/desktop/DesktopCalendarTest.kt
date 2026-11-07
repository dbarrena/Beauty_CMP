package com.lasso.lassoapp.desktop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.model.Event
import com.lasso.lassoapp.screens.calendar.CalendarEvents
import kotlinx.datetime.*
import org.junit.Rule
import org.junit.Test
import kotlin.test.*

class DesktopCalendarTest {
    @get:Rule val compose = createComposeRule()

    @Test fun monthPickerCrossesYearBoundaryAndUsesMondayFirst() {
        val dates = monthGridDates(LocalDate(2027, 1, 15))
        assertEquals(LocalDate(2026, 12, 28), dates.first())
        assertEquals(DayOfWeek.MONDAY, dates.first().dayOfWeek)
        assertEquals(42, dates.size)
        assertEquals(31, dates.count { it.month == Month.JANUARY })
    }

    @Test fun mouseClickCreatesCorrectSlotAfterOuterTimelineScroll() {
        var selected: Int? = null
        var slotPixels = 0f
        compose.setContent {
            MaterialTheme {
                val scroll = rememberScrollState(initial = 160)
                slotPixels = with(LocalDensity.current) { 20.dp.toPx() }
                Box(Modifier.width(300.dp).height(400.dp).testTag("viewport").verticalScroll(scroll)) {
                    CalendarEvents(80.dp, scroll, emptyList(), onEmptySlotClick = { selected = it },
                        scrollInternally = false, modifier = Modifier.width(300.dp).height(1120.dp))
                }
            }
        }
        compose.onNodeWithTag("viewport").performMouseInput { click(Offset(120f, slotPixels)) }
        compose.runOnIdle {
            // scroll is 160 physical pixels; obtain density-independent expected value from slotPixels.
            val minutes = 420 + (((160f + slotPixels) / (slotPixels * 2)).toInt() * 30)
            assertEquals(minutes, selected)
        }
    }

    @Test fun mouseDragSnapsWithoutLongPressAndClickStillEdits() {
        var moved: Int? = null
        var edits = 0
        var hourPixels = 0f
        val event = Event(1, "Ana", Color(0xFF00D1AD), LocalDateTime(2026, 11, 7, 8, 0),
            LocalDateTime(2026, 11, 7, 9, 0), "Manicure", 1)
        compose.setContent {
            MaterialTheme {
                hourPixels = with(LocalDensity.current) { 80.dp.toPx() }
                CalendarEvents(80.dp, rememberScrollState(), listOf(event),
                    modifier = Modifier.width(300.dp).height(600.dp), dragEnabled = true,
                    dragAfterLongPress = false, onEditAppointment = { edits++ }, onAppointmentMoved = { _, minutes -> moved = minutes })
            }
        }
        compose.onNodeWithText("Ana").performMouseInput { click() }
        compose.runOnIdle { assertEquals(1, edits) }
        compose.onNodeWithText("Ana").performMouseInput {
            moveTo(center)
            press()
            moveBy(Offset(0f, hourPixels / 2), delayMillis = 100)
            moveBy(Offset(0f, hourPixels / 2), delayMillis = 100)
            release()
        }
        compose.runOnIdle {
            assertNotNull(moved)
            assertEquals(0, moved!! % 15)
            assertTrue(moved!! > 480)
            assertEquals(1, edits)
        }
    }
}
