package com.lasso.lassoapp.screens.admin

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.data.remote.endpoints.*
import com.lasso.lassoapp.ui.theme.*
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

internal val adminTimeZone = TimeZone.of("America/Mexico_City")
internal fun adminDate(epoch: Long?): String = epoch?.let {
    Instant.fromEpochMilliseconds(it).toLocalDateTime(adminTimeZone).date.toString()
} ?: "Sin vencimiento"
internal val adminMembershipTypes = listOf("trial", "premium")
internal fun planName(plan: String?) = when (plan) { "premium" -> "Premium"; else -> "Prueba" }

@Composable
fun AdminScreen() {
    val model = koinViewModel<AdminViewModel>()
    val state by model.state.collectAsState()
    Surface(Modifier.fillMaxSize(), color = LassoPageBackground) {
        if (!state.authorized) {
            Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                if (state.loading) CircularProgressIndicator() else {
                    Text(state.error ?: "Acceso exclusivo para cuentas master")
                    TextButton(onClick = model::refreshAccess) { Text("Reintentar") }
                }
            }
        } else {
            val partner = state.selectedPartner
            LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(vertical = 16.dp)) {
                item {
                    Text("Administracion Lasso", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = LassoTextPrimary)
                    Text("Partners, membresías, uso y permisos", color = LassoTextMuted)
                }
                if (partner == null) {
                    item {
                        Button(onClick = { model.editPartner(create = true) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                            Icon(Icons.Default.Add, null); Spacer(Modifier.width(8.dp)); Text("Nuevo partner")
                        }
                    }
                    item {
                        OutlinedTextField(state.search, model::search, Modifier.fillMaxWidth(), label = { Text("Buscar por nombre o correo") }, singleLine = true,
                            trailingIcon = { IconButton(onClick = { model.loadPartners(1) }, enabled = !state.loading) { Icon(Icons.Default.Search, "Buscar") } })
                    }
                    item {
                        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            (listOf<String?>(null) + adminMembershipTypes).forEach { plan ->
                                FilterChip(state.plan == plan, { model.filter(plan, state.active) }, label = { Text(if (plan == null) "Todas las membresías" else planName(plan)) })
                            }
                        }
                        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(null, true, false).forEach { active ->
                                FilterChip(state.active == active, { model.filter(state.plan, active) }, label = { Text(when (active) { true -> "Activos"; false -> "Suspendidos"; null -> "Todos los estados" }) })
                            }
                        }
                    }
                    if (state.error != null) item { AdminError(state.error!!) { model.loadPartners() } }
                    if (state.loading) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
                    if (!state.loading && state.error == null && state.partners.isEmpty()) item {
                        AdminCard { Text("No se encontraron partners"); Text("Prueba otra búsqueda o crea un partner.", color = LassoTextMuted) }
                    }
                    items(state.partners, key = { it.id }) { item ->
                        Card(onClick = { model.selectPartner(item) }, enabled = !state.loading, colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(item.name, fontWeight = FontWeight.SemiBold, color = LassoTextPrimary)
                                Text(item.email, color = LassoTextMuted)
                                Text("${planName(item.licenseType)} · ${if (!item.isActive) "Suspendido" else if (item.accessAllowed) "Acceso activo" else "Membresía vencida"}", color = if (item.accessAllowed) LassoPrimaryDark else LassoTertiaryDark)
                                Text("${item.salesUsage.used} ventas · ${item.salesUsage.limit?.let { "Límite $it" } ?: "Sin límite"}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                    item {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            TextButton(onClick = { model.loadPartners(state.page - 1) }, enabled = state.page > 1 && !state.loading) { Text("Anterior") }
                            Text("${state.page} / ${maxOf(1, (state.total + 24) / 25)}")
                            TextButton(onClick = { model.loadPartners(state.page + 1) }, enabled = state.page * 25 < state.total && !state.loading) { Text("Siguiente") }
                        }
                    }
                } else {
                    item { TextButton(onClick = model::closePartner) { Text("Volver a partners") } }
                    if (state.partnerLoading) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
                    if (state.partnerError != null) item { AdminError(state.partnerError!!) { model.loadPartnerDetails() } }
                    item {
                        AdminCard {
                            Text(partner.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(partner.email, color = LassoTextMuted)
                            partner.phone?.let { Text(it) }
                            Text("Membresía: ${planName(partner.licenseType)}")
                            Text("Vencimiento: ${adminDate(partner.licenseExpiry)}")
                            Text(if (!partner.isActive) "Cuenta suspendida" else if (partner.accessAllowed) "Acceso activo" else "Membresía vencida", color = if (partner.accessAllowed) LassoPrimaryDark else LassoTertiaryDark)
                            OutlinedButton(onClick = { model.editPartner() }, enabled = !state.partnerLoading && state.partnerError == null,
                                modifier = Modifier.fillMaxWidth()) { Text("Editar partner y membresía") }
                        }
                    }
                    item {
                        AdminCard {
                            Text("Uso de ventas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            val usage = partner.salesUsage
                            Text("${usage.used} / ${usage.limit?.toString() ?: "Sin límite"}", style = MaterialTheme.typography.headlineSmall, color = LassoPrimaryDark)
                            usage.limit?.let { limit ->
                                LinearProgressIndicator(progress = { (usage.used.toFloat() / limit.coerceAtLeast(1)).coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth())
                                Text("${usage.remaining ?: 0} ventas disponibles")
                            }
                            Text(if (usage.canCreateSale && partner.accessAllowed) "Puede registrar ventas" else "Registro de ventas bloqueado", color = LassoTextPrimary)
                            Text("El uso es acumulado de por vida y se conserva al cambiar de membresía.", style = MaterialTheme.typography.bodySmall, color = LassoTextMuted)
                        }
                    }
                    item {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text("Empleados y permisos", Modifier.weight(1f), fontWeight = FontWeight.Bold)
                            TextButton(onClick = { model.editEmployee() }) { Text("Nuevo") }
                        }
                    }
                    if (state.error != null) item { AdminError(state.error!!) { model.loadEmployees() } }
                    if (state.employeesLoading) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
                    if (state.employeesError != null) item { AdminError(state.employeesError!!) { model.loadEmployees() } }
                    if (!state.employeesLoading && state.employeesError == null && state.employees.isEmpty()) item { AdminCard { Text("Este partner aún no tiene empleados") } }
                    items(state.employees, key = { it.id }) { employee ->
                        Card(onClick = { model.editEmployee(employee) }, enabled = !employee.isMaster && !state.employeesLoading, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(employee.name, fontWeight = FontWeight.SemiBold)
                                Text(employee.email, color = LassoTextMuted)
                                Text("${if (employee.role == "administrador") "Administrador" else "Empleado"} · ${if (employee.disabled) "Desactivado" else "Activo"}")
                                Text("Ventas: ${if (employee.canSeeSales || employee.role == "administrador") "Visibles" else "Ocultas"} · Calendario: ${if (employee.isDisplayedOnCalendar) "Visible" else "Oculto"}", style = MaterialTheme.typography.bodySmall)
                                if (employee.isMaster) Text("Cuenta master protegida", color = LassoPrimaryDark)
                            }
                        }
                    }
                }
            }
        }
    }
    if (state.authorized && state.partnerEditor) PartnerEditor(state, model::closeEditor, model::savePartner)
    if (state.authorized && state.employeeEditor) EmployeeEditor(state, model::closeEditor, model::saveEmployee)
}

@Composable
internal fun AdminCard(content: @Composable ColumnScope.() -> Unit) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp), content = content)
    }
}

@Composable
private fun AdminError(message: String, retry: () -> Unit) {
    AdminCard { Text(message, color = MaterialTheme.colorScheme.error); TextButton(onClick = retry) { Text("Reintentar") } }
}
