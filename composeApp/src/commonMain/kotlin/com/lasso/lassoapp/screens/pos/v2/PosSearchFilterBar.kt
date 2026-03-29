package com.lasso.lassoapp.screens.pos.v2

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.screens.pos.PosCatalogFilter
import com.lasso.lassoapp.screens.pos.label
import com.lasso.lassoapp.ui.theme.LassoTextPlaceholder

@Composable
fun PosSearchFilterBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    catalogFilter: PosCatalogFilter = PosCatalogFilter.ALL,
    onCatalogFilterChange: (PosCatalogFilter) -> Unit,
    onNuevoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val menuExpanded = remember { mutableStateOf(false) }
    val showNuevoButton = false
    val controlHeight = 35.dp
    val shape = RoundedCornerShape(20.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val isSearchFocused by interactionSource.collectIsFocusedAsState()
    val searchBorderColor = if (isSearchFocused) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .width(200.dp)
                .height(controlHeight)
                /*.border(
                    width = 1.dp,
                    color = searchBorderColor,
                    shape = shape
                )*/
                .background(MaterialTheme.colorScheme.surface, shape)
                .padding(horizontal = 12.dp),
            interactionSource = interactionSource,
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = LassoTextPlaceholder,
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Buscar...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight(400),
                                ),
                                color = LassoTextPlaceholder
                            )
                        }
                        innerTextField()
                    }
                }
            }
        )
        Box(modifier = Modifier.weight(1f)) {
            OutlinedButton(
                onClick = { menuExpanded.value = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(controlHeight),
                shape = shape,
                border = null,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
            ) {
                Text(
                    text = catalogFilter.label(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight(500),
                    ),
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(
                expanded = menuExpanded.value,
                onDismissRequest = { menuExpanded.value = false },
            ) {
                PosCatalogFilter.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.label()) },
                        onClick = {
                            onCatalogFilterChange(option)
                            menuExpanded.value = false
                        },
                    )
                }
            }
        }
        if (showNuevoButton) {
            TextButton(onClick = onNuevoClick) {
                Text(
                    text = "Nuevo",
                    color = MaterialTheme.colorScheme.tertiary,
                )
            }
        }
    }
}