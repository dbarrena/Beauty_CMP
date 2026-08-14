package com.lasso.lassoapp.screens.sales.detail.edit_dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lasso.lassoapp.model.SaleDetailApiResponse
import com.lasso.lassoapp.model.SaleDetailEditApiRequest
import com.lasso.lassoapp.ui.theme.LassoPrimary
import com.lasso.lassoapp.ui.theme.LassoTextMuted
import com.lasso.lassoapp.ui.theme.LassoTextPrimary

@Composable
fun SaleDetailEditDialogScreen(
    selectedSaleDetail: SaleDetailApiResponse,
    isLoading: Boolean,
    onConfirmEditChanges: (SaleDetailEditApiRequest) -> Unit,
    onDismiss: () -> Unit,
) {
    var quantity by remember(selectedSaleDetail.id) {
        mutableStateOf(selectedSaleDetail.quantity.toString())
    }
    var price by remember(selectedSaleDetail.id) {
        mutableStateOf(selectedSaleDetail.price.replace("$", "").replace(",", ""))
    }
    val parsedQuantity = quantity.toIntOrNull()
    val parsedPrice = price.toDoubleOrNull()
    val isValid = parsedQuantity != null && parsedQuantity > 0 && parsedPrice != null && parsedPrice >= 0.0

    Dialog(onDismissRequest = { if (!isLoading) onDismiss() }) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Box(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Editar artículo",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = LassoTextPrimary,
                        ),
                    )
                    Spacer(Modifier.height(24.dp))
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it.filter(Char::isDigit) },
                        label = { Text("Cantidad") },
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = price,
                        onValueChange = { value ->
                            if (value.count { it == '.' } <= 1 && value.all { it.isDigit() || it == '.' }) {
                                price = value
                            }
                        },
                        label = { Text("Precio unitario") },
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    )
                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = {
                            onConfirmEditChanges(
                                SaleDetailEditApiRequest(
                                    id = selectedSaleDetail.id,
                                    quantity = requireNotNull(parsedQuantity),
                                    price = requireNotNull(parsedPrice),
                                ),
                            )
                        },
                        enabled = isValid && !isLoading,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LassoPrimary),
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Text("Guardar", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        }
                    }
                    TextButton(
                        onClick = onDismiss,
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                    ) {
                        Text("Cancelar", color = LassoTextMuted, fontWeight = FontWeight.SemiBold)
                    }
                }

                if (!isLoading) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.TopEnd).size(24.dp),
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = LassoTextMuted,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
        }
    }
}
