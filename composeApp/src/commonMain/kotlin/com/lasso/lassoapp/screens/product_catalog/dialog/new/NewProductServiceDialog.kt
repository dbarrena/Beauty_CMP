package com.lasso.lassoapp.screens.product_catalog.dialog.new

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lasso.lassoapp.model.Product
import com.lasso.lassoapp.model.ProductCategory
import com.lasso.lassoapp.model.Service
import com.lasso.lassoapp.screens.product_service.dialog.DialogType
import com.lasso.lassoapp.screens.product_service.dialog.ProductCategoryPickerDialog
import com.lasso.lassoapp.screens.product_service.dialog.ProductServiceDialogViewModel
import com.lasso.lassoapp.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NewProductServiceDialog(
    onDismiss: () -> Unit,
    onResult: () -> Unit,
) {
    val viewModel = koinViewModel<ProductServiceDialogViewModel>()
    val state by viewModel.state.collectAsState()

    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ProductCategory?>(null) }
    var showCategoryPicker by remember { mutableStateOf(false) }

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
                        text = if (state.dialogType == DialogType.SERVICE) "Nuevo Servicio" else "Nuevo Producto",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = LassoTextPrimary
                        ),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Type Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TypeButton(
                            text = "Servicio",
                            isSelected = state.dialogType == DialogType.SERVICE,
                            onClick = { viewModel.updateDialogType(DialogType.SERVICE) },
                            modifier = Modifier.weight(1f)
                        )
                        TypeButton(
                            text = "Producto",
                            isSelected = state.dialogType == DialogType.PRODUCT,
                            onClick = { viewModel.updateDialogType(DialogType.PRODUCT) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

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
                            placeholder = { Text("Nombre", color = LassoTextPlaceholder) },
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
                            placeholder = { Text("Precio", color = LassoTextPlaceholder) },
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

                    // Categoría Field
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

                    Spacer(modifier = Modifier.height(32.dp))

                    // Register Button
                    Button(
                        onClick = {
                            if (state.dialogType == DialogType.SERVICE) {
                                viewModel.registerService(Service(name = name, price = price))
                            } else {
                                viewModel.registerProduct(Product(name = name, price = price, categoryId = selectedCategory?.id))
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
                                text = "Registrar",
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

@Composable
private fun TypeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) LassoPrimary.copy(alpha = 0.1f) else Color.White
    val borderColor = if (isSelected) LassoPrimary else LassoDivider
    val textColor = LassoTextPrimary

    Box(
        modifier = modifier
            .height(50.dp)
            .background(backgroundColor, RoundedCornerShape(20.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(if (isSelected) LassoPrimary else Color.White, CircleShape)
                    .border(1.5.dp, if (isSelected) LassoPrimary else LassoDivider, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = textColor,
                    fontSize = 16.sp
                )
            )
        }
    }
}
