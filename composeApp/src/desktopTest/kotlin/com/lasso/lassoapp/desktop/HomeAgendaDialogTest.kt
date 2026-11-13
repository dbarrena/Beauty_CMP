package com.lasso.lassoapp.desktop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.lasso.lassoapp.data.local.dao.SessionDao
import com.lasso.lassoapp.data.local.session.SessionRepository
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.model.*
import com.lasso.lassoapp.model.room.Session
import com.lasso.lassoapp.screens.calendar.CalendarScreenViewModel
import com.lasso.lassoapp.screens.home.HomeScreenViewModel
import com.lasso.lassoapp.screens.home.v2.HomeScreenV2
import com.lasso.lassoapp.ui.theme.*
import kotlinx.datetime.*
import org.junit.Test
import org.koin.compose.KoinIsolatedContext
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import java.lang.reflect.Proxy
import kotlin.test.*
import kotlin.time.Clock

@OptIn(ExperimentalTestApi::class)
class HomeAgendaDialogTest {
    @Test fun homeCardsAlignOpenCorrectAppointmentAndRefreshAfterSaveAndDelete() = runDesktopComposeUiTest(width = 420, height = 900) {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val startsAt = LocalDateTime(today, LocalTime(10, 0)).toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        var appointment: HomeAppointment? = HomeAppointment(10, AppointmentClientSummary(4, "Mariana"),
            HomeAppointmentEmployee(8, "Ana"), listOf(AppointmentServiceSummary(3, "Corte")), startsAt, startsAt + 3_600_000, "Nota de la cita")
        var homeLoads = 0
        var editedId: Int? = null
        var edited: AppointmentWriteRequest? = null
        var deletedId: Int? = null
        val api = Proxy.newProxyInstance(LassoApi::class.java.classLoader, arrayOf(LassoApi::class.java)) { _, method, args ->
            when (method.name) {
                "getHome" -> {
                    homeLoads++
                    Home(0, "$0.00", 0, 0, 0, numberOfAppointments = if (appointment == null) 0 else 1, appointments = listOfNotNull(appointment))
                }
                "getAppointmentCalendar" -> AppointmentCalendarResponse(args[0] as Long, args[1] as Long, emptyList())
                "getClients" -> listOf(Client(4, "Mariana", partnerId = 7))
                "getEmployees" -> listOf(Employee(8, "Ana", "ana@example.test", role = "administrador", partnerId = 7, updatedAt = null))
                "getServices" -> listOf(Service("Corte", id = 3, price = "100", commissionPercentage = "0"))
                "editAppointment" -> {
                    editedId = args[0] as Int
                    val input = args[1] as AppointmentWriteRequest
                    edited = input
                    appointment = appointment!!.copy(notes = input.notes)
                    SavedAppointment(10, 7, input.employeeId, startsAt = input.startsAt, endsAt = input.endsAt)
                }
                "deleteAppointment" -> { deletedId = args[0] as Int; appointment = null; MessageResponse("Eliminada") }
                else -> error("Unexpected home API call: ${method.name}")
            }
        } as LassoApi
        val session = Session(partnerId = 7, employeeId = 8, partnerName = "Mi salón", employeeName = "Ana", employeeRole = "administrador", token = "test-token")
        val repository = SessionRepository(object : SessionDao {
            override suspend fun insert(session: Session) = 1L
            override suspend fun deleteAll() = 1
            override suspend fun getSession() = session
            override suspend fun getSessions() = listOf(session)
        })
        val koin = koinApplication { modules(module {
            factory { HomeScreenViewModel(api, repository) }
            factory { CalendarScreenViewModel(api, repository) }
        }) }
        val owner = object : ViewModelStoreOwner { override val viewModelStore = ViewModelStore() }
        try {
            setContent {
                KoinIsolatedContext(koin) {
                    CompositionLocalProvider(LocalDensity provides Density(1f), LocalViewModelStoreOwner provides owner) {
                        MaterialTheme(colorScheme = LightLassoColorScheme, typography = lassoTypography()) {
                            Box(Modifier.requiredSize(420.dp, 900.dp)) { HomeScreenV2({}, {}, {}, {}, {}) }
                        }
                    }
                }
            }
            waitUntil(timeoutMillis = 10_000) { onAllNodesWithText("Agenda de hoy").fetchSemanticsNodes().isNotEmpty() }
            onNodeWithText("Agenda de hoy").performScrollTo()
            val headingLeft = onNodeWithText("Agenda de hoy").fetchSemanticsNode().boundsInRoot.left
            assertEquals(onNodeWithText("Acciones rápidas").fetchSemanticsNode().boundsInRoot.left, headingLeft)
            onNodeWithText("Mariana").performScrollTo()
            assertEquals(headingLeft, onNode(hasClickAction() and hasText("Mariana")).fetchSemanticsNode().boundsInRoot.left)
            onNodeWithText("Mariana").performClick()
            onNodeWithText("Editar cita").assertIsDisplayed()
            onNode(hasSetTextAction()).performScrollTo().performTextReplacement("Editada desde inicio")
            onNodeWithText("Guardar").performClick()
            waitUntil(timeoutMillis = 10_000) { editedId != null && homeLoads >= 2 }
            assertEquals(10, editedId)
            assertEquals(8, edited?.employeeId)
            assertEquals(listOf(3), edited?.serviceIds)
            assertEquals("Editada desde inicio", edited?.notes)
            onNodeWithText("Mariana").performScrollTo().performClick()
            onNodeWithText("Editar cita").assertIsDisplayed()
            onNodeWithContentDescription("Eliminar cita").performClick()
            waitUntil(timeoutMillis = 10_000) { deletedId != null && homeLoads >= 3 }
            assertEquals(10, deletedId)
            onNodeWithText("No hay citas para hoy").assertExists()
        } finally { owner.viewModelStore.clear(); koin.close() }
    }
}
