package com.beauty.beautyapp.screens.sales.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.beauty.beautyapp.model.SaleApiResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleDetailsDialogScreen(sale: SaleApiResponse, onDismiss: (needsRefresh: Boolean) -> Unit) {
    SaleDetailsDialogScreenContent(sale, onDismiss)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleDetailsDialogScreenContent(
    sale: SaleApiResponse,
    onDismiss: (Boolean) -> Unit
) {
    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier.padding(16.dp)
            ){
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp),
                    onClick = { onDismiss(false) },
                    elevation = ButtonDefaults.buttonElevation(6.dp)
                ) {
                    Text(
                        "Cerrar",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    )
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
        ) {
            Text(
                text = "Venta No.${sale.id}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Total: ${sale.total}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.padding(bottom = 50.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(sale.saleDetails) {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            it.product?.let {
                                Text(
                                    text = it.name,
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                Text(text = "Tipo: Producto")
                            }

                            it.service?.let {
                                Text(
                                    text = it.name,
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                Text(text = "Tipo: Servicio")
                            }
                            Text(text = "Cantidad: ${it.quantity}")
                            Text(text = "Precio: ${it.price}")
                        }
                    }
                }
            }
        }
    }

}