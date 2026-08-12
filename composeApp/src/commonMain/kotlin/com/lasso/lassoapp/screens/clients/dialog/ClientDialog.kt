package com.lasso.lassoapp.screens.clients.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lasso.lassoapp.model.Client
import com.lasso.lassoapp.model.ClientWriteRequest
import com.lasso.lassoapp.ui.theme.LassoPrimary
import com.lasso.lassoapp.ui.theme.LassoSurfaceVariant
import com.lasso.lassoapp.ui.theme.LassoTextMuted
import com.lasso.lassoapp.ui.theme.LassoTextPrimary

@Composable
fun ClientDialog(
    client: Client?,
    isLoading: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onSave: (ClientWriteRequest) -> Unit,
) {
    var name by remember(client?.id) { mutableStateOf(client?.name.orEmpty()) }
    var phone by remember(client?.id) { mutableStateOf(client?.phone.orEmpty()) }
    var email by remember(client?.id) { mutableStateOf(client?.email.orEmpty()) }
    var notes by remember(client?.id) { mutableStateOf(client?.notes.orEmpty()) }
    val isValid = name.isNotBlank() && name.length <= 255 && phone.length <= 20 && email.length <= 255

    Dialog(onDismissRequest = { if (!isLoading) onDismiss() }) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Column(
                modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text(
                    text = if (client == null) "Nuevo cliente" else "Editar cliente",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = LassoTextPrimary,
                    ),
                    modifier = Modifier.padding(bottom = 10.dp),
                )
                ClientField(
                    value = name,
                    onValueChange = { if (it.length <= 255) name = it },
                    label = "Nombre *",
                    enabled = !isLoading,
                )
                ClientField(
                    value = phone,
                    onValueChange = { if (it.length <= 20) phone = it },
                    label = "Teléfono",
                    enabled = !isLoading,
                    keyboardType = KeyboardType.Phone,
                )
                ClientField(
                    value = email,
                    onValueChange = { if (it.length <= 255) email = it },
                    label = "Email",
                    enabled = !isLoading,
                    keyboardType = KeyboardType.Email,
                )
                ClientField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = "Notas",
                    enabled = !isLoading,
                    singleLine = false,
                )
                error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                }
                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = {
                        onSave(
                            ClientWriteRequest(
                                name = name.trim(),
                                phone = phone.trim().ifBlank { null },
                                email = email.trim().ifBlank { null },
                                notes = notes.trim().ifBlank { null },
                            )
                        )
                    },
                    enabled = isValid && !isLoading,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LassoPrimary),
                ) {
                    if (isLoading) {
                        Text(
                            text = "Guardando...",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                            ),
                        )
                    } else {
                        Text(
                            text = if (client == null) "Registrar" else "Guardar",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                            ),
                        )
                    }
                }
                TextButton(
                    onClick = onDismiss,
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                ) {
                    Text(
                        text = "Cancelar",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = LassoTextMuted,
                            fontSize = 16.sp,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun ClientField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    enabled: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium,
                color = LassoTextPrimary,
                fontSize = 14.sp,
            ),
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().height(if (singleLine) 48.dp else 110.dp),
            enabled = enabled,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = LassoSurfaceVariant,
                unfocusedContainerColor = LassoSurfaceVariant,
                disabledContainerColor = LassoSurfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = LassoPrimary,
                focusedTextColor = LassoTextPrimary,
                unfocusedTextColor = LassoTextPrimary,
            ),
            shape = RoundedCornerShape(14.dp),
            singleLine = singleLine,
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        )
    }
}
