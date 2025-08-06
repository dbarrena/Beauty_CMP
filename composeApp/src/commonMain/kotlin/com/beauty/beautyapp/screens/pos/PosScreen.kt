package com.beauty.beautyapp.screens.pos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.beauty.beautyapp.screens.pos.checkout.CheckoutDialogScreen
import com.beauty.beautyapp.screens.pos.search.SearchDialogScreen
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PosScreen() {
    val viewModel = koinViewModel<PosViewModel>()

    PosScreenContent(viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PosScreenContent(viewModel: PosViewModel) {
    val isSearchDialogDisplayed = remember { mutableStateOf(false) }
    val isCheckoutDialogDisplayed = remember { mutableStateOf(false) }
    val isSaleRegisteredDisplayed = remember { mutableStateOf(false) }
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false,
        )
    )
    val scope = rememberCoroutineScope()

    val state = viewModel.state.collectAsState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetSwipeEnabled = false,
        sheetDragHandle = null,
        sheetContainerColor = Color.White,
        sheetTonalElevation = 8.dp, // subtle tonal overlay
        sheetShadowElevation = 16.dp, // noticeable shadow
        sheetContent = {
            println("DIEGO bottom sheet ${state.value.availableItems.size}")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, top = 18.dp, bottom = 0.dp)
                    .height(500.dp) // ✅ max height here
            ) {
                SearchDialogScreen(
                    isAvailableItemsLoading = state.value.isAvailableItemsLoading,
                    beautyItems = state.value.availableItems
                ) { service ->
                    service?.let {
                        viewModel.updateItemsList(service)
                    }
                    scope.launch { scaffoldState.bottomSheetState.hide() }
                    isSearchDialogDisplayed.value = false
                }
            }
        }
    ) {
        Column {
            SearchBar {
                isSearchDialogDisplayed.value = true
                viewModel.getAvailableItems()
                scope.launch { scaffoldState.bottomSheetState.expand() }
            }

            if (state.value.selectedPosItems.isEmpty() && isSaleRegisteredDisplayed.value == false) {
                EmptyScreen(modifier = Modifier.weight(1f, true))
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f, true),
                ) {
                    items(
                        state.value.selectedPosItems,
                        key = { it.instanceId }
                    ) { item ->
                        PosBeautyItem(
                            modifier = Modifier.animateItem(),
                            item = item,
                            onBeautyItemDeleted = { viewModel.removeItem(it) },
                            onBeautyItemClicked = {}
                        )
                    }
                }
            }

            CheckOutButton(state.value.totalPrice.toString()) {
                isCheckoutDialogDisplayed.value = true
            }
        }
    }

    if (isCheckoutDialogDisplayed.value) {
        CheckoutDialogScreen(
            beautyItems = state.value.selectedPosItems,
            totalPrice = state.value.totalPrice,
            onDismiss = { isSuccess ->
                isCheckoutDialogDisplayed.value = false

                if (isSuccess) {
                    isSaleRegisteredDisplayed.value = true
                    viewModel.restartPos()
                }
            }
        )
    }

    if (isSaleRegisteredDisplayed.value) {
        DisplaySaleAnimation {
            isSaleRegisteredDisplayed.value = false
        }
    }
}

@Composable
fun DisplaySaleAnimation(onTimerFinished: () -> Unit) {
    val isVisible = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible.value = true
        kotlinx.coroutines.delay(1500)
        isVisible.value = false
        onTimerFinished.invoke()
    }

    if (isVisible.value) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = isVisible.value,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.9f))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "Venta Registrada",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun CheckOutButton(totalPrice: String, onCheckOutClicked: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            modifier = Modifier.fillMaxWidth().height(55.dp),
            shape = RoundedCornerShape(4.dp),
            onClick = onCheckOutClicked,
            elevation = ButtonDefaults.buttonElevation(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = "Buscar Servicios"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cobrar $$totalPrice",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun SearchBar(onSearchClicked: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSearchClicked() }
            .background(Color.LightGray.copy(alpha = 0.5f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = "Search Icon",
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Buscar servicio o producto...",
            color = Color.Gray
        )
    }
}

@Composable
private fun EmptyScreen(modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Esta venta está vacía",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
            Text(
                text = "Agrega servicios o productos a esta venta",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
        }
    }
}
