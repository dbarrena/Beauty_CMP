package com.beauty.beautyapp

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.beauty.beautyapp.model.Product
import com.beauty.beautyapp.screens.configuration.ConfigurationScreen
import com.beauty.beautyapp.screens.detail.DetailScreen
import com.beauty.beautyapp.screens.list.ListScreen
import com.beauty.beautyapp.screens.pos.PosScreen
import com.beauty.beautyapp.screens.product.ProductScreen
import com.beauty.beautyapp.screens.sales.SalesScreen
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

@Serializable
object ProductDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    MaterialTheme(
        colorScheme = lightColorScheme()
    ) {
        val navController: NavHostController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        val showBottomNav = isRootDestination(currentDestination)
        val showMainTopBar = isRootDestination(currentDestination)
        val showSecondaryTopBar = !showMainTopBar

        // Define a lambda for back navigation, assign default for root destinations
        var onBackNavigation: () -> Unit = { navController.popBackStack() }

        val titleText: String = when (currentDestination?.route) {
            HomeDestination::class.qualifiedName -> "Inicio"
            PosDestination::class.qualifiedName -> "Punto de Venta"
            SalesDestination::class.qualifiedName -> "Ventas"
            ConfigurationDestination::class.qualifiedName -> "Configuración"
            ProductDestination::class.qualifiedName -> "Producto"
            DetailDestination::class.qualifiedName -> "Detalle"
            else -> ""
        }

        Scaffold(
            topBar = {
                //AnimatedVisibility(visible = showMainTopBar) {
                //if(showMainTopBar) {
                Surface(shadowElevation = 3.dp) {
                    TopAppBar(
                        title = { Text(titleText) },
                        navigationIcon = {
                            if (!showMainTopBar) {
                                IconButton(onClick = onBackNavigation) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            }
                        }
                    )
                }
                //} else null
                //}
            },
            bottomBar = {
                //AnimatedVisibility(visible = showBottomNav) {
                if (showBottomNav) {
                    NavigationBar(
                        modifier = Modifier.height(100.dp),
                    ) {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Home, contentDescription = null) },
                            //label = { Text("Inicio") },
                            selected = currentDestination?.hierarchy?.any { it.route == HomeDestination::class.qualifiedName } == true,
                            onClick = {
                                navController.navigate(HomeDestination)
                            }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                            //label = { Text("POS") },
                            selected = currentDestination?.hierarchy?.any { it.route == PosDestination::class.qualifiedName } == true,
                            onClick = {
                                navController.navigate(PosDestination)
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
                            }
                        )
                    }
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
                    val product = navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.get<Product>("new_product")

                    println("Producto: $product")

                    PosScreen(product) {
                        navController.navigate(ProductDestination)
                    }
                }
                composable<SalesDestination> {
                    SalesScreen()
                }
                composable<ConfigurationDestination> {
                    ConfigurationScreen()
                }
                composable<ProductDestination> { backStackEntry ->
                    val editProduct = navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.get<Product>("editing_product")

                    ProductScreen(product = editProduct) { product ->
                        onBackNavigation = {
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("new_product", product)
                            navController.popBackStack()
                        }
                    }
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
