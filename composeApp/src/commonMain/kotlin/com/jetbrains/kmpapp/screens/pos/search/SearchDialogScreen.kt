package com.jetbrains.kmpapp.screens.pos.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jetbrains.kmpapp.model.Service

@Composable
fun SearchDialogScreen(services: List<Service>, onDismiss: (service: Service?) -> Unit) {
    SearchDialogContent(services, onDismiss)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDialogContent(
    services: List<Service>,
    onDismiss: (service: Service?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { onDismiss(null) },
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            SearchBar()

            LazyColumn(
                modifier = Modifier.height(400.dp)
            ) {
                items(services) { item ->
                    SearchDialogItem(item) {
                        onDismiss(it)
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar() {
    val searchText = remember { mutableStateOf("") }

    OutlinedTextField(
        value = searchText.value,
        onValueChange = { searchText.value = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = { Text("Buscar servicio...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search Icon"
            )
        },
        shape = RoundedCornerShape(12.dp)
    )
}

private fun getSampleItems(): List<Service> {
    return listOf(
        /*Service("Corte de Cabello", 25.00),
        Service("Manicure y Pedicure", 35.50),
        Service("Limpieza Facial", 45.00),
        Service("Masaje Relajante", 60.00),
        Service("Tinte de Cabello", 80.00),
        Service("Depilación con Cera", 30.00),
        Service("Tratamiento Capilar", 55.00),
        Service("Maquillaje Profesional", 40.00),
        Service("Peinado para Evento", 70.00),
        Service("Extensiones de Pestañas", 90.00)*/
    )
}