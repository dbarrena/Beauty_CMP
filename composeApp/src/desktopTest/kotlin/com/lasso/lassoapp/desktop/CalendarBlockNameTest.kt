package com.lasso.lassoapp.desktop

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.model.CalendarBlock
import com.lasso.lassoapp.model.CalendarBlockWriteRequest
import com.lasso.lassoapp.model.Employee
import com.lasso.lassoapp.model.Event
import com.lasso.lassoapp.screens.calendar.CalendarEventItem
import com.lasso.lassoapp.screens.calendar.dialog.CalendarBlockDialog
import com.lasso.lassoapp.ui.theme.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class CalendarBlockNameTest {
    @Test fun calendarShowsNameAndExistingBlockCanBeRenamedOrCleared() = runDesktopComposeUiTest(width = 420, height = 820) {
        var saved: CalendarBlockWriteRequest? = null
        val date = LocalDate(2026, 11, 12)
        val block = CalendarBlock(50, 8,
            LocalDateTime(2026, 11, 12, 10, 0).toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
            LocalDateTime(2026, 11, 12, 11, 0).toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(), title = "Comida")
        val event = Event(name = "Comida y descanso del equipo", color = LassoGray, employeeId = 8, calendarBlockId = 50,
            start = LocalDateTime(2026, 11, 12, 10, 0), end = LocalDateTime(2026, 11, 12, 11, 0))
        setContent {
            CompositionLocalProvider(LocalDensity provides Density(1f)) {
                MaterialTheme(colorScheme = LightLassoColorScheme, typography = lassoTypography()) {
                    var open by remember { mutableStateOf(false) }
                    Box(Modifier.requiredSize(420.dp, 820.dp)) {
                        Box(Modifier.width(220.dp).height(100.dp)) { CalendarEventItem(event, onEdit = { open = true }) }
                        if (open) CalendarBlockDialog(date, 8,
                            listOf(Employee(8, "Ana", "ana@example.test", role = "empleado", partnerId = 7, updatedAt = null)),
                            false, false, null, onDismiss = { open = false }, onRetryEmployees = {},
                            onSave = { saved = it }, block = block)
                    }
                }
            }
        }
        val titleLayouts = mutableListOf<TextLayoutResult>()
        onNodeWithText(event.name!!).assertIsDisplayed().performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(titleLayouts) }
        assertTrue(titleLayouts.single().lineCount > 1)
        capture("calendar-block-wrapped-title")
        onNodeWithContentDescription("${event.name}, 10:00 - 11:00").performClick()
        onNode(hasSetTextAction()).assertTextContains("Comida")
        onNode(hasSetTextAction()).performTextReplacement("Descanso")
        capture("calendar-block-name")
        onNodeWithText("Guardar").performClick()
        assertEquals("Descanso", saved?.title)
        onNode(hasSetTextAction()).performTextClearance()
        onNodeWithText("Guardar").performClick()
        assertEquals(null, saved?.title)
        onNodeWithContentDescription("Eliminar bloqueo").assertIsDisplayed()
    }

    private fun ComposeUiTest.capture(name: String) {
        waitForIdle()
        val roots = onAllNodes(isRoot())
        val bitmap = roots[roots.fetchSemanticsNodes().lastIndex].captureToImage().asSkiaBitmap()
        val directory = File("build/calendar-screenshots").apply { mkdirs() }
        org.jetbrains.skia.Image.makeFromBitmap(bitmap).encodeToData()?.use { File(directory, "$name.png").writeBytes(it.bytes) }
    }
}
