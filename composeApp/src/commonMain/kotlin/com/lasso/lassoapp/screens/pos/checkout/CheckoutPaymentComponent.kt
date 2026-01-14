package com.lasso.lassoapp.screens.pos.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.screens.utils.StylizedTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutPaymentComponent(
    paymentOptions: List<PaymentType>,
    total: Double,
    onSelectionChange: (PosPayment) -> Unit
) {
    val selectedPaymentType = remember { mutableStateOf(paymentOptions.first()) }

    val dropdownPaymentTypeExpanded = remember { mutableStateOf(false) }
    val totalInput = remember(total) {
        mutableStateOf(if (total == 0.0) "" else total.toString())
    }
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StylizedTextField(
                label = "Total de pago",
                body = totalInput.value,
                readOnly = false,
                enabled = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                onValueChange = { newValue ->
                    totalInput.value = newValue
                }
            )

            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .clickable {
                        dropdownPaymentTypeExpanded.value = !dropdownPaymentTypeExpanded.value
                    }
            ) {
                StylizedTextField(
                    label = "Tipo de pago",
                    body = selectedPaymentType.value.display,
                    readOnly = true,
                    onClick = {
                        dropdownPaymentTypeExpanded.value = !dropdownPaymentTypeExpanded.value
                    }
                )

                DropdownMenu(
                    expanded = dropdownPaymentTypeExpanded.value,
                    onDismissRequest = { dropdownPaymentTypeExpanded.value = false }
                ) {
                    paymentOptions.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = {
                                Text(text = selectionOption.display)
                            },
                            onClick = {
                                dropdownPaymentTypeExpanded.value = false
                                selectedPaymentType.value = selectionOption
                            }
                        )
                    }
                }
            }

            AddPaymentButton {
                val payment = PosPayment(
                    total = totalInput.value.toDouble(),
                    paymentType = selectedPaymentType.value
                )
                onSelectionChange(payment)
            }
        }
    }
}

@Composable
private fun AddPaymentButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .height(56.dp),
        elevation = ButtonDefaults.buttonElevation(6.dp)
    ) {
        Text(
            "Agregar Pago",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        )
    }
}