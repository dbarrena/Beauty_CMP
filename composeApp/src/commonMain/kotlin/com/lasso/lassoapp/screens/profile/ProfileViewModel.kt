package com.lasso.lassoapp.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lasso.lassoapp.data.local.session.SessionRepository
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.data.remote.endpoints.ProfileUpdateRequest
import com.lasso.lassoapp.model.Employee
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ProfileDialog { Edit, Password }
enum class ProfileField { Name, Email, Password, ConfirmPassword }

data class ProfileState(
    val employee: Employee? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isLoggingOut: Boolean = false,
    val loggedOut: Boolean = false,
    val dialog: ProfileDialog? = null,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val fieldErrors: Map<ProfileField, String> = emptyMap(),
    val error: String? = null,
    val success: String? = null,
)

internal interface ProfileRepository {
    suspend fun load(): Employee
    suspend fun update(id: Int, request: ProfileUpdateRequest): Employee
    suspend fun rememberProfile(employee: Employee)
    suspend fun logout()
}

class ProfileViewModel internal constructor(private val repository: ProfileRepository) : ViewModel() {
    constructor(api: LassoApi, session: SessionRepository) : this(object : ProfileRepository {
        override suspend fun load(): Employee {
            val id = session.getEmployeeId() ?: error("No se encontró una sesión activa.")
            return api.getProfile(id)
        }

        override suspend fun update(id: Int, request: ProfileUpdateRequest) = api.updateProfile(id, request)
        override suspend fun rememberProfile(employee: Employee) = session.updateProfile(employee)
        override suspend fun logout() {
            try { api.logout() } catch (exception: CancellationException) { throw exception }
            catch (_: Exception) { /* Match the existing local sign-out behavior when offline. */ }
            session.removeSession()
        }
    })
    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    init { loadProfile() }

    fun loadProfile() {
        if (_state.value.isSaving || _state.value.isLoggingOut) return
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val employee = repository.load()
                repository.rememberProfile(employee)
                _state.update { it.copy(employee = employee, isLoading = false) }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _state.update { it.copy(isLoading = false, error = exception.message ?: "No se pudo cargar el perfil.") }
            }
        }
    }

    fun openDialog(dialog: ProfileDialog) {
        val current = _state.value
        val employee = current.employee ?: return
        if (current.isSaving || current.isLoggingOut) return
        _state.update { it.copy(dialog = dialog, name = employee.name, email = employee.email,
            password = "", confirmPassword = "", fieldErrors = emptyMap(), error = null, success = null) }
    }

    fun dismissDialog() {
        if (_state.value.isSaving) return
        _state.update { it.copy(dialog = null, password = "", confirmPassword = "", fieldErrors = emptyMap(), error = null) }
    }

    fun updateField(field: ProfileField, value: String) {
        if (_state.value.isSaving) return
        val limit = if (field == ProfileField.Password || field == ProfileField.ConfirmPassword) 128 else 255
        if (value.length > limit) return
        _state.update { current ->
            val updated = when (field) {
                ProfileField.Name -> current.copy(name = value)
                ProfileField.Email -> current.copy(email = value)
                ProfileField.Password -> current.copy(password = value)
                ProfileField.ConfirmPassword -> current.copy(confirmPassword = value)
            }
            updated.copy(error = null, fieldErrors = current.fieldErrors - field)
        }
    }

    fun save() {
        val form = _state.value
        val employee = form.employee ?: return
        val dialog = form.dialog ?: return
        if (form.isSaving || form.isLoggingOut) return
        val errors = buildMap {
            if (dialog == ProfileDialog.Edit) {
                if (form.name.isBlank()) put(ProfileField.Name, "Ingresa tu nombre.")
                if (!Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$").matches(form.email.trim())) {
                    put(ProfileField.Email, "Ingresa un correo electrónico válido.")
                }
            } else {
                if (form.password.isBlank() || form.password.length < 8) {
                    put(ProfileField.Password, "La contraseña debe tener al menos 8 caracteres.")
                }
                if (form.confirmPassword.isBlank()) put(ProfileField.ConfirmPassword, "Confirma tu contraseña.")
                else if (form.password != form.confirmPassword) put(ProfileField.ConfirmPassword, "Las contraseñas no coinciden.")
            }
        }
        if (errors.isNotEmpty()) {
            _state.update { it.copy(fieldErrors = errors, error = null) }
            return
        }
        val request = if (dialog == ProfileDialog.Edit) {
            ProfileUpdateRequest(name = form.name.trim(), email = form.email.trim())
        } else ProfileUpdateRequest(password = form.password)
        _state.update { it.copy(isSaving = true, error = null, fieldErrors = emptyMap()) }
        viewModelScope.launch {
            try {
                val updated = repository.update(employee.id, request).copy(password = null)
                repository.rememberProfile(updated)
                _state.update { it.copy(employee = updated, dialog = null, isSaving = false,
                    password = "", confirmPassword = "", success = if (dialog == ProfileDialog.Edit)
                        "Perfil actualizado correctamente." else "Contraseña actualizada correctamente.") }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _state.update { it.copy(isSaving = false, error = exception.message ?: "No se pudieron guardar los cambios.") }
            }
        }
    }

    fun logout() {
        if (_state.value.isSaving || _state.value.isLoggingOut) return
        _state.update { it.copy(isLoggingOut = true, error = null, success = null) }
        viewModelScope.launch {
            try {
                repository.logout()
                _state.value = ProfileState(isLoading = false, loggedOut = true)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _state.update { it.copy(isLoggingOut = false, error = "No se pudo cerrar la sesión. Inténtalo de nuevo.") }
            }
        }
    }
}
