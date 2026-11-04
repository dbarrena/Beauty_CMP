package com.lasso.lassoapp

import com.lasso.lassoapp.data.remote.endpoints.EmployeesApi
import com.lasso.lassoapp.data.remote.endpoints.ProfileUpdateRequest
import com.lasso.lassoapp.model.Employee
import com.lasso.lassoapp.model.EmployeeRegistrationRequest
import com.lasso.lassoapp.screens.employees.EmployeesViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class EmployeeDisableTest {
    private val employee = Employee(7, "Ana", "ana@example.com", role = "empleado", partnerId = 2, updatedAt = null)

    private inner class FakeApi : EmployeesApi {
        var calls = mutableListOf<Int>()
        var fails = false
        var gate: CompletableDeferred<Unit>? = null
        override suspend fun getEmployees() = listOf(employee)
        override suspend fun disableEmployee(id: Int): String {
            calls.add(id)
            gate?.await()
            if (fails) error("Sin conexión")
            return "Empleado desactivado"
        }
        override suspend fun getProfile(id: Int) = employee
        override suspend fun updateProfile(id: Int, request: ProfileUpdateRequest) = employee
        override suspend fun registerEmployee(employee: EmployeeRegistrationRequest) = error("Unexpected registration")
        override suspend fun editEmployee(employee: Employee) = error("Unexpected edit")
    }

    private fun employeeTest(block: suspend TestScope.(FakeApi, EmployeesViewModel) -> Unit) = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val api = FakeApi()
            val model = EmployeesViewModel(api)
            runCurrent()
            model.showEditEmployeeDialog(employee)
            block(api, model)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun cancellingConfirmationDoesNotDisableEmployee() = employeeTest { api, model ->
        model.disableEmployee()
        model.showDisableConfirmation()
        model.hideDisableConfirmation()
        runCurrent()
        assertTrue(api.calls.isEmpty())
        assertTrue(model.state.value.isEditEmployeeDialogDisplayed)
        assertEquals(listOf(employee), model.state.value.employees)
    }

    @Test
    fun confirmationDisablesOnceAndRemovesEmployeeAfterSuccess() = employeeTest { api, model ->
        api.gate = CompletableDeferred()
        model.showDisableConfirmation()
        model.disableEmployee()
        model.disableEmployee()
        model.hideDisableConfirmation()
        model.hideEditEmployeeDialog()
        runCurrent()
        assertEquals(listOf(employee.id), api.calls)
        assertTrue(model.state.value.isDisabling)
        assertTrue(model.state.value.isDisableConfirmationDisplayed)
        assertEquals(listOf(employee), model.state.value.employees)
        api.gate!!.complete(Unit)
        runCurrent()
        assertFalse(model.state.value.isDisabling)
        assertFalse(model.state.value.isDisableConfirmationDisplayed)
        assertFalse(model.state.value.isEditEmployeeDialogDisplayed)
        assertNull(model.state.value.selectedEmployee)
        assertTrue(model.state.value.employees.isEmpty())
    }

    @Test
    fun failedDisableKeepsEmployeeAndAllowsRetry() = employeeTest { api, model ->
        api.fails = true
        model.showDisableConfirmation()
        model.disableEmployee()
        runCurrent()
        assertEquals("Sin conexión", model.state.value.disableError)
        assertTrue(model.state.value.isDisableConfirmationDisplayed)
        assertFalse(model.state.value.isDisabling)
        assertEquals(listOf(employee), model.state.value.employees)
        api.fails = false
        model.disableEmployee()
        runCurrent()
        assertEquals(2, api.calls.size)
        assertNull(model.state.value.disableError)
        assertTrue(model.state.value.employees.isEmpty())
    }
}
