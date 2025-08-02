package com.beauty.beautyapp.screens.sales.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    Column(
        modifier = Modifier
        //.padding(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onDismiss(false) },
            ) {
                Icon(
                    imageVector = Icons.Default.Close, // or use Icons.Filled.Close
                    contentDescription = "Close",
                    tint = Color.Black
                )
            }

            Text(
                text = "Venta No.${sale.id}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
        }

        //Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Total: ${sale.total}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier.height(400.dp).padding(16.dp),
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