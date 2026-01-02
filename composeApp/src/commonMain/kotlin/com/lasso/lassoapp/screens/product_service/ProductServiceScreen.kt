package com.lasso.lassoapp.screens.product_service

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.screens.pos.search.SearchDialogItem
import com.lasso.lassoapp.screens.product_service.dialog.ProductDialogScreen
import com.lasso.lassoapp.screens.utils.FullScreenLoading
import com.lasso.lassoapp.screens.utils.SearchBar
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun ProductServiceScreen() {
    val viewModel = koinViewModel<ProductServiceViewModel>()

    ProductServiceContent(viewModel)
}

@Composable
fun ProductServiceContent(viewModel: ProductServiceViewModel) {
    val state = viewModel.state.collectAsState()
    val availableItems = state.value.availableItems
    val filteredItems = remember { mutableStateOf(availableItems) }
    val searchText = remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(availableItems) {
        filteredItems.value = availableItems
    }

    LaunchedEffect(searchText.value) {
        filteredItems.value = if (searchText.value.isEmpty()) {
            availableItems
        } else {
            availableItems.filter {
                it.name.contains(searchText.value, ignoreCase = true)
            }
        }
    }

    if (state.value.isLoading) {
        FullScreenLoading()
    } else {
        Column(
            modifier = Modifier.padding(8.dp).fillMaxSize()
        ) {
            SearchBar(searchText.value, keyboardController) {
                searchText.value = it
            }

            Button(
                onClick = {
                    keyboardController?.hide()
                    viewModel.showDialog()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Nuevo")
            }

            LazyColumn(
                modifier = Modifier.padding(8.dp)
            ) {
                items(filteredItems.value) {
                    SearchDialogItem(it) { item ->
                        viewModel.showDialog(item)
                    }
                }
            }
        }
    }

    if (state.value.isDialogDisplayed) {
        ProductDialogScreen(
            lassoItem = state.value.selectedItem,
            onDismiss = { viewModel.hideDialog() }
        ) { product ->
            viewModel.hideDialog()
            viewModel.getAvailableItems()
        }
    }
}