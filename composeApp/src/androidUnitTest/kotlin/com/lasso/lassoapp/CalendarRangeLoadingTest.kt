package com.lasso.lassoapp

import androidx.lifecycle.ViewModelStore
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.model.AppointmentCalendarResponse
import com.lasso.lassoapp.model.CalendarAppointment
import com.lasso.lassoapp.model.EmployeeAppointmentSchedule
import com.lasso.lassoapp.screens.calendar.CalendarScreenViewModel
import com.lasso.lassoapp.screens.calendar.CalendarViewMode
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

        fun succeed() {
            response.complete(AppointmentCalendarResponse(start, end, listOf(
                EmployeeAppointmentSchedule(1, "Ana", listOf(
                    CalendarAppointment(10, startsAt = start + 10 * 60 * 60 * 1000L, endsAt = start + 11 * 60 * 60 * 1000L),
                )),
            )))
        }
    }

    /** JVM proxy keeps unrelated Lasso endpoints out of this calendar-only fake. */
    private class FakeApi {
        val requests = mutableListOf<Request>()
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
                "getClients", "getEmployees", "getServices" -> emptyList<Any>()
                else -> error("Unexpected calendar API call: ${method.name}")
            }
        } as LassoApi
    }

    private fun calendarTest(block: suspend TestScope.(FakeApi, CalendarScreenViewModel) -> Unit) = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val store = ViewModelStore()
        try {
            val api = FakeApi()
            val model = CalendarScreenViewModel(api.api)
            store.put("calendar", model)
            model.onDateSelected(selectedDate)
            model.onViewModeSelected(CalendarViewMode.ThreeDays)
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
    fun nextRangeLoadsExactlyOnceAndKeepsCurrentGridUntilSuccess() = calendarTest { api, model ->
        val originalEvents = model.state.value.events
        model.loadNextThreeDays()
        model.loadNextThreeDays()
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
        model.loadNextThreeDays()
        runCurrent()
        api.requests.last().response.completeExceptionally(IllegalStateException("Sin conexión"))
        runCurrent()
        assertEquals(selectedDate, model.state.value.selectedDate)
        assertEquals(originalEvents, model.state.value.events)
        assertEquals("Sin conexión", model.state.value.error)
        assertFalse(model.state.value.isLoadingNextRange)
        model.loadNextThreeDays()
        runCurrent()
        assertEquals(3, api.requests.size)
        api.requests.last().succeed()
        runCurrent()
        assertEquals(LocalDate(2026, 11, 9), model.state.value.selectedDate)
    }

    @Test
    fun switchingToDayCancelsPendingRangeAdvance() = calendarTest { api, model ->
        model.loadNextThreeDays()
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
        model.loadNextThreeDays()
        runCurrent()
        assertEquals(3, api.requests.size)
    }

    @Test
    fun previousRangeLoadsOnceAndKeepsCurrentGridUntilSuccess() = calendarTest { api, model ->
        val originalEvents = model.state.value.events
        model.loadPreviousThreeDays()
        model.loadPreviousThreeDays()
        model.loadNextThreeDays()
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
        assertFalse(model.state.value.isLoadingPreviousRange)
        assertFalse(model.state.value.isLoading)
    }

    @Test
    fun failedPreviousRangeKeepsCurrentAppointmentsAndCanBeRetried() = calendarTest { api, model ->
        val originalEvents = model.state.value.events
        model.loadPreviousThreeDays()
        runCurrent()
        api.requests.last().response.completeExceptionally(IllegalStateException("Sin conexión"))
        runCurrent()
        assertEquals(selectedDate, model.state.value.selectedDate)
        assertEquals(originalEvents, model.state.value.events)
        assertFalse(model.state.value.isLoadingPreviousRange)
        model.loadPreviousThreeDays()
        runCurrent()
        api.requests.last().succeed()
        runCurrent()
        assertEquals(LocalDate(2026, 11, 3), model.state.value.selectedDate)
    }

    @Test
    fun switchingToDayCancelsPendingBackwardRequest() = calendarTest { api, model ->
        model.loadPreviousThreeDays()
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
        model.loadPreviousThreeDays()
        runCurrent()
        assertEquals(3, api.requests.size)
    }

    @Test
    fun navigatingForwardAndBackReturnsToOriginalRange() = calendarTest { api, model ->
        model.loadNextThreeDays()
        runCurrent()
        api.requests.last().succeed()
        runCurrent()
        model.loadPreviousThreeDays()
        runCurrent()
        api.requests.last().succeed()
        runCurrent()
        assertEquals(selectedDate, model.state.value.selectedDate)
        assertFalse(model.state.value.isLoadingPreviousRange)
        assertFalse(model.state.value.isLoadingNextRange)
    }
}
