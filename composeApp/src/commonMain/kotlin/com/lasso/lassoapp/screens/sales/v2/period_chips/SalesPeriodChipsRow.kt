package com.lasso.lassoapp.screens.sales.v2.period_chips

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.screens.sales.v2.SalesPeriodFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesPeriodChipsRow(
    selected: SalesPeriodFilter,
    onSelect: (SalesPeriodFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    val chips = listOf(
        SalesPeriodFilter.Today to "Hoy",
        SalesPeriodFilter.ThisWeek to "Esta semana",
        SalesPeriodFilter.ThisMonth to "Este mes",
        SalesPeriodFilter.Custom to "Personalizado",
    )
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(chips) { (filter, label) ->
            val isSelected = selected == filter
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(filter) },
                label = { Text(label, maxLines = 1) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = MaterialTheme.colorScheme.onSurface,
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                ),
                shape = CircleShape,
            )
        }
    }
}
