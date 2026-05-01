package com.lasso.lassoapp.screens.product_catalog.dialog.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lasso.lassoapp.model.LassoItem
import com.lasso.lassoapp.model.Product
import com.lasso.lassoapp.model.Service
import com.lasso.lassoapp.screens.product_service.dialog.ProductCategoryPickerDialog
import com.lasso.lassoapp.screens.product_service.dialog.ProductServiceDialogViewModel
import com.lasso.lassoapp.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EditProductServiceDialog(
    lassoItem: LassoItem,
    onDismiss: () -> Unit,
    onResult: () -> Unit,
) {
    val viewModel = koinViewModel<ProductServiceDialogViewModel>()
    val state by viewModel.state.collectAsState()

    var name by remember { mutableStateOf(lassoItem.name) }
    var price by remember { mutableStateOf(lassoItem.price) }
    var selectedCategory by remember { 
        mutableStateOf(state.productCategories.find { it.id == (lassoItem as? Product)?.categoryId }) 
    }
    var showCategoryPicker by remember { mutableStateOf(false) }

    // Update selectedCategory when productCategories are loaded
    LaunchedEffect(state.productCategories) {
        if (selectedCategory == null) {
            selectedCategory = state.productCategories.find { it.id == (lassoItem as? Product)?.categoryId }
        }
    }

    LaunchedEffect(state.registeredProduct, state.registeredService) {
        if (state.registeredProduct != null || state.registeredService != null) {
            viewModel.resetState()
            onResult()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = LassoTextMuted.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (lassoItem is Service) "Editar Servicio" else "Editar Producto",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = LassoTextPrimary
                        ),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Nombre Field
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Nombre",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = LassoTextPrimary,
                                fontSize = 14.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = name,
                            onValueChange = { name = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = LassoSurfaceVariant,
                                unfocusedContainerColor = LassoSurfaceVariant,
                                disabledContainerColor = LassoSurfaceVariant,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                cursorColor = LassoPrimary,
                                focusedTextColor = LassoTextPrimary,
                                unfocusedTextColor = LassoTextPrimary
                            ),
                            shape = RoundedCornerShape(20.dp),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Precio Field
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Precio ($)",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = LassoTextPrimary,
                                fontSize = 14.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = price,
                            onValueChange = { price = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = LassoSurfaceVariant,
                                unfocusedContainerColor = LassoSurfaceVariant,
                                disabledContainerColor = LassoSurfaceVariant,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                cursorColor = LassoPrimary,
                                focusedTextColor = LassoTextPrimary,
                                unfocusedTextColor = LassoTextPrimary
                            ),
                            shape = RoundedCornerShape(20.dp),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Categoría Dropdown (Figma shows it even for Service)
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Categoría",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = LassoTextPrimary,
                                fontSize = 14.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .border(1.5.dp, LassoPrimary, RoundedCornerShape(20.dp))
                                .clickable { showCategoryPicker = true }
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = selectedCategory?.name ?: "Seleccionar categoría",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = LassoTextPrimary,
                                        fontSize = 14.sp
                                    )
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = LassoTextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tipo Indicator Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LassoPrimary.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Column {
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                                        append("Tipo: ")
                                    }
                                    append(if (lassoItem is Service) "Servicio" else "Producto")
                                },
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = LassoTextPrimary,
                                    fontSize = 14.sp
                                )
                            )
                            Text(
                                text = "El tipo no puede ser modificado",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = LassoTextMuted,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Save Button
                    Button(
                        onClick = {
                            if (lassoItem is Service) {
                                viewModel.registerService(lassoItem.copy(name = name, price = price))
                            } else if (lassoItem is Product) {
                                viewModel.registerProduct(lassoItem.copy(name = name, price = price, categoryId = selectedCategory?.id))
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LassoPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Guardar Cambios",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Cancel Button
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = "Cancelar",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = LassoTextMuted,
                                fontSize = 16.sp
                            )
                        )
                    }
                }
            }
        }
    }

    if (showCategoryPicker) {
        ProductCategoryPickerDialog(
            isVisible = showCategoryPicker,
            categories = state.productCategories,
            onDismiss = { showCategoryPicker = false },
            onSelect = {
                selectedCategory = it
                showCategoryPicker = false
            },
            onNew = {
                // Handle new category if needed
            }
        )
    }
}
