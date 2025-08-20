package com.beauty.beautyapp.screens.pos.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.beauty.beautyapp.screens.pos.SelectedPosItem
import com.beauty.beautyapp.screens.utils.DropdownMenuPaymentType
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CheckoutDialogScreen(
    totalPrice: Double,
    beautyItems: List<SelectedPosItem>,
    onDismiss: (Boolean) -> Unit
) {
    val viewModel = koinViewModel<CheckoutDialogViewModel>()

    CheckoutDialogContent(viewModel, totalPrice, beautyItems, onDismiss)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutDialogContent(
    viewModel: CheckoutDialogViewModel,
    totalPrice: Double,
    items: List<SelectedPosItem>,
    onDismiss: (Boolean) -> Unit
) {
    val state = viewModel.state.collectAsState()

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    val isDropDownExpanded = remember {
        mutableStateOf(true)
    }

    LaunchedEffect(state.value.success) {
        if (state.value.success) {
            viewModel.restartCheckout()
            onDismiss(true)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.setTotal(totalPrice)
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { onDismiss(false) },
        dragHandle = null,
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Nueva Venta",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = "$$totalPrice",
                onValueChange = {},
                readOnly = true,
                label = {
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.titleSmall
                    )
                },
                enabled = false,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = MaterialTheme.colorScheme.primary,
                    disabledLabelColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
            )
            DropdownMenuPaymentType { paymentType ->
                viewModel.updatePaymentType(paymentType)
            }
            CheckoutButton(state.value.isLoading) {
                viewModel.registerSale(items)
            }
        }
    }
}

@Composable
private fun CheckoutButton(isLoading: Boolean, onClick: () -> Unit) {
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
            CircularProgressIndicator(
                modifier = Modifier.height(24.dp),
                strokeWidth = 2.dp
            )
        } else {
            Text(
                "Registrar Venta",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            )
        }
    }
}
