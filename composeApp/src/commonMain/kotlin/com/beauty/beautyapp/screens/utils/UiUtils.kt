package com.beauty.beautyapp.screens.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun DropdownMenuPaymentType(onSelectionChange: (String) -> Unit) {
    val options = listOf("Tarjeta", "Efectivo")
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
                        onSelectionChange(if (selectionOption == "Tarjeta") "card" else "cash")
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
    onValueChange: (String) -> Unit = {}
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
        modifier = Modifier.fillMaxWidth()
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