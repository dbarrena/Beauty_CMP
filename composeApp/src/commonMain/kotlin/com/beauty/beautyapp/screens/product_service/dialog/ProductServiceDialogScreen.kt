package com.beauty.beautyapp.screens.product_service.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.beauty.beautyapp.model.BeautyItem
import com.beauty.beautyapp.model.Product
import com.beauty.beautyapp.model.Service
import com.beauty.beautyapp.screens.utils.FullScreenLoading
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProductDialogScreen(
    beautyItem: BeautyItem?,
    onDismiss: () -> Unit,
    onProductServiceRegistered: (beautyItem: BeautyItem) -> Unit,
) {
    val viewModel = koinViewModel<ProductServiceDialogViewModel>()

    ProductDialogContent(viewModel, beautyItem, onDismiss, onProductServiceRegistered)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDialogContent(
    viewModel: ProductServiceDialogViewModel,
    beautyItem: BeautyItem?,
    onDismiss: () -> Unit,
    onProductServiceRegistered: (beautyItem: BeautyItem) -> Unit
) {
    val state = viewModel.state.collectAsState()

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val nameState = remember { mutableStateOf(beautyItem?.name ?: "") }
    val priceState = remember { mutableStateOf(beautyItem?.price ?: "") }
    var titlePrefix = remember { mutableStateOf("") }

    LaunchedEffect(beautyItem) {
        when (beautyItem) {
            is Product -> {
                titlePrefix.value = "Editar"
                viewModel.updateDialogType(DialogType.PRODUCT)
            }

            is Service -> {
                titlePrefix.value = "Editar"
                viewModel.updateDialogType(DialogType.SERVICE)
            }

            null -> {
                titlePrefix.value = "Nuevo"
            }
        }
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = "${titlePrefix.value} ${if (state.value.dialogType == DialogType.SERVICE) "Servicio" else "Producto"}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp, top = 8.dp)
            )

            DialogTypeSelector(
                selectedType = state.value.dialogType,
                isEnabled = beautyItem == null
            ) {
                viewModel.updateDialogType(it)
            }

            OutlinedTextField(
                value = nameState.value,
                onValueChange = { nameState.value = it },
                label = { Text("Nombre") },
                modifier = Modifier
                    .fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                )
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
            ProductDialogButton(state.value.isLoading) {
                if (state.value.dialogType == DialogType.SERVICE) {
                    val newService = Service(
                        name = nameState.value,
                        price = priceState.value
                    )
                    viewModel.registerService(newService)
                } else {
                    val newProduct = Product(
                        name = nameState.value,
                        price = priceState.value
                    )

                    viewModel.registerProduct(newProduct)
                }
            }
        }
    }

    LaunchedEffect(state.value.registeredProduct) {
        state.value.registeredProduct?.let {
            viewModel.resetState()
            onProductServiceRegistered(it)
        }
    }

    LaunchedEffect(state.value.registeredService) {
        state.value.registeredService?.let {
            viewModel.resetState()
            onProductServiceRegistered(it)
        }
    }
}

@Composable
private fun ProductDialogButton(isLoading: Boolean, onClick: () -> Unit) {
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
        if (isLoading) {
            FullScreenLoading(Color.White)
        } else{
            Text(
                "Registrar",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun DialogTypeSelector(
    selectedType: DialogType,
    isEnabled: Boolean = true,
    onChange: (DialogType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = selectedType == DialogType.SERVICE,
                enabled = isEnabled,
                onCheckedChange = {
                    if (it) onChange(DialogType.SERVICE)
                }
            )
            Text("Servicio")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = selectedType == DialogType.PRODUCT,
                enabled = isEnabled,
                onCheckedChange = {
                    if (it) onChange(DialogType.PRODUCT)
                }
            )
            Text("Producto")
        }
    }
}