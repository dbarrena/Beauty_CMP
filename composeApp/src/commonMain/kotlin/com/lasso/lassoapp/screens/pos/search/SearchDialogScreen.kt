package com.lasso.lassoapp.screens.pos.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.model.LassoItem
import com.lasso.lassoapp.screens.product_service.dialog.ProductDialogScreen
import com.lasso.lassoapp.screens.utils.FullScreenLoading
import com.lasso.lassoapp.screens.utils.SearchBar

@Composable
fun SearchDialogScreen(
    isAvailableItemsLoading: Boolean,
    lassoItems: List<LassoItem>,
    onDismiss: (lassoItem: LassoItem?) -> Unit
) {
    SearchDialogContentNoDialog(isAvailableItemsLoading, lassoItems, onDismiss)
}


@Composable
private fun SearchDialogContentNoDialog(
    isAvailableItemsLoading: Boolean,
    lassoItems: List<LassoItem>,
    onDismiss: (lassoItem: LassoItem?) -> Unit
) {
    val isNewProductDialogDisplayed = remember { mutableStateOf(false) }
    val filteredItems = remember { mutableStateOf(lassoItems) }
    val searchText = remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(lassoItems) {
        filteredItems.value = lassoItems
    }

    LaunchedEffect(searchText.value) {
        filteredItems.value = if (searchText.value.isEmpty()) {
            lassoItems
        } else {
            lassoItems.filter {
                it.name.contains(searchText.value, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        IconButton(
            onClick = {
                keyboardController?.hide()
                onDismiss(null)
            },
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
                .fillMaxWidth()
                .padding(start = 8.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Nuevo")
        }

        if (isAvailableItemsLoading) {
            FullScreenLoading()
        } else {
            LazyColumn(
                modifier = Modifier.height(400.dp).padding(top = 8.dp)
            ) {
                items(filteredItems.value) { item ->
                    SearchDialogItem(item) {
                        searchText.value = ""
                        keyboardController?.hide()
                        onDismiss(it)
                    }
                }
            }
        }
    }

    if (isNewProductDialogDisplayed.value) {
        ProductDialogScreen(
            lassoItem = null,
            onDismiss = { isNewProductDialogDisplayed.value = false }
        ) { product ->
            isNewProductDialogDisplayed.value = false
            keyboardController?.hide()
            onDismiss(product)
        }
    }
}