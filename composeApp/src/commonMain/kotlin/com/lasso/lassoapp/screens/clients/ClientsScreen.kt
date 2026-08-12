package com.lasso.lassoapp.screens.clients

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.model.Client
import com.lasso.lassoapp.screens.clients.dialog.ClientDialog
import com.lasso.lassoapp.screens.utils.FullScreenLoading
import com.lasso.lassoapp.ui.theme.LassoPrimary
import com.lasso.lassoapp.ui.theme.LassoTextMuted
import com.lasso.lassoapp.ui.theme.LassoTextPrimary
import lassoapp.composeapp.generated.resources.Res
import lassoapp.composeapp.generated.resources.pencil_icon
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ClientsScreen() {
    val viewModel = koinViewModel<ClientsViewModel>()
    val state by viewModel.state.collectAsState()

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        if (state.isLoading && state.clients.isEmpty()) {
            FullScreenLoading()
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text(
                    text = "Clientes",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                    ),
                    color = LassoTextPrimary,
                )
                Text(
                    text = "Administra tus clientes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LassoTextMuted,
                )
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = viewModel::showCreateDialog,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LassoPrimary,
                        contentColor = Color.White,
                    ),
                    shape = RoundedCornerShape(20.dp),
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Nuevo", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    placeholder = { Text("Buscar...") },
                )
                Spacer(Modifier.height(16.dp))

                when {
                    state.loadError != null && state.clients.isEmpty() -> ClientsMessage(
                        message = state.loadError ?: "No se pudieron cargar los clientes",
                        action = {
                            IconButton(onClick = viewModel::loadClients) {
                                Icon(Icons.Default.Refresh, contentDescription = "Reintentar")
                            }
                        },
                    )
                    state.filteredClients.isEmpty() -> ClientsMessage(
                        message = if (state.searchQuery.isBlank()) {
                            "Todavía no hay clientes registrados"
                        } else {
                            "No encontramos clientes con esa búsqueda"
                        },
                    )
                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp),
                    ) {
                        items(state.filteredClients, key = { it.id }) { client ->
                            ClientItem(client = client, onEditClick = viewModel::showEditDialog)
                        }
                    }
                }
            }
        }
    }

    if (state.isDialogDisplayed) {
        ClientDialog(
            client = state.selectedClient,
            isLoading = state.isSaving,
            error = state.saveError,
            onDismiss = viewModel::hideDialog,
            onSave = viewModel::saveClient,
        )
    }
}

@Composable
private fun ClientsMessage(message: String, action: (@Composable () -> Unit)? = null) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(message, color = LassoTextMuted, textAlign = TextAlign.Center)
        action?.invoke()
    }
}

@Composable
private fun ClientItem(client: Client, onEditClick: (Client) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(24.dp)).background(
                    Brush.verticalGradient(listOf(LassoPrimary, Color(0xFF00B999)))
                ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    client.name.firstOrNull()?.uppercase() ?: "",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(client.name, color = LassoTextPrimary, fontWeight = FontWeight.SemiBold)
                client.phone?.let { Text(it, color = LassoTextMuted, fontSize = 14.sp) }
                client.email?.let { Text(it, color = LassoTextMuted, fontSize = 13.sp) }
                client.notes?.takeIf { it.isNotBlank() }?.let {
                    Text(it, color = LassoTextMuted, fontSize = 12.sp, maxLines = 1)
                }
            }
            IconButton(onClick = { onEditClick(client) }) {
                Icon(
                    painter = painterResource(Res.drawable.pencil_icon),
                    contentDescription = "Editar cliente",
                    tint = LassoPrimary,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}

