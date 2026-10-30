package com.lasso.lassoapp

import com.lasso.lassoapp.data.local.dao.SessionDao
import com.lasso.lassoapp.data.local.session.SessionRepository
import com.lasso.lassoapp.data.remote.endpoints.ProfileUpdateRequest
import com.lasso.lassoapp.model.Employee
import com.lasso.lassoapp.model.room.Session
import com.lasso.lassoapp.screens.profile.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileTest {
    private val employee = Employee(7, "Ana Pérez", "ana@example.com", role = "empleado", partnerId = 2, updatedAt = null)

    private inner class FakeRepository : ProfileRepository {
        var loadFails = false
        var saveFails = false
        var requests = mutableListOf<ProfileUpdateRequest>()
        var gate: CompletableDeferred<Unit>? = null
        var remembered: Employee? = null
        override suspend fun load(): Employee {
            if (loadFails) error("Sin conexión")
            return employee
        }
        override suspend fun update(id: Int, request: ProfileUpdateRequest): Employee {
            assertEquals(employee.id, id)
            requests.add(request)
            gate?.await()
            if (saveFails) error("Sin conexión")
            return employee.copy(name = request.name ?: employee.name, email = request.email ?: employee.email)
        }
        override suspend fun rememberProfile(employee: Employee) { remembered = employee }
        override suspend fun logout() {}
    }

    private fun profileTest(block: suspend TestScope.(FakeRepository, ProfileViewModel) -> Unit) = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val repository = FakeRepository()
            val model = ProfileViewModel(repository)
            runCurrent()
            block(repository, model)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun profileEditsTrimFieldsAndPreserveRoleWithoutSendingPassword() = profileTest { repository, model ->
        model.openDialog(ProfileDialog.Edit)
        model.updateField(ProfileField.Name, "  Ana María  ")
        model.updateField(ProfileField.Email, "  maria@example.com  ")
        repository.gate = CompletableDeferred()
        model.save()
        model.save()
        model.dismissDialog()
        runCurrent()
        assertEquals(1, repository.requests.size)
        assertEquals(ProfileUpdateRequest(name = "Ana María", email = "maria@example.com"), repository.requests.single())
        assertEquals(ProfileDialog.Edit, model.state.value.dialog)
        repository.gate!!.complete(Unit)
        runCurrent()
        assertNull(model.state.value.dialog)
        assertEquals("Ana María", repository.remembered?.name)
        assertEquals(employee.role, model.state.value.employee?.role)
    }

    @Test
    fun invalidProfileAndMismatchedPasswordsDoNotReachTheApi() = profileTest { repository, model ->
        model.openDialog(ProfileDialog.Edit)
        model.updateField(ProfileField.Name, " ")
        model.updateField(ProfileField.Email, "invalid")
        model.save()
        assertEquals(setOf(ProfileField.Name, ProfileField.Email), model.state.value.fieldErrors.keys)
        model.dismissDialog()
        model.openDialog(ProfileDialog.Password)
        model.updateField(ProfileField.Password, "short")
        model.updateField(ProfileField.ConfirmPassword, "different")
        model.save()
        assertEquals(setOf(ProfileField.Password, ProfileField.ConfirmPassword), model.state.value.fieldErrors.keys)
        runCurrent()
        assertTrue(repository.requests.isEmpty())
    }

    @Test
    fun passwordFailureCanBeRetriedAndSuccessClearsSecrets() = profileTest { repository, model ->
        model.openDialog(ProfileDialog.Password)
        model.updateField(ProfileField.Password, "newPassword123")
        model.updateField(ProfileField.ConfirmPassword, "newPassword123")
        repository.saveFails = true
        model.save()
        runCurrent()
        assertEquals(ProfileDialog.Password, model.state.value.dialog)
        assertEquals("newPassword123", model.state.value.password)
        assertNotNull(model.state.value.error)
        repository.saveFails = false
        model.save()
        runCurrent()
        assertEquals(ProfileUpdateRequest(password = "newPassword123"), repository.requests.last())
        assertNull(model.state.value.dialog)
        assertEquals("", model.state.value.password)
        assertEquals("", model.state.value.confirmPassword)
        assertNull(repository.remembered?.password)
        assertNotNull(model.state.value.success)
    }

    @Test
    fun requestJsonOmitsUntouchedFieldsAndHasNoRoleOrCurrentPassword() {
        val edit = Json.parseToJsonElement(Json.encodeToString(ProfileUpdateRequest(name = "Ana", email = employee.email))).jsonObject
        assertEquals(setOf("name", "email"), edit.keys)
        val password = Json.parseToJsonElement(Json.encodeToString(ProfileUpdateRequest(password = "newPassword123"))).jsonObject
        assertEquals(setOf("password"), password.keys)
    }

    @Test
    fun closingThePasswordDialogClearsItsDraft() = profileTest { _, model ->
        model.openDialog(ProfileDialog.Password)
        model.updateField(ProfileField.Password, "newPassword123")
        model.dismissDialog()
        model.openDialog(ProfileDialog.Password)
        assertEquals("", model.state.value.password)
        assertEquals("", model.state.value.confirmPassword)
    }

    @Test
    fun loadingFailureIsVisibleAndCanBeRetried() = profileTest { repository, model ->
        repository.loadFails = true
        model.loadProfile()
        runCurrent()
        assertFalse(model.state.value.isLoading)
        assertNotNull(model.state.value.error)
        repository.loadFails = false
        model.loadProfile()
        runCurrent()
        assertNull(model.state.value.error)
        assertEquals(employee, model.state.value.employee)
    }

    @Test
    fun updatingCachedProfilePreservesTheSessionTokenAndBusiness() = runTest {
        val original = Session(id = 1, partnerId = 2, employeeId = 7, partnerName = "Estética", employeeName = "Ana",
            employeeRole = "empleado", token = "existing-session-token")
        var saved = original
        val dao = object : SessionDao {
            override suspend fun insert(session: Session): Long { saved = session; return session.id.toLong() }
            override suspend fun getSession() = saved
            override suspend fun deleteAll(): Int = error("Updating a profile must preserve the session")
        }
        val repository = SessionRepository(dao)
        repository.updateProfile(employee.copy(name = "Ana María"))
        assertEquals(original.copy(employeeName = "Ana María"), saved)
        repository.updateProfile(employee.copy(id = 99, name = "Another employee"))
        assertEquals("Ana María", saved.employeeName)
    }
}
