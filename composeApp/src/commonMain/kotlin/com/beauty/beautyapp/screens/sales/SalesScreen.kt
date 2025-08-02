package com.beauty.beautyapp.screens.sales

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.beauty.beautyapp.model.SaleApiResponse
import com.beauty.beautyapp.screens.sales.detail.SaleDetailsDialogScreen
import com.beauty.beautyapp.screens.utils.FullScreenLoading
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SalesScreen() {
    val viewModel = koinViewModel<SalesScreenViewModel>()
    SalesScreenContent(viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SalesScreenContent(viewModel: SalesScreenViewModel) {
    val state = viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false,
        )
    )

    if (state.value.isLoading) {
        FullScreenLoading()
    } else {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = 0.dp,
            sheetSwipeEnabled = false,
            sheetDragHandle = null,
            sheetContainerColor = Color.White,
            sheetTonalElevation = 8.dp, // subtle tonal overlay
            sheetShadowElevation = 16.dp, // noticeable shadow
            sheetContent = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 0.dp)
                        .height(500.dp) // ✅ max height here
                ) {
                    viewModel.state.collectAsState().value.selectedSale?.let {
                        SaleDetailsDialogScreen(it) {
                            scope.launch { scaffoldState.bottomSheetState.hide() }
                        }
                    }
                }
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier.padding(8.dp).fillMaxSize()
            ) {
                OutlinedTextField(
                    value = "$${state.value.total}",
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(
                            text = "Total",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    enabled = false,
                    textStyle = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = MaterialTheme.colorScheme.primary,
                        disabledLabelColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                )

                LazyColumn {
                    items(state.value.sales) { sale ->
                        SaleItem(sale) { selectedSale ->
                            println("SaleItem clicked: ${selectedSale.id}")
                            viewModel.setSelectedSale(selectedSale)
                            scope.launch { scaffoldState.bottomSheetState.expand() }
                        }
                    }
                }
            }
        }
    }

    /*SaleDetailsDialogScreen(
        scaffoldState = saleDetailsScaffoldState,
        saleDetails = state.value.selectedSale?.saleDetails ?: emptyList(),
        onDismiss = { needsRefresh ->
            scope.launch { saleDetailsScaffoldState.bottomSheetState.hide() }

            if (needsRefresh) {
                viewModel.getSales()
            }
        }
    )*/


}

@Composable
private fun SaleItem(sale: SaleApiResponse, onSaleClick: (SaleApiResponse) -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = { onSaleClick(sale) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Total: ${sale.total}", style = MaterialTheme.typography.titleMedium)
            Text(text = "ID: ${sale.id}", style = MaterialTheme.typography.bodyMedium)
            sale.clientId?.let { clientId ->
                Text(text = "Cliente: $clientId", style = MaterialTheme.typography.bodyMedium)
            }
            Text(
                text = "Tipo de pago: ${if (sale.paymentType == "cash") "Efectivo" else "Tarjeta"}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Fecha: ${sale.createdAt}",
                style = MaterialTheme.typography.bodyMedium
            )
            sale.updatedAt?.let { updatedAt ->
                Text(text = "Updated At: $updatedAt", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}