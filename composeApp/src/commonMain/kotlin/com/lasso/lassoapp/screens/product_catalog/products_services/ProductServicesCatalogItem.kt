package com.lasso.lassoapp.screens.product_catalog.products_services

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.model.LassoItem
import com.lasso.lassoapp.model.Product
import lassoapp.composeapp.generated.resources.Res
import lassoapp.composeapp.generated.resources.pencil_icon
import lassoapp.composeapp.generated.resources.trash_icon
import org.jetbrains.compose.resources.painterResource

@Composable
fun ProductServicesCatalogItem(
    lassoItem: LassoItem,
    onEditClick: (lassoItem: LassoItem) -> Unit,
    onDeleteClick: (lassoItem: LassoItem) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = lassoItem.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight(600),
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (lassoItem is Product) {
                        lassoItem.category?.let {
                            Text(
                                text = "Categoría: ${lassoItem.category}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight(500),
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Text(
                            text = "Producto",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight(500),
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else {
                        Text(
                            text = "Servicio",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight(500),
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Text(
                    text = "$${lassoItem.price}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight(700),
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                IconButton(onClick = { onEditClick(lassoItem) }) {
                    Icon(
                        modifier = Modifier.size(22.dp),
                        painter = painterResource(Res.drawable.pencil_icon),
                        contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = { onDeleteClick(lassoItem) }) {
                    Icon(
                        modifier = Modifier.size(22.dp),
                        painter = painterResource(Res.drawable.trash_icon),
                        contentDescription = "Eliminar",
                        tint = Color.Unspecified
                    )
                }
            }
        }
    }
}
