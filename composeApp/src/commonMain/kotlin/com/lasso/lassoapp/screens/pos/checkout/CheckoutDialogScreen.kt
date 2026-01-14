package com.lasso.lassoapp.screens.pos.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.lasso.lassoapp.screens.pos.SelectedPosItem
import com.lasso.lassoapp.screens.utils.StylizedTextField
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CheckoutDialogScreen(
    totalPrice: Double,
    items: List<SelectedPosItem>,
    onDismiss: (Boolean) -> Unit
) {
    val viewModel = koinViewModel<CheckoutDialogViewModel>()

    CheckoutDialogContent(viewModel, totalPrice, items, onDismiss)
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

    val addPaymentSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    val isPaymentDropdownDisplayed = remember {
        mutableStateOf(false)
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
        ) {
            Text(
                text = "Nueva Venta",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            StylizedTextField(
                label = "Restante",
                body = "$${state.value.remainingTotal}",
                readOnly = true,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            if (state.value.payments.isNotEmpty()) {
                LazyColumn(
                    Modifier
                        .padding(bottom = 24.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    item {
                        Row(Modifier.background(MaterialTheme.colorScheme.primary)) {
                            TableCell(
                                text = "Tipo Pago",
                                weight = .4f,
                                color = Color.White,
                                style = MaterialTheme.typography.titleSmall
                            )
                            TableCell(
                                text = "Total",
                                weight = .6f,
                                color = Color.White,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }

                    items(
                        items = state.value.payments
                    ) { payment ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TableCell(text = payment.paymentType.display, weight = .4f)
                            TableCell(text = "$${payment.total}", weight = .4f)
                            IconButton(
                                modifier = Modifier.weight(.2f),
                                onClick = {
                                    viewModel.removePayment(payment.paymentType)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.surfaceTint
                                )
                            }
                        }
                    }
                }
            }

            if (viewModel.getPaymentTypes().isNotEmpty()) {
                AddPaymentButton {
                    isPaymentDropdownDisplayed.value = true
                }
            }

            CheckoutButton(state.value.isLoading) {
                viewModel.registerSale(items)
            }
        }
    }

    if (isPaymentDropdownDisplayed.value) {
        Dialog(
            onDismissRequest = {
                isPaymentDropdownDisplayed.value = false
            }
        ) {
            CheckoutPaymentComponent(
                paymentOptions = viewModel.getPaymentTypes(),
                total = state.value.remainingTotal
            ) { updatedPosPayment ->
                viewModel.addPayment(updatedPosPayment)
                isPaymentDropdownDisplayed.value = false
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
                color = Color.White,
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
            .padding(bottom = 16.dp)
            .fillMaxWidth()
            .height(56.dp),
        elevation = ButtonDefaults.buttonElevation(6.dp)
    ) {
        Text(
            "Agregar Pago",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        )
    }
}

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
    color: Color = Color.Black,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = color,
        style = style,
        modifier = modifier
            //.border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp)
    )
}
