package com.jetbrains.kmpapp.screens.configuration

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable

@Composable
fun ConfigurationScreen() {
    ConfigurationScreenContent()
}

@Composable
fun ConfigurationScreenContent() {
    val options = listOf(
        "Set Partner ID",
        "View Products",
        "View Services",
        "View Sales"
    )
    Column(modifier = Modifier.padding(16.dp)) {
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