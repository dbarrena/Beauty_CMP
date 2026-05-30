package com.lasso.lassoapp.screens.employees

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.screens.employees.dialog.edit.EditEmployeeDialog
import com.lasso.lassoapp.screens.employees.dialog.new.NewEmployeeDialog
import com.lasso.lassoapp.screens.utils.FullScreenLoading
import com.lasso.lassoapp.ui.theme.LassoPrimary
import com.lasso.lassoapp.ui.theme.LassoTextMuted
import com.lasso.lassoapp.ui.theme.LassoTextPrimary
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EmployeesScreen() {
    val viewModel = koinViewModel<EmployeesViewModel>()
    val state by viewModel.state.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (state.isLoading) {
            FullScreenLoading()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Text(
                    text = "Empleados",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = LassoTextPrimary
                )
                Text(
                    text = "Administra tus empleados",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LassoTextMuted
                )

                Spacer(modifier = Modifier.height(24.dp))

                // New Employee Button
                Button(
                    onClick = { viewModel.showNewEmployeeDialog() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LassoPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Nuevo",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Employees List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(state.employees) { employee ->
                        EmployeeItem(
                            employee = employee,
                            onEditClick = { viewModel.showEditEmployeeDialog(it) }
                        )
                    }
                }
            }
        }
    }

    if (state.isNewEmployeeDialogDisplayed) {
        NewEmployeeDialog(
            onDismiss = { viewModel.hideNewEmployeeDialog() },
            onResult = { viewModel.onEmployeeSaved() }
        )
    }

    if (state.isEditEmployeeDialogDisplayed) {
        val employee = state.selectedEmployee
        if (employee != null) {
            EditEmployeeDialog(
                employee = employee,
                onDismiss = { viewModel.hideEditEmployeeDialog() },
                onResult = { viewModel.onEmployeeSaved() }
            )
        }
    }
}
