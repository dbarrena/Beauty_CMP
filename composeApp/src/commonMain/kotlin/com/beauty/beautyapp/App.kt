package com.beauty.beautyapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.beauty.beautyapp.screens.configuration.ConfigurationScreen
import com.beauty.beautyapp.screens.home.HomeScreen
import com.beauty.beautyapp.screens.login.LoginScreen
import com.beauty.beautyapp.screens.pos.PosScreen
import com.beauty.beautyapp.screens.product_service.ProductServiceScreen
import com.beauty.beautyapp.screens.sales.SalesScreen
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

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
object ProductServiceDestination

@Serializable
object LoginDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    MaterialTheme(
        colorScheme = lightColorScheme()
    ) {
        val navController: NavHostController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        val viewModel = koinViewModel<AppViewModel>()
        val state = viewModel.state.collectAsState()

        val showBottomNav = isRootDestination(currentDestination)
        val showMainTopBar = isRootDestination(currentDestination)

        var onBackNavigation: () -> Unit = { navController.popBackStack() }

        val titleText: String = when (currentDestination?.route) {
            HomeDestination::class.qualifiedName -> "Inicio"
            PosDestination::class.qualifiedName -> "Punto de Venta"
            SalesDestination::class.qualifiedName -> "Ventas"
            ConfigurationDestination::class.qualifiedName -> "Configuración"
            ProductServiceDestination::class.qualifiedName -> "Productos y Servicios"
            DetailDestination::class.qualifiedName -> "Detalle"
            else -> ""
        }

        state.value.isLoggedIn?.let { isLoggedIn ->
            println("isLoggedIn: $isLoggedIn")
            val startDestination = if (isLoggedIn) HomeDestination else LoginDestination

            Scaffold(
                topBar = {
                    AnimatedVisibility(visible = isLoggedIn) {
                        Surface(shadowElevation = 3.dp) {
                            TopAppBar(
                                title = {
                                    /*AnimatedContent(
                                        targetState = titleText,
                                        transitionSpec = {
                                            slideIn() togetherWith(fadeOut())
                                        }
                                    ) { targetTitle ->
                                        Text(text = targetTitle)
                                    }*/

                                    Crossfade(targetState = titleText) { targetTitle ->
                                        Text(text = targetTitle)
                                    }
                                },
                                navigationIcon = {
                                    if (!showMainTopBar) {
                                        IconButton(onClick = onBackNavigation) {
                                            Icon(
                                                Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = "Back",
                                                tint = MaterialTheme.colorScheme.onPrimary
                                            )
                                        }
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                            )
                        }
                    }
                },
                bottomBar = {
                    //AnimatedVisibility(visible = showBottomNav) {
                    if (showBottomNav && isLoggedIn) {
                        NavigationBar(
                            modifier = Modifier.height(105.dp),
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
                                icon = { Icon(Icons.Filled.AttachMoney, contentDescription = null) },
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
                    startDestination = startDestination,
                    modifier = Modifier.padding(innerPadding),
                    enterTransition = {
                        EnterTransition.None
                    },
                    exitTransition = {
                        ExitTransition.None
                    }
                ) {
                    composable<HomeDestination> {
                        HomeScreen()
                    }
                    composable<PosDestination> {
                        PosScreen()
                    }
                    composable<SalesDestination> {
                        SalesScreen()
                    }
                    composable<ConfigurationDestination> {
                        ConfigurationScreen {
                            navController.navigate(ProductServiceDestination)
                        }
                    }
                    composable<ProductServiceDestination> {
                        ProductServiceScreen()
                    }
                    composable<LoginDestination> {
                        LoginScreen {
                            viewModel.setLoggedIn()
                            navController.navigate(HomeDestination)
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
