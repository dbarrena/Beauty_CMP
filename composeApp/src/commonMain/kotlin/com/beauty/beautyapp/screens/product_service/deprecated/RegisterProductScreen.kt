package com.beauty.beautyapp.screens.product_service.deprecated

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import com.beauty.beautyapp.model.Product

@Composable
fun RegisterProductScreen(product: Product?, onBackNavigation: (Product?) -> Unit) {
    RegisterProductScreenContent(product, onBackNavigation = { onBackNavigation(null) }) {
        onBackNavigation(it)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterProductScreenContent(
    product: Product?,
    onBackNavigation: () -> Unit,
    onProductSubmit: (Product) -> Unit
) {
    val scrollState = rememberScrollState()

    // Keep field values in state, initialize from product if present
    val nameState = remember { mutableStateOf(product?.name ?: "") }
    val descState = remember { mutableStateOf(product?.description ?: "") }
    val priceState = remember { mutableStateOf(product?.price ?: "") }
    val costState = remember { mutableStateOf(product?.cost ?: "") }
    val stockState = remember { mutableStateOf(product?.stock?.toString() ?: "") }
    val partnerIdState = remember { mutableStateOf(product?.partnerId?.toString() ?: "") }
    val categoryState = remember { mutableStateOf(product?.category ?: "") }

    Scaffold(
        bottomBar = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Button(
                    onClick = {
                        val newProduct = Product(
                            name = nameState.value,
                            description = descState.value,
                            price = priceState.value,
                            cost = if (costState.value.isNotBlank()) costState.value else null,
                            stock = stockState.value.toIntOrNull(),
                            partnerId = partnerIdState.value.toIntOrNull() ?: 0,
                            createdAt = product?.createdAt ?: "",
                            updatedAt = null,
                            category = categoryState.value
                        )
                        onProductSubmit(newProduct)
                    },
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
                    Text(
                        "Guardar Cambios",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                //.padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = nameState.value,
                onValueChange = { nameState.value = it },
                label = { Text("Nombre") },
                modifier = Modifier
                    .fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.padding(vertical = 6.dp))
            OutlinedTextField(
                value = descState.value,
                onValueChange = { descState.value = it },
                label = { Text("Descripción") },
                modifier = Modifier
                    .fillMaxWidth(),
                maxLines = 3
            )
            Spacer(modifier = Modifier.padding(vertical = 6.dp))
            OutlinedTextField(
                value = priceState.value,
                onValueChange = { priceState.value = it },
                label = { Text("Precio") },
                modifier = Modifier
                    .fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.padding(vertical = 6.dp))
            OutlinedTextField(
                value = costState.value,
                onValueChange = { costState.value = it },
                label = { Text("Costo") },
                modifier = Modifier
                    .fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.padding(vertical = 6.dp))
            OutlinedTextField(
                value = stockState.value,
                onValueChange = { stockState.value = it },
                label = { Text("Stock") },
                modifier = Modifier
                    .fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.padding(vertical = 6.dp))
            OutlinedTextField(
                value = partnerIdState.value,
                onValueChange = { partnerIdState.value = it },
                label = { Text("ID del Partner") },
                modifier = Modifier
                    .fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.padding(vertical = 6.dp))
            OutlinedTextField(
                value = categoryState.value,
                onValueChange = { categoryState.value = it },
                label = { Text("Categoría") },
                modifier = Modifier
                    .fillMaxWidth(),
                singleLine = true
            )
        }
    }
}
