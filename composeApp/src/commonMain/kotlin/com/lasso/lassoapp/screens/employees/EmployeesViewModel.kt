package com.lasso.lassoapp.screens.employees

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.model.Employee
import com.lasso.lassoapp.model.EmployeeRegistrationRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EmployeesState(
    val employees: List<Employee> = emptyList(),
    val isLoading: Boolean = false,
    val isRegistering: Boolean = false,
    val isUpdating: Boolean = false,
    val isNewEmployeeDialogDisplayed: Boolean = false,
    val selectedEmployee: Employee? = null,
    val isEditEmployeeDialogDisplayed: Boolean = false
)

class EmployeesViewModel(private val lassoApi: LassoApi) : ViewModel() {
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
        _state.value = _state.value.copy(selectedEmployee = null, isEditEmployeeDialogDisplayed = false)
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
