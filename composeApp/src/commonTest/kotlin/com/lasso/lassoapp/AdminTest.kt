package com.lasso.lassoapp

import com.lasso.lassoapp.data.remote.LassoApiException
import com.lasso.lassoapp.data.remote.endpoints.*
import com.lasso.lassoapp.screens.admin.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class AdminTest {
    private val partner = AdminPartner(7, "Salón", "salon@example.com", isActive = true, accessAllowed = true, salesUsage = SalesUsage(100, 100, 0, false))
    private val input = AdminPartnerInput("Salón", "salon@example.com", null, "premium", null, true)
    private inner class FakeApi : AdminApi {
        var master = true
        var listCalls = 0
        var saveCalls = 0
        var gate: CompletableDeferred<Unit>? = null
        var failSave = false
        var query: List<Any?> = emptyList()
        var detail = partner
        override suspend fun getMasterAccess(): MasterAccess {
            if (!master) throw LassoApiException("Solo master", 403)
            return MasterAccess(true)
        }
        override suspend fun getAdminPartner(id: Int) = detail
        override suspend fun getAdminPartners(page: Int, search: String, plan: String?, active: Boolean?): AdminPartnerPage {
            listCalls++
            query = listOf(page, search, plan, active)
            return AdminPartnerPage(page, 1, listOf(partner))
        }
        override suspend fun saveAdminPartner(id: Int?, input: AdminPartnerInput): AdminPartner {
            saveCalls++
            gate?.await()
            if (failSave) error("Sin conexión")
            return partner.copy(licenseType = input.licenseType, salesUsage = SalesUsage(100, null, null, true))
        }
        override suspend fun getAdminEmployees(partnerId: Int) = emptyList<AdminEmployee>()
        override suspend fun saveAdminEmployee(partnerId: Int, id: Int?, input: AdminEmployeeInput): AdminEmployee = error("Unexpected")
    }
    private fun adminTest(block: suspend TestScope.(FakeApi) -> Unit) = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try { block(FakeApi()) } finally { Dispatchers.resetMain() }
    }
    @Test fun nonMasterNeverLoadsPartners() = adminTest { api ->
        api.master = false
        val model = AdminViewModel(api)
        runCurrent()
        assertFalse(model.state.value.authorized)
        assertEquals(0, api.listCalls)
    }
    @Test fun filtersResetPageAndUseServerSearch() = adminTest { api ->
        val model = AdminViewModel(api)
        runCurrent()
        model.search(" salon ")
        model.filter("premium", false)
        runCurrent()
        assertEquals(listOf(1, "salon", "premium", false), api.query)
    }
    @Test fun selectingPartnerLoadsFreshUsageFromDetailsEndpoint() = adminTest { api ->
        val model = AdminViewModel(api)
        runCurrent()
        api.detail = partner.copy(salesUsage = SalesUsage(123, null, null, true), licenseType = "premium")
        model.selectPartner(partner)
        runCurrent()
        assertEquals(123, model.state.value.selectedPartner?.salesUsage?.used)
        assertEquals("premium", model.state.value.selectedPartner?.licenseType)
        assertFalse(model.state.value.partnerLoading)
    }
    @Test fun duplicateSavesAndDismissAreBlockedUntilResponse() = adminTest { api ->
        val model = AdminViewModel(api)
        runCurrent()
        model.selectPartner(partner)
        runCurrent()
        model.editPartner()
        api.gate = CompletableDeferred()
        model.savePartner(input)
        model.savePartner(input)
        model.closeEditor()
        runCurrent()
        assertEquals(1, api.saveCalls)
        assertTrue(model.state.value.partnerEditor)
        api.gate!!.complete(Unit)
        runCurrent()
        assertFalse(model.state.value.partnerEditor)
        assertEquals(100, model.state.value.selectedPartner?.salesUsage?.used)
        assertNull(model.state.value.selectedPartner?.salesUsage?.limit)
    }
    @Test fun failedSaveKeepsEditorOpenForRetry() = adminTest { api ->
        val model = AdminViewModel(api)
        runCurrent()
        model.editPartner(create = true)
        api.failSave = true
        model.savePartner(input)
        runCurrent()
        assertTrue(model.state.value.partnerEditor)
        assertFalse(model.state.value.saving)
        assertEquals("Sin conexión", model.state.value.error)
    }
    @Test fun clearingExpiryAndPhoneSendsExplicitNullButBlankPasswordIsOmitted() {
        val json = Json.encodeToString(input)
        assertTrue(json.contains("\"license_expiry\":null"))
        assertTrue(json.contains("\"phone\":null"))
        assertFalse(Json.encodeToString(AdminEmployeeInput("Ana", "ana@example.com", "empleado", false, false, true)).contains("password"))
    }
    @Test fun membershipsMatchExistingTrialAndPremiumValues() {
        assertEquals(listOf("trial", "premium"), adminMembershipTypes)
        assertEquals("Premium", planName("premium"))
        assertEquals("Prueba", planName("trial"))
        assertEquals("Prueba", planName(null))
    }
    @Test fun datesUseMexicoMidnightAndRejectInvalidDates() {
        val epoch = expiryFromDate("2027-01-15")!!
        assertEquals("2027-01-15", adminDate(epoch))
        assertNull(expiryFromDate(""))
        assertFails { expiryFromDate("2027-02-30") }
    }
}
