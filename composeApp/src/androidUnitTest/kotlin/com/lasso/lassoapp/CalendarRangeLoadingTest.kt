package com.lasso.lassoapp

import androidx.lifecycle.ViewModelStore
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.data.local.dao.SessionDao
import com.lasso.lassoapp.data.local.session.SessionRepository
import com.lasso.lassoapp.model.room.Session
import com.lasso.lassoapp.model.AppointmentCalendarResponse
import com.lasso.lassoapp.model.CalendarAppointment
import com.lasso.lassoapp.model.EmployeeAppointmentSchedule
import com.lasso.lassoapp.model.AppointmentClientSummary
import com.lasso.lassoapp.model.AppointmentServiceSummary
import com.lasso.lassoapp.model.AppointmentWriteRequest
import com.lasso.lassoapp.model.SavedAppointment
import com.lasso.lassoapp.model.CalendarBlock
import com.lasso.lassoapp.model.CalendarBlockWriteRequest
import com.lasso.lassoapp.model.Employee
import com.lasso.lassoapp.model.MessageResponse
import com.lasso.lassoapp.model.Client
import com.lasso.lassoapp.model.ClientWriteRequest
import com.lasso.lassoapp.screens.calendar.CalendarScreenViewModel
import com.lasso.lassoapp.screens.calendar.CalendarViewMode
import com.lasso.lassoapp.screens.calendar.CalendarRangeDirection
import com.lasso.lassoapp.screens.calendar.appointmentDayRange
import java.lang.reflect.Proxy
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.startCoroutine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarRangeLoadingTest {
    private val selectedDate = LocalDate(2026, 11, 6)

    private class Request(val start: Long, val end: Long) {
        val response = CompletableDeferred<AppointmentCalendarResponse>()

        fun succeed(blocks: List<CalendarBlock> = emptyList()) {
            response.complete(AppointmentCalendarResponse(start, end, listOf(
                EmployeeAppointmentSchedule(1, "Ana", listOf(
                    CalendarAppointment(10, client = AppointmentClientSummary(4, "Cliente"),
                        services = listOf(AppointmentServiceSummary(3, "Manicure")), notes = "Nota de la cita",
                        startsAt = start + 10 * 60 * 60 * 1000L, endsAt = start + 11 * 60 * 60 * 1000L),
                ), blockedHours = blocks),
                EmployeeAppointmentSchedule(2, "Bea", listOf(
                    CalendarAppointment(11, startsAt = start + 12 * 60 * 60 * 1000L, endsAt = start + 13 * 60 * 60 * 1000L),
                )),
            )))
        }
    }

    private class MoveRequest(val id: Int, val body: AppointmentWriteRequest) {
        val response = CompletableDeferred<SavedAppointment>()
        fun succeed() = response.complete(SavedAppointment(id, 7, body.employeeId,
            startsAt = body.startsAt, endsAt = body.endsAt))
    }

    private class BlockRequest(val body: CalendarBlockWriteRequest) {
        val response = CompletableDeferred<CalendarBlock>()
        fun succeed() = response.complete(CalendarBlock(50, body.employeeId, body.startTime, body.endTime,
            body.startDate, body.endDate, 7))
    }

    /** JVM proxy keeps unrelated Lasso endpoints out of this calendar-only fake. */
    private class FakeApi {
        val requests = mutableListOf<Request>()
        val moves = mutableListOf<MoveRequest>()
        val blocks = mutableListOf<BlockRequest>()
        val deletedBlocks = mutableListOf<Int>()
        var deleteBlockResponse = CompletableDeferred<MessageResponse>()
        val clientEdits = mutableListOf<Pair<Int, ClientWriteRequest>>()
        var clientEditResponse = CompletableDeferred<Client>()
        @Suppress("UNCHECKED_CAST")
        val api = Proxy.newProxyInstance(
            LassoApi::class.java.classLoader,
            arrayOf(LassoApi::class.java),
        ) { _, method, args ->
            when (method.name) {
                "getAppointmentCalendar" -> {
                    val request = Request(args[0] as Long, args[1] as Long)
                    requests += request
                    val waitForResponse: suspend () -> AppointmentCalendarResponse = { request.response.await() }
                    waitForResponse.startCoroutine(args[2] as Continuation<AppointmentCalendarResponse>)
                    COROUTINE_SUSPENDED
                }
                "editAppointment" -> {
                    val request = MoveRequest(args[0] as Int, args[1] as AppointmentWriteRequest)
                    moves += request
                    val waitForResponse: suspend () -> SavedAppointment = { request.response.await() }
                    waitForResponse.startCoroutine(args[2] as Continuation<SavedAppointment>)
                    COROUTINE_SUSPENDED
                }
                "createCalendarBlock" -> {
                    val request = BlockRequest(args[0] as CalendarBlockWriteRequest)
                    blocks += request
                    val waitForResponse: suspend () -> CalendarBlock = { request.response.await() }
                    waitForResponse.startCoroutine(args[1] as Continuation<CalendarBlock>)
                    COROUTINE_SUSPENDED
                }
                "deleteCalendarBlock" -> {
                    deletedBlocks += args[0] as Int
                    val waitForResponse: suspend () -> MessageResponse = { deleteBlockResponse.await() }
                    waitForResponse.startCoroutine(args[1] as Continuation<MessageResponse>)
                    COROUTINE_SUSPENDED
                }
                "editClient" -> {
                    clientEdits += (args[0] as Int) to (args[1] as ClientWriteRequest)
                    val waitForResponse: suspend () -> Client = { clientEditResponse.await() }
                    waitForResponse.startCoroutine(args[2] as Continuation<Client>)
                    COROUTINE_SUSPENDED
                }
                "registerClient" -> {
                    val body = args[0] as ClientWriteRequest
                    Client(8, body.name, body.phone, body.email, body.notes, partnerId = 7)
                }
                "getEmployees" -> listOf(
                    Employee(1, "Ana", "ana@example.com", role = "empleado", partnerId = 7, updatedAt = null),
                    Employee(2, "Bea", "bea@example.com", role = "empleado", partnerId = 7, updatedAt = null),
                )
                "getClients", "getServices" -> emptyList<Any>()
                else -> error("Unexpected calendar API call: ${method.name}")
            }
        } as LassoApi
    }

    private fun calendarTest(
        role: String = "administrador",
        viewMode: CalendarViewMode = CalendarViewMode.ThreeDays,
        block: suspend TestScope.(FakeApi, CalendarScreenViewModel) -> Unit,
    ) = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val store = ViewModelStore()
        try {
            val api = FakeApi()
            val session = Session(partnerId = 7, employeeId = 1, partnerName = "Estética",
                employeeName = "Ana", employeeRole = role, token = "token")
            val repository = SessionRepository(object : SessionDao {
                override suspend fun getSession() = session
                override suspend fun getSessions() = listOf(session)
                override suspend fun insert(session: Session): Long = error("Unexpected session write")
                override suspend fun deleteAll(): Int = error("Unexpected session deletion")
            })
            val model = CalendarScreenViewModel(api.api, repository)
            store.put("calendar", model)
            model.onDateSelected(selectedDate)
            model.onViewModeSelected(viewMode)
            runCurrent()
            api.requests.single().succeed()
            runCurrent()
            block(api, model)
        } finally {
            store.clear()
            Dispatchers.resetMain()
        }
    }

    @Test
    fun successfulLoadsAdvanceFocusRevisionEvenWhenAppointmentsAreUnchanged() = calendarTest { api, model ->
        val revision = model.state.value.calendarLoadRevision
        assertTrue(revision > 0)
        model.retry()
        runCurrent()
        assertEquals(revision, model.state.value.calendarLoadRevision)
        api.requests.last().succeed()
        runCurrent()
        assertEquals(revision + 1, model.state.value.calendarLoadRevision)
        model.retry()
        runCurrent()
        api.requests.last().response.completeExceptionally(IllegalStateException("Sin conexión"))
        runCurrent()
        assertEquals(revision + 1, model.state.value.calendarLoadRevision)
    }

    @Test
    fun clientEditsReplaceExistingOptionsAndKeepTheAppointmentOpen() = calendarTest { api, model ->
        model.showNewAppointmentDialog()
        model.showNewClientDialog()
        model.saveClient(ClientWriteRequest(name = "Original", phone = "5551234567"))
        runCurrent()
        val original = model.state.value.createdClient!!

        model.showEditClientDialog(original)
        val request = ClientWriteRequest(name = "Actualizado", email = "cliente@example.com")
        model.saveClient(request)
        runCurrent()
        model.hideNewClientDialog()
        assertTrue(model.state.value.isNewClientDialogDisplayed)
        assertTrue(model.state.value.isSavingClient)
        assertEquals(listOf(original.id to request), api.clientEdits)

        val updated = original.copy(name = request.name, email = request.email, phone = null)
        api.clientEditResponse.complete(updated)
        runCurrent()
        assertEquals(listOf(updated), model.state.value.clients)
        assertEquals(updated, model.state.value.createdClient)
        assertEquals(null, model.state.value.editingClient)
        assertFalse(model.state.value.isNewClientDialogDisplayed)
        assertTrue(model.state.value.isAppointmentDialogDisplayed)
    }

    @Test
    fun failedClientEditPreservesOriginalAndAllowsRetry() = calendarTest { api, model ->
        val original = Client(4, "Original", partnerId = 7)
        model.showEditClientDialog(original)
        val request = ClientWriteRequest(name = "Actualizado")
        model.saveClient(request)
        runCurrent()
        api.clientEditResponse.completeExceptionally(IllegalStateException("Sin conexión"))
        runCurrent()
        assertEquals(original, model.state.value.editingClient)
        assertEquals("Sin conexión", model.state.value.clientSaveError)
        assertTrue(model.state.value.isNewClientDialogDisplayed)
        assertFalse(model.state.value.isSavingClient)

        api.clientEditResponse = CompletableDeferred()
        model.saveClient(request)
        runCurrent()
        val updated = original.copy(name = request.name)
        api.clientEditResponse.complete(updated)
        runCurrent()
        assertEquals(updated, model.state.value.createdClient)
        assertFalse(model.state.value.isNewClientDialogDisplayed)
        model.showNewClientDialog()
        assertEquals(null, model.state.value.editingClient)
    }

    @Test
    fun savedBlockCanOnlyBeDeletedAndCannotBeSubmittedAsAnEdit() = calendarTest { api, model ->
        val block = CalendarBlock(50, 1, 100, 200)
        model.retry()
        runCurrent()
        api.requests.last().succeed(listOf(block))
        runCurrent()
        model.showCalendarBlockDialog(50)
        runCurrent()
        assertEquals(block, model.state.value.selectedCalendarBlock)
        model.createCalendarBlock(CalendarBlockWriteRequest(1, 300, 400))
        runCurrent()
        assertTrue(api.blocks.isEmpty())
        model.deleteCalendarBlock()
        model.deleteCalendarBlock()
        model.hideCalendarBlockDialog()
        runCurrent()
        assertTrue(model.state.value.isCalendarBlockDialogDisplayed)
        assertTrue(model.state.value.isDeletingCalendarBlock)
        assertEquals(listOf(50), api.deletedBlocks)
        api.deleteBlockResponse.complete(MessageResponse("Eliminado"))
        runCurrent()
        assertFalse(model.state.value.isCalendarBlockDialogDisplayed)
        assertFalse(model.state.value.isDeletingCalendarBlock)
        assertEquals(null, model.state.value.selectedCalendarBlock)
        api.requests.last().succeed()
        runCurrent()
        assertTrue(model.state.value.events.none { it.calendarBlockId != null })
    }

    @Test
    fun failedBlockDeletePreservesReadOnlyDetailsAndCanBeRetried() = calendarTest { api, model ->
        val block = CalendarBlock(50, 1, 100, 200)
        model.retry()
        runCurrent()
        api.requests.last().succeed(listOf(block))
        runCurrent()
        model.showCalendarBlockDialog(50)
        runCurrent()
        model.deleteCalendarBlock()
        runCurrent()
        api.deleteBlockResponse.completeExceptionally(IllegalStateException("Sin conexión"))
        runCurrent()
        assertEquals(block, model.state.value.selectedCalendarBlock)
        assertTrue(model.state.value.isCalendarBlockDialogDisplayed)
        assertFalse(model.state.value.isDeletingCalendarBlock)
        assertEquals("Sin conexión", model.state.value.calendarBlockError)
        api.deleteBlockResponse = CompletableDeferred()
        model.deleteCalendarBlock()
        runCurrent()
        assertEquals(listOf(50, 50), api.deletedBlocks)
        api.deleteBlockResponse.complete(MessageResponse("Eliminado"))
        runCurrent()
        api.requests.last().succeed()
        runCurrent()
    }

    @Test
    fun calendarBlockSavePreventsDuplicatesAndDismissalThenRefreshesCalendar() = calendarTest { api, model ->
        model.showCalendarBlockDialog()
        runCurrent()
        assertEquals(listOf(1, 2), model.state.value.calendarBlockEmployees.map { it.id })
        val request = CalendarBlockWriteRequest(1, 100, 200, startDate = 0, endDate = 0)
        model.createCalendarBlock(request)
        model.createCalendarBlock(request)
        model.hideCalendarBlockDialog()
        runCurrent()
        assertTrue(model.state.value.isSavingCalendarBlock)
        assertTrue(model.state.value.isCalendarBlockDialogDisplayed)
        assertEquals(request, api.blocks.single().body)
        api.blocks.single().succeed()
        runCurrent()
        assertFalse(model.state.value.isSavingCalendarBlock)
        assertFalse(model.state.value.isCalendarBlockDialogDisplayed)
        assertEquals("Bloqueo de calendario agregado.", model.state.value.calendarBlockNotice)
        assertEquals(2, api.requests.size)
        api.requests.last().succeed()
        runCurrent()
    }

    @Test
    fun calendarBlockFailureKeepsDialogOpenAndAllowsRetry() = calendarTest { api, model ->
        model.showCalendarBlockDialog()
        runCurrent()
        val request = CalendarBlockWriteRequest(1, 100, 200)
        model.createCalendarBlock(request)
        runCurrent()
        api.blocks.single().response.completeExceptionally(IllegalStateException("Sin conexión"))
        runCurrent()
        assertTrue(model.state.value.isCalendarBlockDialogDisplayed)
        assertFalse(model.state.value.isSavingCalendarBlock)
        assertEquals("Sin conexión", model.state.value.calendarBlockError)
        model.createCalendarBlock(request)
        runCurrent()
        assertEquals(2, api.blocks.size)
        assertEquals(request, api.blocks.last().body)
        api.blocks.last().succeed()
        runCurrent()
        api.requests.last().succeed()
        runCurrent()
    }

    @Test
    fun calendarBlocksCannotTargetAnotherEmployeeFromEmployeeSessionOrUseInvalidRanges() = calendarTest(role = "empleado") { api, model ->
        model.showCalendarBlockDialog()
        runCurrent()
        assertEquals(listOf(1), model.state.value.calendarBlockEmployees.map { it.id })
        for (request in listOf(CalendarBlockWriteRequest(2, 100, 200), CalendarBlockWriteRequest(1, 200, 100),
            CalendarBlockWriteRequest(1, 100, 200, startDate = 200, endDate = 100))) {
            model.createCalendarBlock(request)
            runCurrent()
            assertTrue(api.blocks.isEmpty())
        }
        model.hideCalendarBlockDialog()
        model.createCalendarBlock(CalendarBlockWriteRequest(1, 100, 200))
        runCurrent()
        assertTrue(api.blocks.isEmpty())
    }

    @Test
    fun droppingAppointmentPreservesDetailsAndDurationAndUpdatesBothCalendarModels() = calendarTest { api, model ->
        val event = model.state.value.events.first { it.appointmentId == 10 }
        val original = model.state.value.employeeSchedules.first().appointments.first()
        model.moveAppointment(event, 630)
        model.moveAppointment(event, 645)
        assertTrue(model.state.value.isSavingAppointment)
        assertEquals(30, model.state.value.events.first { it.appointmentId == 10 }.start.minute)
        runCurrent()
        assertEquals(1, api.moves.size)
        val move = api.moves.single()
        assertEquals(10, move.id)
        assertEquals(1, move.body.employeeId)
        assertEquals(4, move.body.clientId)
        assertEquals(listOf(3), move.body.serviceIds)
        assertEquals(original.notes, move.body.notes)
        assertEquals(original.startsAt + 30 * 60_000, move.body.startsAt)
        assertEquals(original.endsAt + 30 * 60_000, move.body.endsAt)
        assertEquals(move.body.startsAt, model.state.value.employeeSchedules.first().appointments.first().startsAt)
        move.succeed()
        runCurrent()
        assertFalse(model.state.value.isSavingAppointment)
        assertEquals(1, api.requests.size)
    }

    @Test
    fun rejectedMoveRestoresOriginalPositionAndReportsError() = calendarTest { api, model ->
        val originalEvents = model.state.value.events
        val originalSchedules = model.state.value.employeeSchedules
        model.moveAppointment(originalEvents.first(), 630)
        runCurrent()
        api.moves.single().response.completeExceptionally(IllegalStateException("El empleado ya tiene una cita en ese horario"))
        runCurrent()
        assertEquals(originalEvents, model.state.value.events)
        assertEquals(originalSchedules, model.state.value.employeeSchedules)
        assertFalse(model.state.value.isSavingAppointment)
        assertEquals("El empleado ya tiene una cita en ese horario", model.state.value.error)
    }

    @Test
    fun unchangedAndInvalidDropsDoNotWrite() = calendarTest { api, model ->
        val event = model.state.value.events.first()
        for (start in listOf(600, 601, 400, 1245)) model.moveAppointment(event, start)
        runCurrent()
        assertTrue(api.moves.isEmpty())
        assertFalse(model.state.value.isSavingAppointment)
    }

    @Test
    fun dayViewMovesOneDayForwardAndBackAndIgnoresDuplicateRequests() = calendarTest(viewMode = CalendarViewMode.Day) { api, model ->
        val originalEvents = model.state.value.events
        model.loadNextRange()
        model.loadNextRange()
        model.loadPreviousRange()
        runCurrent()
        assertEquals(2, api.requests.size)
        assertEquals(originalEvents, model.state.value.events)
        val next = api.requests.last()
        assertEquals(appointmentDayRange(LocalDate(2026, 11, 7), TimeZone.currentSystemDefault()), next.start to next.end)
        next.succeed()
        runCurrent()
        assertEquals(LocalDate(2026, 11, 7), model.state.value.selectedDate)
        model.loadPreviousRange()
        runCurrent()
        val previous = api.requests.last()
        assertEquals(appointmentDayRange(selectedDate, TimeZone.currentSystemDefault()), previous.start to previous.end)
        previous.succeed()
        runCurrent()
        assertEquals(selectedDate, model.state.value.selectedDate)
    }

    @Test
    fun failedDayNavigationKeepsDateAndAppointmentsAndAllowsRetry() = calendarTest(viewMode = CalendarViewMode.Day) { api, model ->
        val originalEvents = model.state.value.events
        model.loadPreviousRange()
        runCurrent()
        api.requests.last().response.completeExceptionally(IllegalStateException("Sin conexión"))
        runCurrent()
        assertEquals(selectedDate, model.state.value.selectedDate)
        assertEquals(originalEvents, model.state.value.events)
        assertFalse(model.state.value.isLoadingPreviousRange)
        model.loadPreviousRange()
        runCurrent()
        api.requests.last().succeed()
        runCurrent()
        assertEquals(LocalDate(2026, 11, 5), model.state.value.selectedDate)
    }

    @Test
    fun changingViewDuringDayNavigationDiscardsTheStaleDayResponse() = calendarTest(viewMode = CalendarViewMode.Day) { api, model ->
        model.loadNextRange()
        runCurrent()
        val day = api.requests.last()
        model.onViewModeSelected(CalendarViewMode.ThreeDays)
        runCurrent()
        api.requests.last().succeed()
        day.succeed()
        runCurrent()
        assertEquals(selectedDate, model.state.value.selectedDate)
        assertEquals(CalendarViewMode.ThreeDays, model.state.value.viewMode)
        assertFalse(model.state.value.isLoading)
    }

    @Test
    fun employeeOnlySeesOwnScheduleAcrossBothViews() = calendarTest(role = "empleado") { api, model ->
        assertEquals(listOf(1), model.state.value.employeeSchedules.map { it.id })
        assertTrue(model.state.value.events.all { it.employeeId == 1 })
        model.onViewModeSelected(CalendarViewMode.Day)
        runCurrent()
        api.requests.last().succeed()
        runCurrent()
        assertEquals(listOf(1), model.state.value.employeeSchedules.map { it.id })
        assertTrue(model.state.value.events.all { it.employeeId == 1 })
    }

    @Test
    fun administratorSeesAllEmployeeSchedules() = calendarTest { _, model ->
        assertEquals(listOf(1, 2), model.state.value.employeeSchedules.map { it.id })
        assertEquals(setOf(1, 2), model.state.value.events.map { it.employeeId }.toSet())
    }

    @Test
    fun nextRangeLoadsExactlyOnceAndKeepsCurrentGridUntilSuccess() = calendarTest { api, model ->
        val originalEvents = model.state.value.events
        model.loadNextRange()
        model.loadNextRange()
        assertTrue(model.state.value.isLoadingNextRange)
        assertEquals(selectedDate, model.state.value.selectedDate)
        assertEquals(originalEvents, model.state.value.events)
        runCurrent()
        assertEquals(2, api.requests.size)
        val next = api.requests.last()
        val expected = appointmentDayRange(LocalDate(2026, 11, 9), TimeZone.currentSystemDefault(), 3)
        assertEquals(expected.first, next.start)
        assertEquals(expected.second, next.end)
        next.succeed()
        runCurrent()
        assertEquals(LocalDate(2026, 11, 9), model.state.value.selectedDate)
        assertFalse(model.state.value.isLoading)
        assertFalse(model.state.value.isLoadingNextRange)
        assertEquals(CalendarViewMode.ThreeDays, model.state.value.viewMode)
    }

    @Test
    fun failedNextRangeKeepsCurrentAppointmentsAndCanBeRetried() = calendarTest { api, model ->
        val originalEvents = model.state.value.events
        model.loadNextRange()
        runCurrent()
        api.requests.last().response.completeExceptionally(IllegalStateException("Sin conexión"))
        runCurrent()
        assertEquals(selectedDate, model.state.value.selectedDate)
        assertEquals(originalEvents, model.state.value.events)
        assertEquals("Sin conexión", model.state.value.error)
        assertFalse(model.state.value.isLoadingNextRange)
        model.loadNextRange()
        runCurrent()
        assertEquals(3, api.requests.size)
        api.requests.last().succeed()
        runCurrent()
        assertEquals(LocalDate(2026, 11, 9), model.state.value.selectedDate)
    }

    @Test
    fun switchingToDayCancelsPendingRangeAdvance() = calendarTest { api, model ->
        model.loadNextRange()
        runCurrent()
        val next = api.requests.last()
        model.onViewModeSelected(CalendarViewMode.Day)
        runCurrent()
        api.requests.last().succeed()
        next.succeed()
        runCurrent()
        assertEquals(CalendarViewMode.Day, model.state.value.viewMode)
        assertEquals(selectedDate, model.state.value.selectedDate)
        assertFalse(model.state.value.isLoadingNextRange)
        model.loadNextRange()
        runCurrent()
        assertEquals(4, api.requests.size)
        api.requests.last().succeed()
        runCurrent()
        assertEquals(LocalDate(2026, 11, 7), model.state.value.selectedDate)
    }

    @Test
    fun previousRangeLoadsOnceAndKeepsCurrentGridUntilSuccess() = calendarTest { api, model ->
        val originalEvents = model.state.value.events
        model.loadPreviousRange()
        model.loadPreviousRange()
        model.loadNextRange()
        assertTrue(model.state.value.isLoadingPreviousRange)
        assertFalse(model.state.value.isLoadingNextRange)
        assertEquals(selectedDate, model.state.value.selectedDate)
        assertEquals(originalEvents, model.state.value.events)
        runCurrent()
        assertEquals(2, api.requests.size)
        val previous = api.requests.last()
        val expected = appointmentDayRange(LocalDate(2026, 11, 3), TimeZone.currentSystemDefault(), 3)
        assertEquals(expected.first, previous.start)
        assertEquals(expected.second, previous.end)
        previous.succeed()
        runCurrent()
        assertEquals(LocalDate(2026, 11, 3), model.state.value.selectedDate)
        assertEquals(CalendarRangeDirection.Previous, model.state.value.calendarLoadDirection)
        assertFalse(model.state.value.isLoadingPreviousRange)
        assertFalse(model.state.value.isLoading)
        model.retry()
        runCurrent()
        api.requests.last().succeed()
        runCurrent()
        assertEquals(null, model.state.value.calendarLoadDirection)
    }

    @Test
    fun failedPreviousRangeKeepsCurrentAppointmentsAndCanBeRetried() = calendarTest { api, model ->
        val originalEvents = model.state.value.events
        model.loadPreviousRange()
        runCurrent()
        api.requests.last().response.completeExceptionally(IllegalStateException("Sin conexión"))
        runCurrent()
        assertEquals(selectedDate, model.state.value.selectedDate)
        assertEquals(originalEvents, model.state.value.events)
        assertFalse(model.state.value.isLoadingPreviousRange)
        model.loadPreviousRange()
        runCurrent()
        api.requests.last().succeed()
        runCurrent()
        assertEquals(LocalDate(2026, 11, 3), model.state.value.selectedDate)
    }

    @Test
    fun switchingToDayCancelsPendingBackwardRequest() = calendarTest { api, model ->
        model.loadPreviousRange()
        runCurrent()
        val previous = api.requests.last()
        model.onViewModeSelected(CalendarViewMode.Day)
        runCurrent()
        api.requests.last().succeed()
        previous.succeed()
        runCurrent()
        assertEquals(selectedDate, model.state.value.selectedDate)
        assertEquals(CalendarViewMode.Day, model.state.value.viewMode)
        assertFalse(model.state.value.isLoadingPreviousRange)
        model.loadPreviousRange()
        runCurrent()
        assertEquals(4, api.requests.size)
        api.requests.last().succeed()
        runCurrent()
        assertEquals(LocalDate(2026, 11, 5), model.state.value.selectedDate)
    }

    @Test
    fun navigatingForwardAndBackReturnsToOriginalRange() = calendarTest { api, model ->
        model.loadNextRange()
        runCurrent()
        api.requests.last().succeed()
        runCurrent()
        model.loadPreviousRange()
        runCurrent()
        api.requests.last().succeed()
        runCurrent()
        assertEquals(selectedDate, model.state.value.selectedDate)
        assertFalse(model.state.value.isLoadingPreviousRange)
        assertFalse(model.state.value.isLoadingNextRange)
    }
}
