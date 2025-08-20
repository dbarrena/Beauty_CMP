package com.beauty.beautyapp.screens.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random

@Composable
fun DropdownMenuPaymentType(onSelectionChange: (String) -> Unit) {
    val options = listOf("Tarjeta", "Efectivo", "Transferencia")
    val expanded = remember { mutableStateOf(false) }
    val selectedOptionText = remember { mutableStateOf(options[0]) }

    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .clickable { expanded.value = !expanded.value }
    ) {
        OutlinedTextField(
            value = selectedOptionText.value,
            onValueChange = {},
            readOnly = true,
            label = {
                Text(
                    text = "Tipo de pago",
                    style = MaterialTheme.typography.titleSmall
                )
            },
            enabled = false,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = MaterialTheme.colorScheme.primary,
                disabledLabelColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth()
        )

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = {
                        Text(text = selectionOption)
                    },
                    onClick = {
                        selectedOptionText.value = selectionOption
                        expanded.value = false
                        onSelectionChange(
                            when (selectionOption) {
                                "Tarjeta" -> "card"
                                "Efectivo" -> "cash"
                                "Transferencia" -> "transfer"
                                else -> "card"
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun FullScreenLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun StylizedTextField(
    label: String,
    body: String = "",
    readOnly: Boolean = true,
    enabled: Boolean = false,
    onValueChange: (String) -> Unit = {},
    onClick: () -> Unit = {},
    keyboardActions: KeyboardActions? = null,
    keyboardOptions: KeyboardOptions? = null
) {
    OutlinedTextField(
        value = body,
        onValueChange = onValueChange,
        readOnly = readOnly,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall
            )
        },
        enabled = enabled,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
        colors = OutlinedTextFieldDefaults.colors(
            disabledBorderColor = MaterialTheme.colorScheme.primary,
            disabledLabelColor = MaterialTheme.colorScheme.primary
        ),
        keyboardActions = keyboardActions ?: KeyboardActions.Default,
        keyboardOptions = keyboardOptions ?: KeyboardOptions.Default,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    )
}

@Composable
fun SearchBar(
    searchText: String,
    keyboardController: SoftwareKeyboardController?,
    onSearch: (query: String) -> Unit
) {
    OutlinedTextField(
        value = searchText,
        onValueChange = {
            onSearch(it)
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        placeholder = { Text("Buscar...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search Icon"
            )
        },
        shape = RoundedCornerShape(12.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentMonthDateRangePicker(
    onRangeSelected: (startEpoch: Long?, endEpoch: Long?) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    // Get current month start and end in epoch millis
    val timeZone = TimeZone.currentSystemDefault()
    val today = Clock.System.now().toLocalDateTime(timeZone).date
    val firstDay = LocalDate(today.year, today.monthNumber, 1)
    val lastDay = firstDay.plus(DatePeriod(months = 1)).minus(DatePeriod(days = 1))

    val firstDayMillis = firstDay.atStartOfDayIn(timeZone).toEpochMilliseconds()
    val lastDayMillis = lastDay.atStartOfDayIn(timeZone).toEpochMilliseconds()

    val state = rememberDateRangePickerState(
        initialSelectedStartDateMillis = firstDayMillis,
        initialSelectedEndDateMillis = lastDayMillis
    )

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        println("Rango de fechas start ${state.selectedStartDateMillis} end ${state.selectedEndDateMillis}")
        StylizedTextField(
            label = "Rango de fechas",
            body = "${state.selectedStartDateMillis?.toPickerDateString()} - ${state.selectedEndDateMillis?.toPickerDateString()}",
            onClick = {
                showDialog = true
            }
        )

        if (showDialog) {
            DatePickerDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onRangeSelected(
                                state.selectedStartDateMillis,
                                state.selectedEndDateMillis
                            )
                            showDialog = false
                        }
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
                }
            ) {
                DateRangePicker(
                    state = state,
                    title = { Text("Selecciona el rango de fechas") },
                    dateFormatter = DatePickerDefaults.dateFormatter(),
                    showModeToggle = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                    //.padding(8.dp)
                )
            }
        }
    }
}

/**
 * A composable card with elevation, a decorative colored vertical line, title, description and optional icon.
 *
 * @param title The title of the card (not null).
 * @param description The description of the card (not null).
 * @param icon Optional icon to display at the end of the row.
 * @param color A color for the decorative line; defaults to a random color.
 */
@Composable
fun ElevatedInfoCard(
    title: String,
    description: String,
    icon: ImageVector? = null,
    color: Color = Color(Random.nextFloat(), Random.nextFloat(), Random.nextFloat(), 1f)
) {
    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(48.dp)
                    .background(
                        color = color,
                        shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                    )
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
            }
            if (icon != null) {
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}