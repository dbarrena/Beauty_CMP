package com.lasso.lassoapp.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lasso.lassoapp.data.remote.LassoApiException
import com.lasso.lassoapp.data.remote.endpoints.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminState(
    val authorized: Boolean = false,
    val loading: Boolean = true,
    val saving: Boolean = false,
    val error: String? = null,
    val search: String = "",
    val plan: String? = null,
    val active: Boolean? = null,
    val page: Int = 1,
    val total: Int = 0,
    val partners: List<AdminPartner> = emptyList(),
    val selectedPartner: AdminPartner? = null,
    val partnerLoading: Boolean = false,
    val partnerError: String? = null,
    val employees: List<AdminEmployee> = emptyList(),
    val employeesLoading: Boolean = false,
    val employeesError: String? = null,
    val partnerEditor: Boolean = false,
    val employeeEditor: Boolean = false,
    val selectedEmployee: AdminEmployee? = null,
)

class AdminViewModel(private val api: AdminApi) : ViewModel() {
    private val mutableState = MutableStateFlow(AdminState())
    val state = mutableState.asStateFlow()
    private var listJob: Job? = null
    private var employeeJob: Job? = null
    private var partnerJob: Job? = null
    init { refreshAccess() }

    fun refreshAccess() {
        viewModelScope.launch {
            mutableState.value = mutableState.value.copy(loading = true, error = null)
            try {
                check(api.getMasterAccess().isMaster) { "Acceso exclusivo para cuentas master" }
                mutableState.value = mutableState.value.copy(authorized = true)
                loadPartners()
            } catch (error: Exception) { failure(error) }
        }
    }

    private fun failure(error: Exception) {
        if (error is CancellationException) throw error
        val denied = error is LassoApiException && error.statusCode in listOf(401, 403)
        mutableState.value = if (denied) AdminState(loading = false, error = error.message)
        else mutableState.value.copy(loading = false, saving = false, error = error.message ?: "No se pudo completar la operación")
    }

    fun search(value: String) { mutableState.value = mutableState.value.copy(search = value.take(255)) }
    fun filter(plan: String?, active: Boolean?) {
        mutableState.value = mutableState.value.copy(plan = plan, active = active)
        loadPartners(1)
    }
    fun loadPartners(page: Int = state.value.page) {
        if (!state.value.authorized || state.value.saving) return
        listJob?.cancel()
        listJob = viewModelScope.launch {
            val query = state.value
            mutableState.value = query.copy(loading = true, error = null)
            try {
                val result = api.getAdminPartners(page, query.search.trim(), query.plan, query.active)
                if (page > 1 && result.partners.isEmpty()) { loadPartners(page - 1); return@launch }
                mutableState.value = state.value.copy(loading = false, partners = result.partners, page = result.page, total = result.total)
            } catch (error: Exception) { failure(error) }
        }
    }
    fun selectPartner(partner: AdminPartner) {
        if (state.value.saving) return
        mutableState.value = state.value.copy(selectedPartner = partner, employees = emptyList(), error = null)
        loadPartnerDetails()
        loadEmployees()
    }
    fun loadPartnerDetails() {
        val partner = state.value.selectedPartner ?: return
        partnerJob?.cancel()
        partnerJob = viewModelScope.launch {
            mutableState.value = state.value.copy(partnerLoading = true, partnerError = null)
            try {
                val details = api.getAdminPartner(partner.id)
                mutableState.value = state.value.copy(selectedPartner = details, partnerLoading = false)
            } catch (error: Exception) {
                if (error is CancellationException) throw error
                if (error is LassoApiException && error.statusCode in listOf(401, 403)) failure(error)
                else mutableState.value = state.value.copy(partnerLoading = false, partnerError = error.message ?: "No se pudo cargar el partner")
            }
        }
    }
    fun loadEmployees() {
        val partner = state.value.selectedPartner ?: return
        employeeJob?.cancel()
        employeeJob = viewModelScope.launch {
            mutableState.value = state.value.copy(employeesLoading = true, employeesError = null)
            try {
                val employees = api.getAdminEmployees(partner.id)
                mutableState.value = state.value.copy(employees = employees, employeesLoading = false)
            } catch (error: Exception) {
                if (error is CancellationException) throw error
                if (error is LassoApiException && error.statusCode in listOf(401, 403)) failure(error)
                else mutableState.value = state.value.copy(employeesLoading = false, employeesError = error.message ?: "No se pudieron cargar los empleados")
            }
        }
    }
    fun closePartner() {
        if (state.value.saving) return
        employeeJob?.cancel()
        partnerJob?.cancel()
        mutableState.value = state.value.copy(selectedPartner = null, partnerLoading = false, partnerError = null, employees = emptyList(), error = null)
    }
    fun editPartner(create: Boolean = false) {
        if (!create && (state.value.partnerLoading || state.value.partnerError != null)) return
        mutableState.value = state.value.copy(partnerEditor = true, selectedPartner = if (create) null else state.value.selectedPartner, error = null)
    }
    fun editEmployee(employee: AdminEmployee? = null) {
        if (employee?.isMaster == true) return
        mutableState.value = state.value.copy(employeeEditor = true, selectedEmployee = employee, error = null)
    }
    fun closeEditor() {
        if (state.value.saving) return
        mutableState.value = state.value.copy(partnerEditor = false, employeeEditor = false, error = null)
    }
    fun savePartner(input: AdminPartnerInput) {
        if (state.value.saving || !state.value.authorized) return
        partnerJob?.cancel()
        val id = state.value.selectedPartner?.id
        mutableState.value = state.value.copy(saving = true, error = null)
        viewModelScope.launch {
            try {
                val partner = api.saveAdminPartner(id, input)
                mutableState.value = state.value.copy(saving = false, partnerEditor = false, selectedPartner = partner)
                loadPartners()
                loadEmployees()
            } catch (error: Exception) { failure(error) }
        }
    }
    fun saveEmployee(input: AdminEmployeeInput) {
        val partnerId = state.value.selectedPartner?.id ?: return
        if (state.value.saving || !state.value.authorized) return
        val id = state.value.selectedEmployee?.id
        mutableState.value = state.value.copy(saving = true, error = null)
        viewModelScope.launch {
            try {
                api.saveAdminEmployee(partnerId, id, input)
                mutableState.value = state.value.copy(saving = false, employeeEditor = false)
                loadEmployees()
            } catch (error: Exception) { failure(error) }
        }
    }
}
