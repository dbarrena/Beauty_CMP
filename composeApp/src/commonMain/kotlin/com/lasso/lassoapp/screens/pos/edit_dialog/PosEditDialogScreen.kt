package com.lasso.lassoapp.screens.pos.edit_dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.screens.pos.SelectedPosItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosEditDialogScreen(
    selectedPosItem: SelectedPosItem,
    onDismiss: (selectedPosItem: SelectedPosItem?) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val quantityState = remember { mutableStateOf(selectedPosItem.quantity.toString()) }
    val priceState = remember { mutableStateOf(selectedPosItem.price.toString()) }
    val quantity = quantityState.value.toIntOrNull()
    val price = priceState.value.toDoubleOrNull()
    val isValid =
        quantity != null && quantity > 0 && price != null && price.isFinite() && price >= 0

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { onDismiss(null) },
        dragHandle = null,
    ) {
        Column(
            modifier = Modifier.imePadding().verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            Text(
                text = "Editar Selección",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp, top = 8.dp),
            )
            Text(
                text = selectedPosItem.lassoItem.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            OutlinedTextField(
                value = quantityState.value.toString(),
                onValueChange = { quantityState.value = it },
                label = { Text("Cantidad") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            Spacer(modifier = Modifier.padding(vertical = 6.dp))
            OutlinedTextField(
                value = priceState.value,
                onValueChange = { priceState.value = it },
                label = { Text("Precio") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
            Spacer(modifier = Modifier.padding(vertical = 12.dp))
            ConfirmChangesButton(enabled = isValid) {
                val updatedPosItem =
                    selectedPosItem.copy(
                        quantity = quantity ?: return@ConfirmChangesButton,
                        price = price ?: return@ConfirmChangesButton,
                    )

                onDismiss(updatedPosItem)
            }
        }
    }
}

@Composable
private fun ConfirmChangesButton(enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
        elevation = ButtonDefaults.buttonElevation(6.dp),
    ) {
        Text(
            "Confirmar Cambios",
            style =
                MaterialTheme.typography.titleMedium.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                ),
        )
    }
}
