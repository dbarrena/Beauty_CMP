package com.lasso.lassoapp.screens.home.v2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.ui.theme.LassoTextMuted

@Composable
fun HomeAgendaSection(
    onVerTodo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Agenda de hoy",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            TextButton(onClick = onVerTodo) {
                Text(
                    text = "Ver todo",
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Text(
                text = "No hay citas para hoy",
                style = MaterialTheme.typography.bodyMedium,
                color = LassoTextMuted,
                modifier = Modifier.padding(20.dp),
            )
        }
    }
}
