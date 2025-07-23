package com.jetbrains.kmpapp.screens.product.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jetbrains.kmpapp.model.BeautyItem
import com.jetbrains.kmpapp.model.Product
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProductDialogScreen(
    beautyItem: BeautyItem?,
    onDismiss: () -> Unit,
    onProductRegistered: (beautyItem: BeautyItem) -> Unit,
) {
    val viewModel = koinViewModel<ProductDialogViewModel>()

    ProductDialogContent(viewModel, beautyItem, onDismiss, onProductRegistered)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDialogContent(
    viewModel: ProductDialogViewModel,
    beautyItem: BeautyItem?,
    onDismiss: () -> Unit,
    onProductRegistered: (beautyItem: BeautyItem) -> Unit
) {
    val state = viewModel.state.collectAsState()

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val nameState = remember { mutableStateOf(beautyItem?.name ?: "") }
    val priceState = remember { mutableStateOf(beautyItem?.price ?: "") }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            /*ToggleButton(
                arrayOf(
                    ToggleButtonOption("Producto", null),
                    ToggleButtonOption("Servicio", null)
                )
            )*/
            OutlinedTextField(
                value = nameState.value,
                onValueChange = { nameState.value = it },
                label = { Text("Nombre") },
                modifier = Modifier
                    .fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.padding(vertical = 6.dp))
            OutlinedTextField(
                value = priceState.value,
                onValueChange = { priceState.value = it },
                label = { Text("Precio") },
                modifier = Modifier
                    .fillMaxWidth(),
                maxLines = 3,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.padding(vertical = 12.dp))
            ProductDialogButton {
                val newProduct = Product(
                    name = nameState.value,
                    price = priceState.value,
                    partnerId = 1
                )
                viewModel.registerProduct(newProduct)
            }
        }
    }

    LaunchedEffect(state.value.registeredProduct) {
        state.value.registeredProduct?.let {
            onProductRegistered(it)
        }
    }
}

@Composable
private fun ProductDialogButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        elevation = ButtonDefaults.buttonElevation(6.dp)
    ) {
        Text(
            "Guardar Cambios",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        )
    }
}