package com.lasso.lassoapp.desktop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.data.local.dao.SessionDao
import com.lasso.lassoapp.data.local.session.SessionRepository
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.model.*
import com.lasso.lassoapp.model.room.Session
import com.lasso.lassoapp.screens.calendar.CalendarScreenViewModel
import com.lasso.lassoapp.screens.login.LoginScreenViewModel
import kotlinx.datetime.*
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import java.io.File
import java.lang.reflect.Proxy
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Clock

/** Exercises the real desktop shell and shared ViewModels with an isolated in-memory account. */
@OptIn(ExperimentalTestApi::class)
class DesktopAppFlowTest {
    @Test fun loginCalendarFiltersDialogsNavigationAndLogout() = runDesktopComposeUiTest(width = 1440, height = 900) {
        val compose = this
        val employee = Employee(1, "Ana García", "ana@example.test", role = "administrador", partnerId = 7, updatedAt = null,
            partners = Partner(7, "Lasso Studio", "studio@example.test"))
        var session: Session? = null
        val dao = object : SessionDao {
            override suspend fun insert(value: Session): Long { session = value; return 1 }
            override suspend fun deleteAll(): Int { session = null; return 1 }
            override suspend fun getSession() = session
            override suspend fun getSessions() = listOfNotNull(session)
        }
        val api = Proxy.newProxyInstance(LassoApi::class.java.classLoader, arrayOf(LassoApi::class.java)) { _, method, args ->
            when (method.name) {
                "login" -> if ((args[0] as Login).password == "test-password") LoginResponse(employee = employee, token = "test-token")
                    else LoginResponse(error = "Correo o contraseña incorrectos.")
                "getAppointmentCalendar" -> {
                    val date = Instant.fromEpochMilliseconds(args[0] as Long).toLocalDateTime(TimeZone.currentSystemDefault()).date
                    fun time(hour: Int, minute: Int = 0) = LocalDateTime(date, LocalTime(hour, minute))
                        .toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                    AppointmentCalendarResponse(args[0] as Long, args[1] as Long, listOf(
                        EmployeeAppointmentSchedule(1, "Ana García", listOf(
                            CalendarAppointment(1, AppointmentClientSummary(1, "Mariana López"), listOf(AppointmentServiceSummary(1, "Manicure · Gel")), time(10), time(11)),
                            CalendarAppointment(2, AppointmentClientSummary(2, "Sofía Torres"), listOf(AppointmentServiceSummary(2, "Pedicure")), time(12), time(13)),
                        ), "#00D1AD", blockedHours = listOf(CalendarBlock(50, 1, time(14), time(15)))),
                        EmployeeAppointmentSchedule(2, "Beatriz Ruiz", listOf(
                            CalendarAppointment(3, AppointmentClientSummary(3, "Valeria Ramos"), listOf(AppointmentServiceSummary(3, "Corte y peinado")), time(10, 30), time(12)),
                        ), "#AFBBF2"),
                        EmployeeAppointmentSchedule(3, "Carolina Díaz", listOf(
                            CalendarAppointment(4, AppointmentClientSummary(4, "Camila Vega"), listOf(AppointmentServiceSummary(4, "Diseño de cejas")), time(11), time(11, 30)),
                        ), "#FF7A5F"),
                    ))
                }
                "getEmployees" -> listOf(employee)
                "getClients", "getServices" -> emptyList<Any>()
                else -> error("Unexpected call: ${method.name}")
            }
        } as LassoApi
        startKoin { modules(module {
            single { SessionRepository(dao) }
            single<LassoApi> { api }
            factory { LoginScreenViewModel(get(), get()) }
            factory { CalendarScreenViewModel(get(), get()) }
        }) }
        try {
            compose.setContent {
                CompositionLocalProvider(LocalDensity provides Density(1f)) {
                    Box(Modifier.requiredSize(1440.dp, 900.dp)) { DesktopApp() }
                }
            }
            compose.waitUntil(timeoutMillis = 10_000) { compose.onAllNodesWithText("Iniciar sesión").fetchSemanticsNodes().isNotEmpty() }
            capture("desktop-login")
            compose.onAllNodes(hasSetTextAction())[0].performTextInput("ana@example.test")
            compose.onAllNodes(hasSetTextAction())[1].performTextInput("wrong")
            compose.onNodeWithText("Iniciar sesión").performClick()
            compose.onNodeWithText("Correo o contraseña incorrectos.").assertIsDisplayed()
            compose.onAllNodes(hasSetTextAction())[1].performTextClearance()
            compose.onAllNodes(hasSetTextAction())[1].performTextInput("test-password")
            compose.onNodeWithText("Iniciar sesión").performClick()
            compose.waitUntil(timeoutMillis = 10_000) { compose.onAllNodesWithText("Mariana López").fetchSemanticsNodes().isNotEmpty() }
            compose.onNodeWithText("Mariana López").assertIsDisplayed()
            compose.onAllNodesWithContentDescription("Lasso")[0].assertIsDisplayed()
            compose.onAllNodes(hasText("GMT", substring = true)).assertCountEquals(0)
            compose.onNodeWithContentDescription("Bloqueo de calendario, 14:00 - 15:00").assertExists()
            compose.onNodeWithContentDescription("Calendario").assertIsDisplayed().assertIsSelected()
            compose.onNodeWithContentDescription("Calendario").performMouseInput { moveTo(center) }
            compose.mainClock.advanceTimeBy(1200)
            compose.onNodeWithText("Calendario").assertIsDisplayed()
            compose.onAllNodes(isRoot())[0].performMouseInput { moveTo(androidx.compose.ui.geometry.Offset(1000f, 400f)) }
            compose.onNodeWithContentDescription("Abrir menú de navegación").assertDoesNotExist()
            compose.onNodeWithText("Mi equipo").assertIsDisplayed()
            capture("desktop-calendar")
            compose.onAllNodesWithText("Beatriz Ruiz")[0].performClick()
            compose.onNodeWithText("Valeria Ramos").assertDoesNotExist()
            compose.onNodeWithText("Mariana López").performClick()
            compose.onNodeWithText("Guardar").assertExists()
            capture("desktop-appointment")
            compose.onNodeWithText("Cancelar").performClick()
            compose.onNodeWithText("Día").performClick()
            compose.onNodeWithText("3 días").performClick()
            compose.waitForIdle()
            compose.onAllNodes(hasContentDescription("Bloqueo de calendario", substring = true)).assertCountEquals(0)
            capture("desktop-three-days")
            compose.onNodeWithContentDescription("Rango siguiente").performClick()
            compose.waitForIdle()
            val nextDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.plus(3, DateTimeUnit.DAY)
            compose.onNodeWithText(nextDate.day.toString(), useUnmergedTree = true).assertExists()
            compose.onNodeWithText("Crear").performClick()
            compose.onNodeWithText("Bloquear horario").performClick()
            compose.onNodeWithText("Bloqueo de calendario").assertIsDisplayed()
            compose.onNodeWithContentDescription("Cerrar").performClick()
            compose.onNodeWithText("A", substring = false).performClick()
            compose.onNodeWithText("Cerrar sesión").performClick()
            compose.waitUntil(timeoutMillis = 10_000) { compose.onAllNodesWithText("Iniciar sesión").fetchSemanticsNodes().isNotEmpty() }
            assertNull(session)
        } finally {
            stopKoin()
        }
    }

    private fun ComposeUiTest.capture(name: String) {
        waitForIdle()
        val roots = onAllNodes(isRoot())
        val image = roots[roots.fetchSemanticsNodes().lastIndex].captureToImage().asSkiaBitmap()
        val directory = File("build/desktop-screenshots").apply { mkdirs() }
        org.jetbrains.skia.Image.makeFromBitmap(image).encodeToData()?.use { data ->
            File(directory, "$name.png").writeBytes(data.bytes)
        }
    }
}
