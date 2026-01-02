package com.lasso.lassoapp.screens.pos.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.model.LassoItem
import com.lasso.lassoapp.model.Product

@Composable
fun SearchDialogItem(lassoItem: LassoItem, onClick: (lassoItem: LassoItem) -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = {
            onClick(lassoItem)
        },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = lassoItem.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF353D3C)
                )

                Text(
                    text = "$${lassoItem.price}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF353D3C)
                )
            }
            if (lassoItem is Product) {
                lassoItem.category?.let {
                    Text(
                        text = "Categoria: ${lassoItem.category}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF353D3C)
                    )
                }
                Text(
                    text = "Producto",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF353D3C)
                )
            } else{
                Text(
                    text = "Servicio",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF353D3C)
                )
            }
        }
    }
}