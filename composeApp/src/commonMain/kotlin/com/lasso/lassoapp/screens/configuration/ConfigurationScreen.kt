package com.lasso.lassoapp.screens.configuration

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.lasso.lassoapp.screens.utils.StylizedTextField
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ConfigurationScreen(
    onLoggedOut: () -> Unit,
    onConfigurationNavigation: (configurationScreenRoutes: ConfigurationScreenRoutes) -> Unit
) {
    val viewModel = koinViewModel<ConfigurationViewModel>()
    ConfigurationScreenContent(viewModel, onLoggedOut, onConfigurationNavigation)
}

@Composable
fun ConfigurationScreenContent(
    viewModel: ConfigurationViewModel,
    onLoggedOut: () -> Unit,
    onConfigurationNavigation: (ConfigurationScreenRoutes) -> Unit
) {
    val state = viewModel.state.collectAsState()

    LaunchedEffect(state.value.logoutEventCompleted) {
        if (state.value.logoutEventCompleted) {
            onLoggedOut()
        }
    }

    state.value.session?.let {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StylizedTextField("Empleado: ", it.employeeName)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onConfigurationNavigation(ConfigurationScreenRoutes.EDIT_PRODUCTS_SERVICES) },
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
            ) {
                Text(
                    text = "Productos y Servicios",
                    modifier = Modifier.padding(24.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onConfigurationNavigation(ConfigurationScreenRoutes.CASH_CLOSURE) },
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
            ) {
                Text(
                    text = "Corte de caja",
                    modifier = Modifier.padding(24.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onConfigurationNavigation(ConfigurationScreenRoutes.CASH_CLOSURE_RECORDS) },
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
            ) {
                Text(
                    text = "Registro cortes de caja",
                    modifier = Modifier.padding(24.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.logout() },
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
            ) {
                Text(
                    text = "Cerrar Sesión",
                    modifier = Modifier.padding(24.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

enum class ConfigurationScreenRoutes {
    EDIT_PRODUCTS_SERVICES,
    CASH_CLOSURE,
    CASH_CLOSURE_RECORDS
}