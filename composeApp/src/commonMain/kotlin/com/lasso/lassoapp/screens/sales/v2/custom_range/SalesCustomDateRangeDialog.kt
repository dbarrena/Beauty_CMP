package com.lasso.lassoapp.screens.sales.v2.custom_range

import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun SalesCustomDateRangeDialog(
    onDismiss: () -> Unit,
    onConfirm: (Long?, Long?) -> Unit,
) {
    val timeZone = TimeZone.currentSystemDefault()
    val today = Clock.System.now().toLocalDateTime(timeZone).date
    val firstDay = LocalDate(today.year, today.monthNumber, 1)
    val lastDay = firstDay.plus(DatePeriod(months = 1)).minus(DatePeriod(days = 1))
    val firstDayMillis = firstDay.atStartOfDayIn(timeZone).toEpochMilliseconds()
    val lastDayMillis = lastDay.atStartOfDayIn(timeZone).toEpochMilliseconds()

    val state = rememberDateRangePickerState(
        initialSelectedStartDateMillis = firstDayMillis,
        initialSelectedEndDateMillis = lastDayMillis,
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(state.selectedStartDateMillis, state.selectedEndDateMillis)
                },
            ) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
        colors = DatePickerDefaults.colors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
        ),
    ) {
        DateRangePicker(
            state = state,
            title = {},
            dateFormatter = DatePickerDefaults.dateFormatter(),
            showModeToggle = false,
            colors = DatePickerDefaults.colors(
                containerColor = Color.White,
                selectedDayContentColor = Color.White,
                todayDateBorderColor = Color.Gray,
                todayContentColor = Color.Black,
                dayInSelectionRangeContentColor = Color.Black,
            ),
        )
    }
}
