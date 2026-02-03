package com.lasso.lassoapp.screens.product_service.dialog


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.model.ProductCategory
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color

@Composable
fun ProductCategoryPickerDialog(
    isVisible: Boolean,
    title: String = "Categorías Productos",
    categories: List<ProductCategory>,
    isLoading: Boolean = false,

    onDismiss: () -> Unit,
    onSelect: (ProductCategory) -> Unit,
    onNew: () -> Unit,
) {
    if (!isVisible) return

    val searchText = rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    val filtered = remember(categories, searchText.value) {
        val q = searchText.value.trim()
        if (q.isEmpty()) categories
        else categories.filter { it.name.contains(q, ignoreCase = true) }
    }

    AlertDialog(
        onDismissRequest = {
            keyboardController?.hide()
            onDismiss()
        },
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Search bar
                OutlinedTextField(
                    value = searchText.value,
                    onValueChange = { searchText.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Buscar") },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                )

                Spacer(modifier = Modifier.height(12.dp))

                // New button
                /*Button(
                    onClick = {
                        keyboardController?.hide()
                        onNew()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                ) {
                    Text("Nuevo")
                }*/

                Spacer(modifier = Modifier.height(12.dp))

                // List
                if (isLoading) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    if (categories.isEmpty()) {
                        Text("No hay categorías disponibles.")
                    } else if (filtered.isEmpty()) {
                        Text("No se encontraron resultados para \"${searchText.value}\".")
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 320.dp)
                        ) {
                            items(
                                items = filtered,
                                key = { it.id ?: it.name.hashCode() }
                            ) { item ->
                                CategoryRow(
                                    item = item,
                                    onClick = {
                                        keyboardController?.hide()
                                        onSelect(item)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                keyboardController?.hide()
                onDismiss()
            }) {
                Text("Cerrar")
            }
        },
        containerColor = Color.White
    )
}

@Composable
private fun CategoryRow(
    item: ProductCategory,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        tonalElevation = 1.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
