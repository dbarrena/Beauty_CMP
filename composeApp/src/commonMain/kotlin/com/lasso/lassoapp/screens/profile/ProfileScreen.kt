package com.lasso.lassoapp.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.model.Employee
import com.lasso.lassoapp.screens.register.RegistrationTextField
import com.lasso.lassoapp.screens.utils.LassoDialogFrame
import com.lasso.lassoapp.ui.theme.*
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(onBranchChanged: () -> Unit) {
    val viewModel = koinViewModel<ProfileViewModel>()
    val state by viewModel.state.collectAsState()
    LaunchedEffect(state.branchChanged) { if (state.branchChanged) onBranchChanged() }
    ProfileScreenContent(state, viewModel::openDialog, viewModel::loadProfile, viewModel::switchBranch)
    if (state.dialog != null) {
        ProfileFormDialog(state, viewModel::updateField, viewModel::save, viewModel::dismissDialog)
    }
}

@Composable
internal fun ProfileScreenContent(
    state: ProfileState,
    onOpenDialog: (ProfileDialog) -> Unit,
    onRetry: () -> Unit,
    onSwitchBranch: (Int) -> Unit = {},
) {
    Column(
        Modifier.fillMaxSize().background(LassoPageBackground)
            .verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column {
            Text("Perfil", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = LassoTextPrimary)
            Text("Información y configuración", style = MaterialTheme.typography.bodyMedium, color = LassoTextMuted)
        }
        if (state.isLoading) {
            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LassoPrimary)
            }
        }
        state.employee?.let { employee ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LassoPrimary.copy(alpha = 0.08f).compositeOver(LassoPageBackground),
                ),
                border = BorderStroke(1.dp, LassoOutlineHairline),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(Modifier.size(64.dp).background(LassoPrimary, CircleShape), contentAlignment = Alignment.Center) {
                            val initials = employee.name.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
                                .take(2).joinToString("") { it.take(1).uppercase() }
                            Text(initials, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                        }
                        Column(Modifier.weight(1f)) {
                            Text(employee.name, color = LassoTextPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(roleLabel(employee.role), color = LassoTextMuted, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    ProfileDetail("Correo electrónico", employee.email, Icons.Outlined.Email)
                    ProfileDetail("Cargo", roleLabel(employee.role), Icons.Outlined.WorkOutline)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { onOpenDialog(ProfileDialog.Edit) }, enabled = !state.isSaving,
                            modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, LassoPrimary),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = LassoPrimaryDark),
                        ) {
                            Text("Editar perfil", fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.sp,
                                maxLines = 1, softWrap = false, textAlign = TextAlign.Center)
                        }
                        OutlinedButton(
                            onClick = { onOpenDialog(ProfileDialog.Password) }, enabled = !state.isSaving,
                            modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = LassoTextPrimary),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                            ) {
                                //Icon(Icons.Default.Lock, null, Modifier.size(14.dp))
                                Text("Cambiar contraseña", fontSize = 12.sp, fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.sp, maxLines = 1, softWrap = false)
                            }
                        }
                    }
                }
            }
        }
        val isAdmin = state.employee?.role.equals("administrador", ignoreCase = true)
        if (isAdmin || state.branches.isNotEmpty()) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column {
                    Text("Sucursales", modifier = Modifier.padding(top = 16.dp), style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold, color = LassoTextPrimary)
                    Text("Gestiona multiples sucursales.", style = MaterialTheme.typography.bodyMedium,
                        color = LassoTextMuted)
                }
                if (isAdmin) {
                    BranchActionButton("Agregar sucursal", !state.isSaving && !state.isLoading,
                        { onOpenDialog(ProfileDialog.Branch) }, icon = Icons.Default.Add,
                        containerColor = LassoSecondaryLight.copy(alpha = 0.45f).compositeOver(Color.White),
                        contentColor = Color.Black)
                }
                state.branches.forEach { branch ->
                    Card(
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = LassoSecondaryLight.copy(alpha = 0.45f).compositeOver(Color.White),
                        ),
                        border = BorderStroke(1.dp, LassoOutlineHairline),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(Modifier.size(48.dp).background(LassoSecondary.copy(alpha = 0.14f), RoundedCornerShape(14.dp)),
                                    contentAlignment = Alignment.Center) {
                                    Icon(Icons.Outlined.Storefront, null, Modifier.size(24.dp), tint = LassoSecondary)
                                }
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(branch.partnerName.ifBlank { "Sucursal ${branch.partnerId}" },
                                        style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = LassoTextPrimary)
                                    Text("Sucursal guardada", style = MaterialTheme.typography.bodySmall, color = LassoGray)
                                }
                            }
                            BranchActionButton("Cambiar a esta sucursal", !state.isSaving && !state.isLoading,
                                { onSwitchBranch(branch.partnerId) }, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        }
        if (state.dialog == null) {
            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
                if (state.employee == null && !state.isLoading) TextButton(onClick = onRetry) { Text("Reintentar") }
            }
            state.success?.let { Text(it, color = LassoPrimaryDark, style = MaterialTheme.typography.bodyMedium) }
        }
    }
}

@Composable
private fun BranchActionButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    containerColor: Color = Color.White,
    contentColor: Color = Color.Black,
    fontWeight: FontWeight = FontWeight.Normal,
) {
    OutlinedButton(
        onClick = onClick, enabled = enabled, modifier = modifier.heightIn(min = 48.dp),
        shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, LassoSecondary),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = containerColor, contentColor = contentColor),
    ) {
        icon?.let {
            Icon(it, null, Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = fontWeight,
            textAlign = TextAlign.Center)
    }
}

private fun roleLabel(role: String): String = when (role.lowercase()) {
    "employee", "empleado" -> "Empleado"
    "administrador" -> "Administrador"
    else -> role.replaceFirstChar { it.uppercase() }
}

@Composable
private fun ProfileDetail(label: String, value: String, icon: ImageVector) {
    Row(
        Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(14.dp)).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(icon, contentDescription = null, tint = LassoPrimary, modifier = Modifier.size(20.dp))
        Column(Modifier.weight(1f)) {
            Text(label, color = LassoTextMuted, style = MaterialTheme.typography.labelMedium)
            Text(value, color = LassoTextPrimary, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
internal fun ProfileFormDialog(
    state: ProfileState,
    onFieldChange: (ProfileField, String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    val editing = state.dialog == ProfileDialog.Edit
    val addingBranch = state.dialog == ProfileDialog.Branch
    LassoDialogFrame(
        onDismiss = { if (!state.isSaving) onDismiss() },
        shape = RoundedCornerShape(16.dp), color = Color.White,
    ) {
            Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (addingBranch) "Agregar sucursal" else if (editing) "Editar perfil" else "Cambiar contraseña", Modifier.weight(1f),
                        style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = LassoTextPrimary)
                    IconButton(onClick = onDismiss, enabled = !state.isSaving) {
                        Icon(Icons.Default.Close, "Cerrar", tint = LassoTextMuted)
                    }
                }
                if (addingBranch) Text("Inicia sesión con la cuenta de la sucursal que quieres agregar.",
                    color = LassoTextPrimary, style = MaterialTheme.typography.bodyMedium)
                val fields = if (addingBranch) listOf(ProfileField.Email, ProfileField.Password)
                    else if (editing) listOf(ProfileField.Name, ProfileField.Email)
                    else listOf(ProfileField.Password, ProfileField.ConfirmPassword)
                fields.forEach { field ->
                    val (label, value, placeholder) = when (field) {
                        ProfileField.Name -> Triple("Nombre", state.name, "Tu nombre completo")
                        ProfileField.Email -> Triple("Correo electrónico", state.email, "nombre@correo.com")
                        ProfileField.Password -> if (addingBranch) Triple("Contraseña", state.password, "Tu contraseña")
                            else Triple("Nueva contraseña", state.password, "Mínimo 8 caracteres")
                        ProfileField.ConfirmPassword -> Triple("Confirmar nueva contraseña", state.confirmPassword, "Repite tu contraseña")
                    }
                    RegistrationTextField(
                        label = label, value = value, placeholder = placeholder, enabled = !state.isSaving,
                        onValueChange = { onFieldChange(field, it) }, error = state.fieldErrors[field],
                        keyboardType = when (field) {
                            ProfileField.Email -> KeyboardType.Email
                            ProfileField.Password, ProfileField.ConfirmPassword -> KeyboardType.Password
                            else -> KeyboardType.Text
                        },
                        imeAction = if (field == fields.last()) ImeAction.Done else ImeAction.Next,
                        onDone = onSave,
                    )
                }
                if (editing) {
                    Column {
                        Text("Cargo", color = LassoTextPrimary, style = MaterialTheme.typography.labelMedium)
                        Text(roleLabel(state.employee?.role.orEmpty()),
                            Modifier.fillMaxWidth().padding(top = 8.dp).background(LassoSurfaceVariant, RoundedCornerShape(14.dp)).padding(16.dp),
                            color = LassoTextMuted, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                state.error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                Button(
                    onClick = onSave, enabled = !state.isSaving,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp), shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LassoPrimary, contentColor = Color.White),
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(if (state.isSaving) { if (addingBranch) "Iniciando sesión..." else "Guardando..." }
                        else if (addingBranch) "Iniciar sesión" else if (editing) "Guardar cambios" else "Actualizar contraseña")
                }
            }
    }
}

@Preview
@Composable
private fun ProfilePreview() {
    MaterialTheme(colorScheme = LightLassoColorScheme, typography = lassoTypography()) {
        ProfileScreenContent(ProfileState(isLoading = false, employee = Employee(
            id = 1, name = "Amairany Mateos", email = "amairany@correo.com", role = "empleado", partnerId = 1, updatedAt = null,
        )), {}, {})
    }
}
