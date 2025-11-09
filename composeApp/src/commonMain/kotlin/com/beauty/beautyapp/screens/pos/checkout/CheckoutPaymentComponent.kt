package com.beauty.beautyapp.screens.pos.checkout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.beauty.beautyapp.screens.utils.StylizedTextField

@Composable
fun CheckoutPaymentComponent(
    paymentOptions: List<PaymentType>,
    selectedPosPayment: PosPayment,
    onSelectionChange: (PosPayment) -> Unit
) {
    val expanded = remember { mutableStateOf(false) }
    val totalInput = remember(selectedPosPayment.total) {
        mutableStateOf(if (selectedPosPayment.total == 0.0) "" else selectedPosPayment.total.toString())
    }

    Column {
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
                onSelectionChange(
                    selectedPosPayment.copy(
                        total = newValue.toDoubleOrNull() ?: 0.0
                    )
                )
            }
        )

        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .clickable { expanded.value = !expanded.value }
        ) {
            StylizedTextField(
                label = "Tipo de pago",
                body = selectedPosPayment.paymentType.display,
                readOnly = true,
                onClick = {
                    expanded.value = !expanded.value
                }
            )

            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                paymentOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = {
                            Text(text = selectionOption.display)
                        },
                        onClick = {
                            expanded.value = false
                            onSelectionChange(
                                selectedPosPayment.copy(
                                    paymentType = selectionOption
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}