package com.lasso.lassoapp.screens.pos.v2.search_bar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.model.ProductCategory
import com.lasso.lassoapp.screens.utils.AdaptiveActionRow
import com.lasso.lassoapp.screens.utils.labelWidth
import com.lasso.lassoapp.ui.theme.LassoTextPlaceholder

@Composable
fun PosSearchFilterBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    categories: List<ProductCategory> = emptyList(),
    catalogFilter: ProductCategory? = null,
    onCatalogFilterChange: (ProductCategory?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val shape = RoundedCornerShape(20.dp)
    val searchStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
    val filterStyle =
        MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, fontWeight = FontWeight(500))
    val filterLabel = catalogFilter?.name ?: "Todas"
    val textHeight =
        rememberTextMeasurer().measure("Buscar...", searchStyle, softWrap = false).size.height
    val controlHeight = maxOf(36.dp, with(LocalDensity.current) { textHeight.toDp() } + 12.dp)
    val searchMinWidth = maxOf(160.dp, labelWidth("Buscar...", searchStyle) + 56.dp)
    val filterMinWidth = labelWidth(filterLabel, filterStyle) + 48.dp
    AdaptiveActionRow(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
        minFieldWidth = searchMinWidth,
        secondMinFieldWidth = filterMinWidth,
        spacing = 8.dp,
        verticalAlignment = Alignment.CenterVertically,
        first = { fieldModifier ->
            BasicTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions =
                    KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                    ),
                textStyle = searchStyle.copy(color = MaterialTheme.colorScheme.onSurface),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier =
                    fieldModifier
                        .heightIn(min = controlHeight)
                        .background(MaterialTheme.colorScheme.surface, shape)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                decorationBox = { innerTextField ->
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = LassoTextPlaceholder,
                            modifier = Modifier.size(24.dp),
                        )
                        Box(Modifier.weight(1f).padding(start = 8.dp)) {
                            if (searchQuery.isEmpty())
                                Text("Buscar...", style = searchStyle, color = LassoTextPlaceholder)
                            innerTextField()
                        }
                    }
                },
            )
        },
        second = { fieldModifier ->
            Box(fieldModifier) {
                OutlinedButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.fillMaxWidth().heightIn(min = controlHeight),
                    shape = shape,
                    border = null,
                    colors =
                        ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text(filterLabel, style = filterStyle, modifier = Modifier.weight(1f))
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
                DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                    DropdownMenuItem(
                        text = { Text("Todas") },
                        onClick = {
                            onCatalogFilterChange(null)
                            menuExpanded = false
                        },
                    )
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                onCatalogFilterChange(category)
                                menuExpanded = false
                            },
                        )
                    }
                }
            }
        },
    )
}
