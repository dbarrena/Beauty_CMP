package com.beauty.beautyapp.screens.sales.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.beauty.beautyapp.model.SaleApiResponse
import com.beauty.beautyapp.screens.sales.detail.edit_dialog.SaleDetailEditDialogScreen
import com.beauty.beautyapp.screens.utils.DeleteButton
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleDetailsDialogScreen(sale: SaleApiResponse, onDismiss: (needsRefresh: Boolean) -> Unit) {
    val viewModel = koinViewModel<SaleDetailsDialogScreenViewModel>()

    SaleDetailsDialogScreenContent(viewModel, sale, onDismiss)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleDetailsDialogScreenContent(
    viewModel: SaleDetailsDialogScreenViewModel,
    selectedSale: SaleApiResponse,
    onDismiss: (Boolean) -> Unit
) {
    val state = viewModel.state.collectAsState()

    val isEditSaleDetailDialogDisplayed = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = selectedSale.id) {
        viewModel.setSale(selectedSale)
    }

    Column(
        modifier = Modifier
        //.padding(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    onDismiss(state.value.dismissShouldReload)
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Close, // or use Icons.Filled.Close
                    contentDescription = "Close",
                    tint = Color.Black
                )
            }

            Text(
                text = "Detalle de venta",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
        }

        //Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Total:\n${state.value.sale?.total}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .weight(0.5f)
                    .padding(8.dp)
            )

            Text(
                text = "Fecha: ${state.value.sale?.formattedDate}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .weight(0.5f)
                    .padding(8.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Articulos: ${state.value.sale?.saleDetails?.size}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .weight(0.5f)
                    .padding(8.dp)
            )

            Text(
                text = "ID: ${state.value.sale?.id}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .weight(0.5f)
                    .padding(8.dp)
            )
        }



        state.value.sale?.saleDetails?.let { saleDetails ->
            LazyColumn(
                modifier = Modifier.height(250.dp).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(saleDetails) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left side: All information
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    it.product?.let {
                                        Text(
                                            text = it.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color.Black,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                    }

                                    it.service?.let {
                                        Text(
                                            text = it.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color.Black,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        modifier = Modifier.padding(end = 8.dp),
                                        text = "Cantidad: ${it.quantity}",
                                        color = Color.Black
                                    )

                                    Text(
                                        text = "Precio: ${it.price}",
                                        color = Color.Black
                                    )
                                }
                            }

                            // Right side: Edit button
                            IconButton(
                                onClick = {
                                    viewModel.setSaleDetail(it)
                                    isEditSaleDetailDialogDisplayed.value = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar Detalle",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }

        DeleteButton(
            text = "Eliminar Venta",
            isLoading = state.value.isLoading
        ) {
            viewModel.deleteSale {
                onDismiss(true)
            }
        }
    }

    if (isEditSaleDetailDialogDisplayed.value) {
        state.value.selectedSaleDetail?.let {
            SaleDetailEditDialogScreen(
                selectedSaleDetail = it,
                isLoading = state.value.isLoading,
                onConfirmEditChanges = { saleDetail ->
                    viewModel.editSaleDetail(saleDetail) {
                        isEditSaleDetailDialogDisplayed.value = false
                        viewModel.refreshSale()
                    }
                },
                onDelete = {
                    viewModel.deleteSaleDetail {
                        isEditSaleDetailDialogDisplayed.value = false
                        viewModel.refreshSale()
                    }
                },
                onDismiss = {
                    isEditSaleDetailDialogDisplayed.value = false
                }

            )
        }
    }

}