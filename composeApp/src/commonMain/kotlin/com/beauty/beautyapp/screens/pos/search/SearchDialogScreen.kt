package com.beauty.beautyapp.screens.pos.search

import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.beauty.beautyapp.model.BeautyItem
import com.beauty.beautyapp.screens.product.dialog.ProductDialogScreen

@Composable
fun SearchDialogScreen(
    beautyItems: List<BeautyItem>,
    onNewProductClicked: () -> Unit,
    onDismiss: (beautyItem: BeautyItem?) -> Unit
) {
    SearchDialogContentNoDialog(beautyItems, onNewProductClicked, onDismiss)
}



@Composable
private fun SearchDialogContentNoDialog(
    beautyItems: List<BeautyItem>,
    onNewItemClicked: () -> Unit,
    onDismiss: (beautyItem: BeautyItem?) -> Unit
) {
    val isNewProductDialogDisplayed = remember { mutableStateOf(false) }
    val filteredBeautyItems = remember { mutableStateOf(beautyItems) }
    val searchText = remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(beautyItems) {
        filteredBeautyItems.value = beautyItems
    }

    LaunchedEffect(searchText.value) {
        filteredBeautyItems.value = if (searchText.value.isEmpty()) {
            beautyItems
        } else {
            beautyItems.filter {
                it.name.contains(searchText.value, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        IconButton(
            onClick = { onDismiss(null) },
            //modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(
                imageVector = Icons.Default.Close, // or use Icons.Filled.Close
                contentDescription = "Close",
                tint = Color.Black
            )
        }

        SearchBar(searchText.value, keyboardController) {
            searchText.value = it
        }
        Button(
            onClick = { /*onNewItemClicked() */ isNewProductDialogDisplayed.value = true },
            modifier = Modifier
                .width(500.dp)
                .padding(start = 8.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Nuevo")
        }

        LazyColumn(
            modifier = Modifier.height(400.dp).padding(top = 8.dp)
        ) {
            items(filteredBeautyItems.value) { item ->
                SearchDialogItem(item) {
                    searchText.value = ""
                    keyboardController?.hide()
                    onDismiss(it)
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

/*@OptIn(ExperimentalMaterial3Api::class)
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
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            SearchBar(searchText) { searchText ->
                filteredBeautyItems.value = if (searchText.isEmpty()) {
                    beautyItems
                } else {
                    beautyItems.filter {
                        it.name.contains(searchText, ignoreCase = true)
                    }
                }
            }
            Button(
                onClick = { /*onNewItemClicked() */ isNewProductDialogDisplayed.value = true },
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
}*/

@Composable
private fun SearchBar(
    searchText: String,
    keyboardController: SoftwareKeyboardController?,
    onSearch: (query: String) -> Unit
) {
    OutlinedTextField(
        value = searchText,
        onValueChange = {
            onSearch(it)
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
            }
        ),
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