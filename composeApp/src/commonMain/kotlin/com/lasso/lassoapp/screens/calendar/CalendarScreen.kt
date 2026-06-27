package com.lasso.lassoapp.screens.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.model.Employee
import com.lasso.lassoapp.utils.formatFullDateSpanish
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CalendarScreen(modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<CalendarScreenViewModel>()
    val state by viewModel.state.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF8F8FA)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            CalendarHeader(
                modifier = Modifier.padding(vertical = 16.dp),
                onAddClick = { /* TODO: Implement add appointment */ }
            )

            // Date Selector
            CalendarDateSelector(
                modifier = Modifier,
                selectedDateFormatted = state.selectedDate.formatFullDateSpanish(),
                onPreviousDay = { viewModel.onPreviousDay() },
                onNextDay = { viewModel.onNextDay() },
                onDatePickerClick = { showDatePicker = true }
            )

            if (state.employees.isEmpty() && !state.isLoading) {
                CalendarEmptyState()
            } else {
                val verticalScrollState = rememberScrollState()
                val pagerState = rememberPagerState(pageCount = { state.employees.size })
                val hourHeight = 90.dp
                val sidebarWidth = 60.dp
                val headerHeight = 40.dp
                
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp)
                ) {
                    val availableWidth = maxWidth
                    val pagerWidth = availableWidth - sidebarWidth - 16.dp
                    val columnWidth = if (state.employees.size <= 1) {
                        pagerWidth
                    } else {
                        pagerWidth * 0.85f
                    }

                    Row(modifier = Modifier.fillMaxSize()) {
                        // Sidebar (Fixed horizontally, scrolls vertically with grid)
                        Column(modifier = Modifier.width(sidebarWidth + 16.dp)) {
                            Spacer(modifier = Modifier.height(headerHeight))
                            CalendarHourSidebar(
                                hourHeight = hourHeight,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp)
                                    .verticalScroll(verticalScrollState)
                            )
                        }

                        // Employee Columns (Snappy horizontal scroll)
                        HorizontalPager(
                            state = pagerState,
                            pageSize = PageSize.Fixed(pagerWidth),
                            modifier = Modifier.weight(1f),
                            beyondViewportPageCount = 1,
                            key = { state.employees[it].id },
                            contentPadding = PaddingValues(end = 16.dp)
                        ) { page ->
                            val employee = state.employees[page]
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(end = 8.dp)
                            ) {
                                EmployeeHeader(
                                    employee = employee,
                                    modifier = Modifier.height(headerHeight).fillMaxWidth()
                                )
                                CalendarEvents(
                                    hourHeight = hourHeight,
                                    verticalScrollState = verticalScrollState,
                                    events = state.events.filter { it.employeeId == employee.id },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        CalendarPickerDialog(
            initialDate = state.selectedDate,
            onDismiss = { showDatePicker = false },
            onConfirm = { date ->
                viewModel.onDateSelected(date)
                showDatePicker = false
            }
        )
    }
}

@Composable
private fun EmployeeHeader(
    employee: Employee,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = employee.name,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF353D3C)
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CalendarHeader(
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Citas",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF353D3C)
                )
            )
            Text(
                text = "Gestiona tu agenda",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF9CA3AF)
                )
            )
        }

        FloatingActionButton(
            onClick = onAddClick,
            containerColor = Color(0xFF00D1AD),
            contentColor = Color.White,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Agregar cita"
            )
        }
    }
}

@Composable
private fun CalendarDateSelector(
    modifier: Modifier = Modifier,
    selectedDateFormatted: String,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onDatePickerClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onPreviousDay) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Day", tint = Color(0xFF353D3C))
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onDatePickerClick),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Fecha seleccionada",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF9CA3AF),
                        fontSize = 12.sp
                    )
                )
                Text(
                    text = selectedDateFormatted,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF353D3C),
                        fontSize = 16.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }

            IconButton(onClick = onNextDay) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Next Day", tint = Color(0xFF353D3C))
            }
        }
    }
}

@Composable
private fun CalendarEmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color(0xFFE5E7EB)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No hay citas para este día",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFF9CA3AF)
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}
