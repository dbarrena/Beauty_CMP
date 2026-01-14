package com.lasso.lassoapp.screens.sales.detail.edit_date

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import androidx.compose.material3.rememberDatePickerState
import com.lasso.lassoapp.screens.utils.formatDdMmYyyy
import com.lasso.lassoapp.screens.utils.formatDdMmYyyyHhMm
import com.lasso.lassoapp.screens.utils.toEpochMillis
import com.lasso.lassoapp.screens.utils.toEpochMillisToLocalDateTime
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleDateEditDialogScreen(
    saleDate: LocalDate,
    modifier: Modifier = Modifier,
    onDateSelected: (Long) -> Unit = {},
    onDismissRequest: () -> Unit = {}
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedDate by remember(saleDate) { mutableStateOf(saleDate) }

    OutlinedButton(onClick = { showDialog = true }, modifier = modifier) {
        Text(selectedDate.formatDdMmYyyy())
    }

    if (showDialog) {
        val initialDateUtcMidnightMillis = LocalDateTime(
            saleDate.year,
            saleDate.monthNumber,
            saleDate.dayOfMonth,
            0,
            0
        ).toInstant(TimeZone.UTC).toEpochMilliseconds()

        // DatePicker state initialized to the chosen date
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis =initialDateUtcMidnightMillis
        )

        LaunchedEffect(datePickerState.selectedDateMillis) {
            datePickerState.selectedDateMillis?.let { millis ->
                val pickedDateUtc = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC).date
                // Preserve the time (hour/minute) from the previous selectedDate to avoid shifting timezones.
                selectedDate = pickedDateUtc
            }
        }

        DatePickerDialog(
            onDismissRequest = {
                showDialog = false
                onDismissRequest()
            },
            confirmButton = {
                TextButton(onClick = {
                    // Return selected date
                    val resultMillis = selectedDate.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                    onDateSelected(resultMillis)
                    showDialog = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    onDismissRequest()
                }) {
                    Text("Cancel")
                }
            },
            content = {
                DatePicker(
                    state = datePickerState,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
    }
}