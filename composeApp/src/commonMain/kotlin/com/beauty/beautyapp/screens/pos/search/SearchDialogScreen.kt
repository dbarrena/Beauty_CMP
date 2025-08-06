package com.beauty.beautyapp.screens.pos.search

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
import com.beauty.beautyapp.model.BeautyItem
import com.beauty.beautyapp.screens.product_service.dialog.ProductDialogScreen
import com.beauty.beautyapp.screens.utils.FullScreenLoading
import com.beauty.beautyapp.screens.utils.SearchBar

@Composable
fun SearchDialogScreen(
    isAvailableItemsLoading: Boolean,
    beautyItems: List<BeautyItem>,
    onDismiss: (beautyItem: BeautyItem?) -> Unit
) {
    SearchDialogContentNoDialog(isAvailableItemsLoading, beautyItems, onDismiss)
}


@Composable
private fun SearchDialogContentNoDialog(
    isAvailableItemsLoading: Boolean,
    beautyItems: List<BeautyItem>,
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
                items(filteredBeautyItems.value) { item ->
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
            beautyItem = null,
            onDismiss = { isNewProductDialogDisplayed.value = false }
        ) { product ->
            isNewProductDialogDisplayed.value = false
            keyboardController?.hide()
            onDismiss(product)
        }
    }
}