package com.lasso.lassoapp.screens.employees

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lasso.lassoapp.data.remote.endpoints.EmployeesApi
import com.lasso.lassoapp.model.Employee
import com.lasso.lassoapp.model.EmployeeRegistrationRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException

data class EmployeesState(
    val employees: List<Employee> = emptyList(),
    val isLoading: Boolean = false,
    val isRegistering: Boolean = false,
    val isUpdating: Boolean = false,
    val isDisabling: Boolean = false,
    val isDisableConfirmationDisplayed: Boolean = false,
    val disableError: String? = null,
    val isNewEmployeeDialogDisplayed: Boolean = false,
    val selectedEmployee: Employee? = null,
    val isEditEmployeeDialogDisplayed: Boolean = false
)

class EmployeesViewModel(private val lassoApi: EmployeesApi) : ViewModel() {
    private val _state = MutableStateFlow(EmployeesState())
    val state: StateFlow<EmployeesState> = _state.asStateFlow()

    init {
        loadEmployees()
    }

    fun loadEmployees() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val employees = lassoApi.getEmployees()
                _state.value = _state.value.copy(employees = employees, isLoading = false)
            } catch (e: Exception) {
                e.printStackTrace()
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun showNewEmployeeDialog() {
        _state.value = _state.value.copy(isNewEmployeeDialogDisplayed = true)
    }

    fun hideNewEmployeeDialog() {
        _state.value = _state.value.copy(isNewEmployeeDialogDisplayed = false)
    }

    fun showEditEmployeeDialog(employee: Employee) {
        _state.value = _state.value.copy(selectedEmployee = employee, isEditEmployeeDialogDisplayed = true)
    }

    fun hideEditEmployeeDialog() {
        if (_state.value.isDisabling) return
        _state.value = _state.value.copy(selectedEmployee = null, isEditEmployeeDialogDisplayed = false)
    }

    fun showDisableConfirmation() {
        if (_state.value.selectedEmployee == null || _state.value.isUpdating || _state.value.isDisabling) return
        _state.value = _state.value.copy(isDisableConfirmationDisplayed = true, disableError = null)
    }

    fun hideDisableConfirmation() {
        if (_state.value.isDisabling) return
        _state.value = _state.value.copy(isDisableConfirmationDisplayed = false, disableError = null)
    }

    fun disableEmployee() {
        val current = _state.value
        val employee = current.selectedEmployee ?: return
        if (!current.isDisableConfirmationDisplayed || current.isDisabling || current.isUpdating) return
        _state.value = current.copy(isDisabling = true, disableError = null)
        viewModelScope.launch {
            try {
                lassoApi.disableEmployee(employee.id)
                _state.value = _state.value.copy(
                    employees = _state.value.employees.filterNot { it.id == employee.id },
                    selectedEmployee = null,
                    isEditEmployeeDialogDisplayed = false,
                    isDisableConfirmationDisplayed = false
                )
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _state.value = _state.value.copy(
                    disableError = e.message ?: "No se pudo desactivar al empleado. Intenta de nuevo."
                )
            } finally {
                _state.value = _state.value.copy(isDisabling = false)
            }
        }
    }

    fun onEmployeeSaved() {
        loadEmployees()
        hideNewEmployeeDialog()
        hideEditEmployeeDialog()
    }

    fun editEmployee(employee: Employee) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isUpdating = true)
            try {
                lassoApi.editEmployee(employee)
                onEmployeeSaved()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _state.value = _state.value.copy(isUpdating = false)
            }
        }
    }

    fun registerEmployee(request: EmployeeRegistrationRequest) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isRegistering = true)
            try {
                lassoApi.registerEmployee(request)
                onEmployeeSaved()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _state.value = _state.value.copy(isRegistering = false)
            }
        }
    }
}
