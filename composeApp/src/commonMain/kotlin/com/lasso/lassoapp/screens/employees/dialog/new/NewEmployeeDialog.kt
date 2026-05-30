package com.lasso.lassoapp.screens.employees.dialog.new

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.lasso.lassoapp.ui.theme.*

@Composable
fun NewEmployeeDialog(
    onDismiss: () -> Unit,
    onResult: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("empleado") }
    var productCommission by remember { mutableStateOf("0") }
    var serviceCommission by remember { mutableStateOf("0") }
    var showRoleMenu by remember { mutableStateOf(false) }

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
                        text = "Nuevo Empleado",
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
                            // TODO: API call for register employee (not provided yet, but assuming it exists or will be added)
                            // For now just result
                            onResult()
                        },
                        enabled = name.isNotBlank() && email.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LassoPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "Registrar",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        )
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

@Composable
fun EmployeeField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium,
                color = LassoTextPrimary,
                fontSize = 14.sp
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = LassoSurfaceVariant,
                unfocusedContainerColor = LassoSurfaceVariant,
                disabledContainerColor = LassoSurfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = LassoPrimary,
                focusedTextColor = LassoTextPrimary,
                unfocusedTextColor = LassoTextPrimary
            ),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
            keyboardOptions = keyboardOptions
        )
    }
}
