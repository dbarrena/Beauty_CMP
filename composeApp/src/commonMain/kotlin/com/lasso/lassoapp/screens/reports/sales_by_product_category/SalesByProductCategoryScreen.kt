package com.lasso.lassoapp.screens.reports.sales_by_product_category

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.model.ProductCategory
import com.lasso.lassoapp.model.SalesByProductCategoryItemApiResponse
import com.lasso.lassoapp.screens.product_service.dialog.ProductCategoryPickerDialog
import com.lasso.lassoapp.screens.utils.CurrentMonthDateRangePicker
import com.lasso.lassoapp.screens.utils.FullScreenLoading
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SalesByProductCategoryScreen() {
    val viewModel = koinViewModel<SalesByProductCategoryViewModel>()
    SalesByProductCategoryScreenContent(viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SalesByProductCategoryScreenContent(viewModel: SalesByProductCategoryViewModel) {
    val state = viewModel.state.collectAsState()

    val showCategoryDialog = remember { mutableStateOf(false) }
    val selectedCategory = remember { mutableStateOf<ProductCategory?>(null) }

    if (state.value.isLoading) {
        FullScreenLoading()
    } else {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.onTertiary),
            ) {
                OutlinedButton(
                    onClick = { showCategoryDialog.value = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp, top = 12.dp),

                    shape = RoundedCornerShape(4.dp),

                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary
                    ),

                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        containerColor = Color.Transparent
                    )
                ) {
                    Text(
                        text = selectedCategory.value?.name ?: "Seleccionar categoría producto",
                        modifier = Modifier.fillMaxWidth().padding(4.dp), // ✅ expands text
                        textAlign = TextAlign.Start,         // ✅ aligns left
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }


                ProductCategoryPickerDialog(
                    isVisible = showCategoryDialog.value,
                    categories = state.value.productCategories,
                    onDismiss = { showCategoryDialog.value = false },
                    onSelect = { cat ->
                        selectedCategory.value = cat
                        showCategoryDialog.value = false
                        viewModel.setSelectedCategory(cat)
                    },
                    onNew = {
                        // open another dialog / navigate / call VM to create category
                        // viewModel.showCreateCategoryDialog()
                    }
                )

                state.value.categoryId?.let { categoryId ->
                    CurrentMonthDateRangePicker { start, end ->
                        viewModel.setSelectedDateRange(start, end)
                        if (start != null && end != null) viewModel.getSalesBetweenDates(start, end, categoryId)
                    }
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 8.dp).fillMaxSize()
            ) {
                if (state.value.products.isNotEmpty()) {
                    OutlinedTextField(
                        value = state.value.total ?: "$0.00",
                        onValueChange = {},
                        readOnly = true,
                        label = {
                            Text(
                                text = "Total ganancias",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        enabled = false,
                        textStyle = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = MaterialTheme.colorScheme.primary,
                            disabledLabelColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, top = 0.dp, end = 8.dp, bottom = 8.dp),
                    )

                    OutlinedTextField(
                        value = "${state.value.totalQuantity ?: 0} articulos",
                        onValueChange = {},
                        readOnly = true,
                        label = {
                            Text(
                                text = "Total de articulos",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        enabled = false,
                        textStyle = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = MaterialTheme.colorScheme.primary,
                            disabledLabelColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, top = 0.dp, end = 8.dp, bottom = 8.dp),
                    )
                }

                if (state.value.products.isEmpty()) {
                    EmptyScreen()
                }

                LazyColumn {
                    items(state.value.products) { product ->
                        ProductItem(product)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductItem(product: SalesByProductCategoryItemApiResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = product.name, style = MaterialTheme.typography.titleMedium)
            Text(text = "Total ganancia: ${product.totalRevenue}", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "Cantidad vendida: ${product.totalQuantitySold}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun EmptyScreen(modifier: Modifier = Modifier) {
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
                text = "No hay ventas disponibles",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
            Text(
                text = "Selecciona una categoría y rango de fechas",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
        }
    }
}