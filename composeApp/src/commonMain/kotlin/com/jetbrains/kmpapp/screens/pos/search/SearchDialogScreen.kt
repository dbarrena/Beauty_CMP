package com.jetbrains.kmpapp.screens.pos.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jetbrains.kmpapp.model.BeautyItem
import com.jetbrains.kmpapp.screens.product.dialog.ProductDialogScreen

@Composable
fun SearchDialogScreen(
    beautyItems: List<BeautyItem>,
    onNewProductClicked: () -> Unit,
    onDismiss: (beautyItem: BeautyItem?) -> Unit
) {
    SearchDialogContent(beautyItems, onNewProductClicked, onDismiss)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDialogContent(
    beautyItems: List<BeautyItem>,
    onNewItemClicked: () -> Unit,
    onDismiss: (beautyItem: BeautyItem?) -> Unit
) {
    val isNewProductDialogDisplayed = remember { mutableStateOf(false) }
    val filteredBeautyItems = remember { mutableStateOf(beautyItems) }
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
            SearchBar { searchText ->
                filteredBeautyItems.value = if (searchText.isEmpty()) {
                    beautyItems
                } else {
                    beautyItems.filter {
                        it.name.contains(searchText, ignoreCase = true)
                    }
                }
            }
            Button(
                onClick = { /*onNewItemClicked() */ isNewProductDialogDisplayed.value = true},
                modifier = Modifier
                    .width(500.dp)
                    .padding(start = 8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Nuevo")
            }

            LazyColumn(
                modifier = Modifier.height(400.dp)
            ) {
                items(filteredBeautyItems.value) { item ->
                    SearchDialogItem(item) {
                        onDismiss(it)
                    }
                }
            }
        }
    }

    if (isNewProductDialogDisplayed.value) {
        ProductDialogScreen(
            beautyItem = null,
            onDismiss = { isNewProductDialogDisplayed.value = false }
        ) { product ->
            isNewProductDialogDisplayed.value = false
            onDismiss(product)
        }
    }
}

@Composable
private fun SearchBar(onSearch: (query: String) -> Unit) {
    val searchText = remember { mutableStateOf("") }

    OutlinedTextField(
        value = searchText.value,
        onValueChange = {
            searchText.value = it
            onSearch(it)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        placeholder = { Text("Buscar...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search Icon"
            )
        },
        shape = RoundedCornerShape(12.dp)
    )
}

private fun getSampleItems(): List<BeautyItem> {
    return listOf(
        /*BeautyItem("Corte de Cabello", 25.00),
        BeautyItem("Manicure y Pedicure", 35.50),
        BeautyItem("Limpieza Facial", 45.00),
        BeautyItem("Masaje Relajante", 60.00),
        BeautyItem("Tinte de Cabello", 80.00),
        BeautyItem("Depilación con Cera", 30.00),
        BeautyItem("Tratamiento Capilar", 55.00),
        BeautyItem("Maquillaje Profesional", 40.00),
        BeautyItem("Peinado para Evento", 70.00),
        BeautyItem("Extensiones de Pestañas", 90.00)*/
    )
}