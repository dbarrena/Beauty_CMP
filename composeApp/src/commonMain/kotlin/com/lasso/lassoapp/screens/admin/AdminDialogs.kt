package com.lasso.lassoapp.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.data.remote.endpoints.*
import com.lasso.lassoapp.screens.calendar.dialog.AppointmentDialogActions
import com.lasso.lassoapp.screens.calendar.dialog.AppointmentDateField
import com.lasso.lassoapp.screens.calendar.CalendarPickerDialog
import com.lasso.lassoapp.screens.calendar.dialog.AppointmentFieldLabel
import com.lasso.lassoapp.screens.calendar.dialog.AppointmentPickerField
import com.lasso.lassoapp.screens.utils.DialogContentScaffold
import com.lasso.lassoapp.screens.utils.LassoDialogFrame
import com.lasso.lassoapp.ui.theme.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

internal fun expiryFromDate(value: String): Long? = value.trim().takeIf { it.isNotEmpty() }?.let {
    LocalDate.parse(it).atStartOfDayIn(adminTimeZone).toEpochMilliseconds().also { epoch -> require(epoch >= 0) }
}

private fun validEmail(value: String) = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$").matches(value.trim())

@Composable
internal fun PartnerEditor(state: AdminState, onClose: () -> Unit, onSave: (AdminPartnerInput) -> Unit) {
    val partner = state.selectedPartner
    var name by remember(partner?.id) { mutableStateOf(partner?.name ?: "") }
    var email by remember(partner?.id) { mutableStateOf(partner?.email ?: "") }
    var phone by remember(partner?.id) { mutableStateOf(partner?.phone ?: "") }
    var plan by remember(partner?.id) { mutableStateOf(partner?.licenseType ?: "trial") }
    var expiry by remember(partner?.id) { mutableStateOf(partner?.licenseExpiry?.let { adminDate(it) } ?: "") }
    var showExpiryPicker by remember(partner?.id) { mutableStateOf(false) }
    val expiryDate = expiry.takeIf { it.isNotEmpty() }?.let { LocalDate.parse(it) }
    var active by remember(partner?.id) { mutableStateOf(partner?.isActive ?: true) }
    var confirmation by remember { mutableStateOf(false) }
    val parsedExpiry = runCatching {
        if (partner?.licenseExpiry != null && expiry == adminDate(partner.licenseExpiry)) partner.licenseExpiry
        else expiryFromDate(expiry)
    }
    val valid = name.isNotBlank() && name.trim().length <= 255 && validEmail(email) && email.trim().length <= 255 && phone.length <= 50 && parsedExpiry.isSuccess
    fun save() = onSave(AdminPartnerInput(name.trim(), email.trim(), phone.trim().ifEmpty { null }, plan, parsedExpiry.getOrNull(), active))
    AdminForm(if (partner == null) "Nuevo partner" else "Editar partner", state, onClose, valid, {
        if (partner != null) confirmation = true else save()
    }) {
        AdminField("Nombre del negocio", name, !state.saving) { name = it.take(255) }
        AdminField("Correo electrónico", email, !state.saving) { email = it.take(255) }
        AdminField("Teléfono (opcional)", phone, !state.saving) { phone = it.take(50) }
        Column {
            AppointmentFieldLabel("Membresía")
            AppointmentPickerField(planName(plan), "Seleccionar membresía", adminMembershipTypes,
                ::planName, !state.saving, { plan = it })
            Spacer(Modifier.height(8.dp))
            AdminHelper("Prueba: 100 ventas de por vida. Premium: sin límite.")
        }
        Column {
            AppointmentDateField(expiryDate, !state.saving, { showExpiryPicker = true },
                label = "Vencimiento (opcional)", onClear = { expiry = "" })
            if (parsedExpiry.isFailure) Text("Ingresa una fecha válida", color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
            AdminHelper("Sin fecha, la membresía no tiene vencimiento. El acceso vence al inicio de la fecha seleccionada, hora de Ciudad de México.")
        }
        AdminToggle("Cuenta activa", active, !state.saving) { active = it }
    }
    if (showExpiryPicker) CalendarPickerDialog(
        initialDate = expiryDate ?: Clock.System.now().toLocalDateTime(adminTimeZone).date,
        onDismiss = { showExpiryPicker = false },
        onConfirm = { expiry = it.toString(); showExpiryPicker = false },
    )
    if (confirmation) AlertDialog(onDismissRequest = { confirmation = false }, title = { Text("Guardar cambios del partner") },
        text = { Text("Los cambios de membresía se aplican de inmediato. Suspender la cuenta o establecer una fecha vencida cerrará las sesiones del partner.") },
        confirmButton = { TextButton(onClick = { confirmation = false; save() }) { Text("Guardar cambios") } },
        dismissButton = { TextButton(onClick = { confirmation = false }) { Text("Cancelar") } })
}

@Composable
internal fun EmployeeEditor(state: AdminState, onClose: () -> Unit, onSave: (AdminEmployeeInput) -> Unit) {
    val employee = state.selectedEmployee
    var name by remember(employee?.id) { mutableStateOf(employee?.name ?: "") }
    var email by remember(employee?.id) { mutableStateOf(employee?.email ?: "") }
    var password by remember(employee?.id) { mutableStateOf("") }
    var role by remember(employee?.id) { mutableStateOf(if (employee?.role == "administrador") "administrador" else "empleado") }
    var active by remember(employee?.id) { mutableStateOf(employee?.disabled != true) }
    var sales by remember(employee?.id) { mutableStateOf(employee?.canSeeSales ?: false) }
    var calendar by remember(employee?.id) { mutableStateOf(employee?.isDisplayedOnCalendar ?: true) }
    var confirmation by remember { mutableStateOf(false) }
    val valid = name.isNotBlank() && validEmail(email) && (password.length in 8..128 || (employee != null && password.isEmpty()))
    fun save() = onSave(AdminEmployeeInput(name.trim(), email.trim(), role, !active, sales, calendar, password.ifEmpty { null }))
    AdminForm(if (employee == null) "Nuevo empleado" else "Empleado y permisos", state, onClose, valid, {
        if (employee != null) confirmation = true else save()
    }) {
        AdminField("Nombre", name, !state.saving) { name = it.take(255) }
        AdminField("Correo electrónico", email, !state.saving) { email = it.take(255) }
        AdminField(if (employee == null) "Contraseña (8 a 128 caracteres)" else "Nueva contraseña (opcional)",
            password, !state.saving, visualTransformation = PasswordVisualTransformation()) { password = it.take(128) }
        Column {
            AppointmentFieldLabel("Rol")
            AppointmentPickerField(if (role == "empleado") "Empleado" else "Administrador", "Seleccionar rol",
                listOf("empleado", "administrador"), { if (it == "empleado") "Empleado" else "Administrador" },
                !state.saving, { role = it })
            Spacer(Modifier.height(8.dp))
            AdminHelper("Un administrador gestiona el negocio y ve sus ventas. Este rol no otorga acceso master.")
        }
        AdminToggle("Empleado activo", active, !state.saving) { active = it }
        AdminToggle("Puede ver ventas", sales || role == "administrador", !state.saving && role != "administrador") { sales = it }
        AdminToggle("Visible en el calendario", calendar, !state.saving) { calendar = it }
    }
    if (confirmation) AlertDialog(onDismissRequest = { confirmation = false }, title = { Text("Guardar cambios del empleado") },
        text = { Text("Se cerrarán sus sesiones para aplicar los permisos y credenciales. Si permanece activo, podrá volver a iniciar sesión.") },
        confirmButton = { TextButton(onClick = { confirmation = false; save() }) { Text("Guardar cambios") } },
        dismissButton = { TextButton(onClick = { confirmation = false }) { Text("Cancelar") } })
}

@Composable
private fun AdminForm(title: String, state: AdminState, onClose: () -> Unit, valid: Boolean, save: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    val dismiss = { if (!state.saving) onClose() }
    val scroll = rememberScrollState()
    LaunchedEffect(state.error) {
        if (state.error != null) scroll.animateScrollTo(scroll.maxValue)
    }
    LassoDialogFrame(onDismiss = dismiss, shape = RoundedCornerShape(16.dp), color = Color.White, expanded = true) {
        DialogContentScaffold(onClose = dismiss, closeEnabled = !state.saving, scroll = scroll,
            modifier = Modifier.fillMaxSize(), header = {
                Text(title, Modifier.fillMaxWidth(), style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold, fontSize = 24.sp, color = LassoTextPrimary), textAlign = TextAlign.Center)
            }, actions = {
            Column {
                AppointmentDialogActions(isEditing = false, isValid = valid, isSaving = state.saving,
                    isDeleting = false, onSave = save, onDelete = {})
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = dismiss, enabled = !state.saving,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                    Text("Cancelar", color = LassoTextMuted, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }
        }) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                content()
                state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        }
    }
}

@Composable
private fun AdminField(label: String, value: String, enabled: Boolean, placeholder: String = label,
    visualTransformation: VisualTransformation = VisualTransformation.None, change: (String) -> Unit) {
    Column {
        AppointmentFieldLabel(label)
        TextField(value, change, Modifier.fillMaxWidth().heightIn(min = 56.dp), enabled = enabled,
            placeholder = { Text(placeholder, color = LassoTextMuted) }, singleLine = true,
            visualTransformation = visualTransformation, shape = RoundedCornerShape(20.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = LassoSurfaceVariant, unfocusedContainerColor = LassoSurfaceVariant,
                disabledContainerColor = LassoSurfaceVariant, focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent, disabledIndicatorColor = Color.Transparent,
                cursorColor = LassoPrimary, focusedTextColor = LassoTextPrimary, unfocusedTextColor = LassoTextPrimary))
    }
}

@Composable
private fun AdminHelper(text: String) {
    Text(text, style = MaterialTheme.typography.bodySmall, color = LassoTextMuted)
}

@Composable
private fun AdminToggle(label: String, checked: Boolean, enabled: Boolean, change: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        Text(label, Modifier.weight(1f), color = LassoTextPrimary, style = MaterialTheme.typography.bodyMedium)
        Switch(checked, change, enabled = enabled)
    }
}
