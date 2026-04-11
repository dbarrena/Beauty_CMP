package com.lasso.lassoapp.screens.home.v2

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun HomeHeaderTitle(
    partnerName: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = partnerName.ifBlank { "Mi negocio" },
        modifier = modifier.fillMaxWidth().padding(bottom = 4.dp),
        style =
            MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            ),
    )
}
