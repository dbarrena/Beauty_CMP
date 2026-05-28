package com.lasso.lassoapp.screens.configuration

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.ui.theme.LassoPrimary
import com.lasso.lassoapp.ui.theme.LassoSecondary
import com.lasso.lassoapp.ui.theme.LassoTertiary
import com.lasso.lassoapp.ui.theme.LassoTextMuted
import com.lasso.lassoapp.ui.theme.LassoTextPrimary
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ConfigurationScreen(
    onLoggedOut: () -> Unit,
    onConfigurationNavigation: (configurationScreenRoutes: ConfigurationScreenRoutes) -> Unit
) {
    val viewModel = koinViewModel<ConfigurationViewModel>()
    val state = viewModel.state.collectAsState()

    LaunchedEffect(state.value.logoutEventCompleted) {
        if (state.value.logoutEventCompleted) {
            onLoggedOut()
        }
    }

    ConfigurationScreenContent(
        onLogout = { viewModel.logout() },
        onConfigurationNavigation = onConfigurationNavigation
    )
}

@Composable
fun ConfigurationScreenContent(
    onLogout: () -> Unit,
    onConfigurationNavigation: (ConfigurationScreenRoutes) -> Unit
) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "Más opciones",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = LassoTextPrimary
            )
            Text(
                text = "Gestiona tu cuenta y configuración",
                style = MaterialTheme.typography.bodyMedium,
                color = LassoTextMuted
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Menu Options
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ConfigurationTile(
                    title = "Reportes",
                    subtitle = "Estadísticas y gráficas de tu negocio",
                    icon = Icons.Default.BarChart,
                    containerColor = LassoPrimary.copy(alpha = 0.1f),
                    iconTint = LassoPrimary,
                    onClick = { /* No-op */ }
                )

                ConfigurationTile(
                    title = "Historial de ventas",
                    subtitle = "Consulta y gestiona tus ventas",
                    icon = Icons.Default.History,
                    containerColor = LassoSecondary.copy(alpha = 0.1f),
                    iconTint = LassoSecondary,
                    onClick = { /* No-op */ }
                )

                ConfigurationTile(
                    title = "Caja",
                    subtitle = "Cortes de caja y resumen",
                    icon = Icons.Default.AccountBalanceWallet,
                    containerColor = LassoTertiary.copy(alpha = 0.1f),
                    iconTint = LassoTertiary,
                    onClick = { onConfigurationNavigation(ConfigurationScreenRoutes.CASH_CLOSURE) }
                )

                ConfigurationTile(
                    title = "Mi perfil",
                    subtitle = "Datos personales y contraseña",
                    icon = Icons.Default.Person,
                    containerColor = LassoPrimary.copy(alpha = 0.1f),
                    iconTint = LassoPrimary,
                    onClick = { /* No-op */ }
                )

                ConfigurationTile(
                    title = "Notificaciones",
                    subtitle = "Citas y recordatorios",
                    icon = Icons.Default.Notifications,
                    containerColor = LassoSecondary.copy(alpha = 0.1f),
                    iconTint = LassoSecondary,
                    badgeCount = 2,
                    onClick = { /* No-op */ }
                )

                ConfigurationTile(
                    title = "Ventas por Categoría",
                    subtitle = "Reporte detallado por categorías",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    containerColor = LassoPrimary.copy(alpha = 0.1f),
                    iconTint = LassoPrimary,
                    onClick = { onConfigurationNavigation(ConfigurationScreenRoutes.SALES_BY_PRODUCT_CATEGORIES) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Logout Button
                LogoutTile(
                    onClick = onLogout
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Footer
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "LASSO v1.0.0",
                    style = MaterialTheme.typography.bodySmall,
                    color = LassoTextMuted
                )
                Text(
                    text = "© 2026 Todos los derechos reservados",
                    style = MaterialTheme.typography.bodySmall,
                    color = LassoTextMuted
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ConfigurationTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color,
    iconTint: Color,
    onClick: () -> Unit,
    badgeCount: Int? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(containerColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(28.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    ),
                    color = LassoTextPrimary
                )

                if (badgeCount != null) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(100.dp))
                            .background(LassoTertiary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = badgeCount.toString(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        )
                    }
                }
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = LassoTextMuted
            )
        }
    }
}

@Composable
fun LogoutTile(
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(LassoTertiary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = null,
                tint = LassoTertiary,
                modifier = Modifier.size(28.dp)
            )
        }

        Column {
            Text(
                text = "Cerrar sesión",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                ),
                color = LassoTertiary
            )
            Text(
                text = "Salir de tu cuenta",
                style = MaterialTheme.typography.bodySmall,
                color = LassoTertiary.copy(alpha = 0.7f)
            )
        }
    }
}

enum class ConfigurationScreenRoutes {
    EDIT_PRODUCTS_SERVICES,
    CASH_CLOSURE,
    CASH_CLOSURE_RECORDS,
    PRODUCT_CATEGORIES,
    SALES_BY_PRODUCT_CATEGORIES
}
