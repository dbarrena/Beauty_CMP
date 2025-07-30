package com.beauty.beautyapp.screens.configuration

import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.collectAsState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ConfigurationScreen() {
    val viewModel = koinViewModel<ConfigurationViewModel>()
    ConfigurationScreenContent(viewModel)
}

@Composable
fun ConfigurationScreenContent(viewModel: ConfigurationViewModel) {
    val state = viewModel.state.collectAsState()

    val options = listOf(
        "View Products",
        "View Services",
        "View Sales"
    )
    Column(modifier = Modifier.padding(16.dp)) {
        state.value.session?.let {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { /* TODO: handle click for $option */ },
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Text(
                    text = "partnerID ${it.partnerId}",
                    modifier = Modifier.padding(24.dp),
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "employee name:  ${it.employeeName}",
                    modifier = Modifier.padding(24.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        } ?: run {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { viewModel.getSessionByEmployeeId(3) },
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Text(
                    text = "Set Partner ID",
                    modifier = Modifier.padding(24.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }



        options.forEach { option ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { /* TODO: handle click for $option */ },
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Text(
                    text = option,
                    modifier = Modifier.padding(24.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}