package com.lasso.lassoapp.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.model.RegistrationRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterScreenState(
    val companyName: String = "",
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val fieldErrors: Map<RegistrationField, String> = emptyMap(),
    val validationAttempt: Int = 0,
    val scrollToField: RegistrationField? = null,
    val isRegistered: Boolean = false,
)

enum class RegistrationField { CompanyName, Name, Email, Password, ConfirmPassword }

class RegisterScreenViewModel(private val lassoApi: LassoApi) : ViewModel() {
    private val _state = MutableStateFlow(RegisterScreenState())
    val state = _state.asStateFlow()

    fun updateField(field: RegistrationField, value: String) {
        if (_state.value.isLoading || _state.value.isRegistered) return
        val maxLength = when (field) {
            RegistrationField.Password, RegistrationField.ConfirmPassword -> 128
            else -> 255
        }
        if (value.length > maxLength) return
        _state.update { current ->
            val updated = when (field) {
                RegistrationField.CompanyName -> current.copy(companyName = value, error = null)
                RegistrationField.Name -> current.copy(name = value, error = null)
                RegistrationField.Email -> current.copy(email = value, error = null)
                RegistrationField.Password -> current.copy(password = value, error = null)
                RegistrationField.ConfirmPassword -> current.copy(confirmPassword = value, error = null)
            }
            updated.copy(fieldErrors = current.fieldErrors - field)
        }
    }

    fun register() {
        val form = _state.value
        if (form.isLoading || form.isRegistered) return
        val fieldErrors = buildMap {
            if (form.companyName.isBlank()) put(RegistrationField.CompanyName, "Ingresa el nombre de la empresa.")
            if (form.name.isBlank()) put(RegistrationField.Name, "Ingresa tu nombre.")
            if (form.email.isBlank()) {
                put(RegistrationField.Email, "Ingresa tu correo electrónico.")
            } else if (!Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$").matches(form.email.trim())) {
                put(RegistrationField.Email, "Ingresa un correo electrónico válido.")
            }
            if (form.password.isBlank()) {
                put(RegistrationField.Password, "Ingresa una contraseña.")
            } else if (form.password.length < 8) {
                put(RegistrationField.Password, "La contraseña debe tener al menos 8 caracteres.")
            }
            if (form.confirmPassword.isBlank()) {
                put(RegistrationField.ConfirmPassword, "Confirma tu contraseña.")
            } else if (form.password != form.confirmPassword) {
                put(RegistrationField.ConfirmPassword, "Las contraseñas no coinciden.")
            }
        }
        if (fieldErrors.isNotEmpty()) {
            _state.update { it.copy(
                fieldErrors = fieldErrors, error = null, validationAttempt = it.validationAttempt + 1,
                scrollToField = RegistrationField.entries.first { field -> field in fieldErrors },
            ) }
            return
        }
        _state.update { it.copy(isLoading = true, error = null, fieldErrors = emptyMap()) }
        viewModelScope.launch {
            try {
                lassoApi.register(RegistrationRequest(
                    companyName = form.companyName.trim(), name = form.name.trim(),
                    role = "administrador", email = form.email.trim(), password = form.password,
                ))
                _state.value = RegisterScreenState(isRegistered = true)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _state.update { it.copy(
                    isLoading = false,
                    error = exception.message ?: "No se pudo crear la cuenta. Inténtalo de nuevo.",
                ) }
            }
        }
    }
}
