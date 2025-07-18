package com.jetbrains.kmpapp

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.jetbrains.kmpapp.screens.configuration.ConfigurationScreen
import com.jetbrains.kmpapp.screens.detail.DetailScreen
import com.jetbrains.kmpapp.screens.list.ListScreen
import com.jetbrains.kmpapp.screens.pos.PosScreen
import kotlinx.serialization.Serializable

@Serializable
object HomeDestination

@Serializable
object PosDestination

@Serializable
object SalesDestination

@Serializable
object ConfigurationDestination

@Serializable
data class DetailDestination(val objectId: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    MaterialTheme(
        colorScheme = lightColorScheme()
    ) {
        val navController: NavHostController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val title = remember { mutableStateOf("Inicio") }

        val showBottomNav = isRootDestination(currentDestination)
        val showMainTopBar = isRootDestination(currentDestination)
        val showSecondaryTopBar = !showMainTopBar

        Scaffold(
            topBar = {
                Surface(shadowElevation = 3.dp) {
                    TopAppBar(title = { Text(title.value) })
                }
            },
            bottomBar = {
                NavigationBar(
                    modifier = Modifier.height(100.dp),
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        //label = { Text("Inicio") },
                        selected = currentDestination?.hierarchy?.any { it.route == HomeDestination::class.qualifiedName } == true,
                        onClick = {
                            navController.navigate(HomeDestination)
                            title.value = "Inicio"
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                        //label = { Text("POS") },
                        selected = currentDestination?.hierarchy?.any { it.route == PosDestination::class.qualifiedName } == true,
                        onClick = {
                            navController.navigate(PosDestination)
                            title.value = "Punto de Venta"
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
                        //label = { Text("Ventas") },
                        selected = currentDestination?.hierarchy?.any { it.route == SalesDestination::class.qualifiedName } == true,
                        onClick = { navController.navigate(SalesDestination) }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                        //label = { Text("Configuracion") },
                        selected = currentDestination?.hierarchy?.any { it.route == ConfigurationDestination::class.qualifiedName } == true,
                        onClick = {
                            navController.navigate(ConfigurationDestination)
                            title.value = "Configuración"
                        }
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = PosDestination,
                modifier = Modifier.padding(innerPadding),
                enterTransition = {
                    EnterTransition.None
                },
                exitTransition = {
                    ExitTransition.None
                }
            ) {
                composable<HomeDestination> {
                    ListScreen(navigateToDetails = { objectId ->
                        navController.navigate(DetailDestination(objectId))
                    })
                }
                composable<DetailDestination> { backStackEntry ->
                    DetailScreen(
                        objectId = backStackEntry.toRoute<DetailDestination>().objectId,
                        navigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
                composable<PosDestination> {
                    PosScreen()
                }
                composable<SalesDestination> {
                    Text("Sales Screen - Coming Soon")
                }
                composable<ConfigurationDestination> {
                    ConfigurationScreen()
                }
            }
        }
    }
}

fun isRootDestination(destination: NavDestination?): Boolean {
    val route = destination?.route
    return route == HomeDestination::class.qualifiedName ||
            route == PosDestination::class.qualifiedName ||
            route == SalesDestination::class.qualifiedName ||
            route == ConfigurationDestination::class.qualifiedName
}
