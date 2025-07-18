package com.jetbrains.kmpapp.screens.pos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jetbrains.kmpapp.model.Service
import com.jetbrains.kmpapp.screens.list.ListViewModel
import com.jetbrains.kmpapp.screens.pos.search.SearchDialogScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PosScreen() {
    PosScreenContent()
}

@Composable
private fun PosScreenContent() {
    val viewModel = koinViewModel<PosViewModel>()
    val isDialogDisplayed = remember { mutableStateOf(false) }

    val state = viewModel.state.collectAsState()

    Column {
        SearchBar {
            isDialogDisplayed.value = true
        }

        if (state.value.selectedServices.isEmpty()) {
            EmptyScreen(modifier = Modifier.weight(1f, true)) {
                isDialogDisplayed.value = true
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f, true)) {
                items(state.value.selectedServices) { service ->
                    PosServiceItem(service, onServiceClicked = {})
                }
            }
        }

        CheckOutButton(state.value.totalPrice.toString()) {
            viewModel.getAvailableServices()
        }
    }

    if (isDialogDisplayed.value) {
        SearchDialogScreen(services = state.value.availableServices) { service ->
            service?.let {
                viewModel.updateServicesList(service)
            }
            isDialogDisplayed.value = false
        }
    }
}

@Composable
private fun CheckOutButton(totalPrice: String, onCheckOutClicked: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray.copy(alpha = 0.5f)) // Light gray background
            .padding(horizontal = 16.dp, vertical = 12.dp), // Padding inside the search bar
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(4.dp),
            onClick = onCheckOutClicked
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = "Buscar Servicios"
                )
                Spacer(modifier = Modifier.width(8.dp)) // Space between icon and text in button
                Text("Cobrar $$totalPrice")
            }
        }
    }
}

@Composable
private fun SearchBar(onSearchClicked: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp) // Add some padding around the search bar
            .clip(RoundedCornerShape(12.dp)) // Rounded corners
            .clickable { onSearchClicked() } // Add clickable with ripple effect
            .background(Color.LightGray.copy(alpha = 0.5f)) // Light gray background
            .padding(horizontal = 16.dp, vertical = 12.dp), // Padding inside the search bar
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = "Search Icon",
            tint = Color.Gray // Optional: Set icon color
        )
        Spacer(modifier = Modifier.width(8.dp)) // Space between icon and text
        Text(
            text = "Buscar servicio...",
            color = Color.Gray // Optional: Set text color
        )
    }
}

@Composable
private fun EmptyScreen(modifier: Modifier, onSearchClicked: () -> Unit = {}) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center // 1. Center the Column within the Box
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, // 2. Center Text and Button horizontally
            verticalArrangement = Arrangement.spacedBy(16.dp) // 3. Add space BETWEEN items in the Column
        ) {
            Text(
                text = "Esta venta está vacía",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
            Text(
                text = "Agrega servicios a esta venta",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
            Button(
                onClick = onSearchClicked
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Buscar Servicios"
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // Space between icon and text in button
                    Text("Buscar Servicios")
                }
            }
        }
    }
}
