package com.lasso.lassoapp.screens.product_catalog.dialog.new

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.model.LassoItem
import com.lasso.lassoapp.model.Product
import com.lasso.lassoapp.model.ProductCategory
import com.lasso.lassoapp.model.Service
import com.lasso.lassoapp.screens.product_service.dialog.DialogType
import com.lasso.lassoapp.screens.product_service.dialog.ProductCategoryPickerDialog
import com.lasso.lassoapp.screens.product_service.dialog.ProductServiceDialogViewModel
import com.lasso.lassoapp.screens.utils.AdaptiveEqualPair
import com.lasso.lassoapp.screens.utils.DialogContentScaffold
import com.lasso.lassoapp.screens.utils.LassoDialogFrame
import com.lasso.lassoapp.screens.utils.labelWidth
import com.lasso.lassoapp.ui.theme.LassoDivider
import com.lasso.lassoapp.ui.theme.LassoPrimary
import com.lasso.lassoapp.ui.theme.LassoSurfaceVariant
import com.lasso.lassoapp.ui.theme.LassoTextMuted
import com.lasso.lassoapp.ui.theme.LassoTextPrimary
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NewProductServiceDialog(
    categories: List<ProductCategory>,
    onDismiss: () -> Unit,
    onResult: (LassoItem) -> Unit,
) {
    val viewModel = koinViewModel<ProductServiceDialogViewModel>()
    val state by viewModel.state.collectAsState()

    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var commissionPercentage by remember { mutableStateOf<String?>(null) }
    var selectedCategory by remember { mutableStateOf<ProductCategory?>(null) }
    var showCategoryPicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.resetState() }

    LaunchedEffect(state.registeredProduct, state.registeredService) {
        val registeredItem = state.registeredProduct ?: state.registeredService
        if (registeredItem != null) {
            viewModel.resetState()
            onResult(registeredItem)
        }
    }

    LassoDialogFrame(
        onDismiss = onDismiss,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        expanded = true,
    ) {
        DialogContentScaffold(
            onClose = onDismiss,
            modifier = Modifier.fillMaxSize(),
            header = {
                Text(
                    if (state.dialogType == DialogType.SERVICE) "Nuevo Servicio"
                    else "Nuevo Producto",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    style =
                        MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                        ),
                )
            },
            actions = {
                Column {
                    // Register Button
                    Button(
                        onClick = {
                            if (state.dialogType == DialogType.SERVICE) {
                                viewModel.registerService(
                                    Service(
                                        name = name,
                                        price = price,
                                        commissionPercentage = commissionPercentage,
                                        categoryId = selectedCategory?.id,
                                    )
                                )
                            } else {
                                viewModel.registerProduct(
                                    Product(
                                        name = name,
                                        price = price,
                                        commissionPercentage = commissionPercentage,
                                        categoryId = selectedCategory?.id,
                                    )
                                )
                            }
                        },
                        enabled = name.isNotBlank() && price.isNotBlank() && !state.isLoading,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = LassoPrimary,
                                contentColor = Color.White,
                            ),
                        shape = RoundedCornerShape(20.dp),
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Text(
                                text = "Registrar",
                                style =
                                    MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp,
                                    ),
                            )
                        }
                    }

                    state.error?.let { error ->
                        Text(
                            error,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // Cancel Button
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                    ) {
                        Text(
                            text = "Cancelar",
                            style =
                                MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = LassoTextMuted,
                                    fontSize = 16.sp,
                                ),
                        )
                    }
                }
            },
        ) {
            // Type Selector
            AdaptiveEqualPair(
                minFieldWidth =
                    labelWidth(
                        "Producto",
                        MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                    ) + 44.dp,
                first = { typeModifier ->
                    TypeButton(
                        text = "Servicio",
                        isSelected = state.dialogType == DialogType.SERVICE,
                        onClick = { viewModel.updateDialogType(DialogType.SERVICE) },
                        modifier = typeModifier,
                    )
                },
                second = { typeModifier ->
                    TypeButton(
                        text = "Producto",
                        isSelected = state.dialogType == DialogType.PRODUCT,
                        onClick = { viewModel.updateDialogType(DialogType.PRODUCT) },
                        modifier = typeModifier,
                    )
                },
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                // Nombre Field
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Nombre",
                        style =
                            MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = LassoTextPrimary,
                                fontSize = 14.sp,
                            ),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                            TextFieldDefaults.colors(
                                focusedContainerColor = LassoSurfaceVariant,
                                unfocusedContainerColor = LassoSurfaceVariant,
                                disabledContainerColor = LassoSurfaceVariant,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                cursorColor = LassoPrimary,
                                focusedTextColor = LassoTextPrimary,
                                unfocusedTextColor = LassoTextPrimary,
                            ),
                        shape = RoundedCornerShape(20.dp),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Precio Field
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Precio ($)",
                        style =
                            MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = LassoTextPrimary,
                                fontSize = 14.sp,
                            ),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = price,
                        onValueChange = { price = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                            TextFieldDefaults.colors(
                                focusedContainerColor = LassoSurfaceVariant,
                                unfocusedContainerColor = LassoSurfaceVariant,
                                disabledContainerColor = LassoSurfaceVariant,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                cursorColor = LassoPrimary,
                                focusedTextColor = LassoTextPrimary,
                                unfocusedTextColor = LassoTextPrimary,
                            ),
                        shape = RoundedCornerShape(20.dp),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Categoría Field
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Categoría",
                        style =
                            MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = LassoTextPrimary,
                                fontSize = 14.sp,
                            ),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier =
                            Modifier.fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .border(1.5.dp, LassoPrimary, RoundedCornerShape(20.dp))
                                .clickable { showCategoryPicker = true }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = selectedCategory?.name ?: "Seleccionar categoría",
                                modifier = Modifier.weight(1f),
                                style =
                                    MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = LassoTextPrimary,
                                        fontSize = 14.sp,
                                    ),
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = LassoTextMuted,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Comisión especial (%)",
                        style =
                            MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = LassoTextPrimary,
                                fontSize = 14.sp,
                            ),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = commissionPercentage ?: "",
                        onValueChange = { commissionPercentage = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                            TextFieldDefaults.colors(
                                focusedContainerColor = LassoSurfaceVariant,
                                unfocusedContainerColor = LassoSurfaceVariant,
                                disabledContainerColor = LassoSurfaceVariant,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                cursorColor = LassoPrimary,
                                focusedTextColor = LassoTextPrimary,
                                unfocusedTextColor = LassoTextPrimary,
                            ),
                        shape = RoundedCornerShape(20.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                    )
                    Text(
                        text =
                            "Opcional. Reemplaza la comisión del empleado solo para este artículo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = LassoTextMuted,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }
    }

    if (showCategoryPicker) {
        ProductCategoryPickerDialog(
            isVisible = showCategoryPicker,
            categories = categories,
            onDismiss = { showCategoryPicker = false },
            onSelect = {
                selectedCategory = it
                showCategoryPicker = false
            },
            onNew = {
                // Handle new category if needed
            },
        )
    }
}

@Composable
private fun TypeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (isSelected) LassoPrimary.copy(alpha = 0.1f) else Color.White
    val borderColor = if (isSelected) LassoPrimary else LassoDivider
    val textColor = LassoTextPrimary

    Box(
        modifier =
            modifier
                .heightIn(min = 50.dp)
                .background(backgroundColor, RoundedCornerShape(20.dp))
                .border(1.5.dp, borderColor, RoundedCornerShape(20.dp))
                .clickable(onClick = onClick)
                .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier =
                    Modifier.size(20.dp)
                        .background(if (isSelected) LassoPrimary else Color.White, CircleShape)
                        .border(
                            1.5.dp,
                            if (isSelected) LassoPrimary else LassoDivider,
                            CircleShape,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style =
                    MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = textColor,
                        fontSize = 16.sp,
                    ),
            )
        }
    }
}
