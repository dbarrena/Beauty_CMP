package com.lasso.lassoapp.screens.employees.dialog.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lasso.lassoapp.model.Employee
import com.lasso.lassoapp.screens.employees.EmployeesViewModel
import com.lasso.lassoapp.screens.employees.dialog.new.EmployeeField
import com.lasso.lassoapp.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.coroutines.launch

@Composable
fun EditEmployeeDialog(
    employee: Employee,
    onDismiss: () -> Unit,
    onResult: () -> Unit
) {
    val viewModel = koinViewModel<EmployeesViewModel>()
    
    var name by remember { mutableStateOf(employee.name) }
    var email by remember { mutableStateOf(employee.email) }
    var role by remember { mutableStateOf(employee.role) }
    var productCommission by remember { mutableStateOf(employee.productCommissionPercentage ?: "0") }
    var serviceCommission by remember { mutableStateOf(employee.serviceCommissionPercentage ?: "0") }
    var showRoleMenu by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Editar Perfil",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = LassoTextPrimary
                        ),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Nombre Field
                    EmployeeField(label = "Nombre", value = name, onValueChange = { name = it })

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email Field
                    EmployeeField(label = "Email", value = email, onValueChange = { email = it })

                    Spacer(modifier = Modifier.height(16.dp))

                    // Role Field
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Cargo",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = LassoTextPrimary,
                                fontSize = 14.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .background(LassoSurfaceVariant, RoundedCornerShape(14.dp))
                                .clickable { showRoleMenu = true }
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = role.replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = LassoTextMuted
                                )
                            }
                            DropdownMenu(
                                expanded = showRoleMenu,
                                onDismissRequest = { showRoleMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Administrador") },
                                    onClick = {
                                        role = "administrador"
                                        showRoleMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Empleado") },
                                    onClick = {
                                        role = "empleado"
                                        showRoleMenu = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Commissions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            EmployeeField(
                                label = "Comisión Prod. (%)",
                                value = productCommission,
                                onValueChange = { productCommission = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            EmployeeField(
                                label = "Comisión Serv. (%)",
                                value = serviceCommission,
                                onValueChange = { serviceCommission = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Save Button
                    Button(
                        onClick = {
                            isLoading = true
                            scope.launch {
                                viewModel.editEmployee(
                                    employee.copy(
                                        name = name,
                                        email = email,
                                        role = role,
                                        productCommissionPercentage = productCommission,
                                        serviceCommissionPercentage = serviceCommission
                                    )
                                )
                                isLoading = false
                                onResult()
                            }
                        },
                        enabled = name.isNotBlank() && email.isNotBlank() && !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LassoPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Text(
                                text = "Guardar Cambios",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Cancel Button
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = "Cancelar",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = LassoTextMuted,
                                fontSize = 16.sp
                            )
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 12.dp, y = (-12).dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = LassoTextMuted.copy(alpha = 0.7f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
