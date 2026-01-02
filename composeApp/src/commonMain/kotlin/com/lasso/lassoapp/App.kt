package com.lasso.lassoapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lasso.lassoapp.screens.calendar.CalendarScreen
import com.lasso.lassoapp.screens.cash_closure.create.CashClosureScreen
import com.lasso.lassoapp.screens.configuration.ConfigurationScreen
import com.lasso.lassoapp.screens.home.HomeScreen
import com.lasso.lassoapp.screens.login.LoginScreen
import com.lasso.lassoapp.screens.pos.PosScreen
import com.lasso.lassoapp.screens.product_service.ProductServiceScreen
import com.lasso.lassoapp.screens.sales.SalesScreen
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import com.lasso.lassoapp.ui.theme.LightLassoColorScheme

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

@Serializable
object CalendarDestination

@Serializable
object CashClosureDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    MaterialTheme(
        colorScheme = LightLassoColorScheme
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
            ConfigurationDestination::class.qualifiedName -> "Herramientas"
            ProductServiceDestination::class.qualifiedName -> "Productos y Servicios"
            DetailDestination::class.qualifiedName -> "Detalle"
            CalendarDestination::class.qualifiedName -> "Calendario de citas"
            CashClosureDestination::class.qualifiedName -> "Corte de caja"
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

                                    //Crossfade(targetState = titleText) { targetTitle ->
                                    Text(text = titleText)
                                    //}
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
                                actions = {
                                    IconButton(
                                        onClick = {
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.AccountCircle,
                                            contentDescription = "Profile",
                                            tint = MaterialTheme.colorScheme.onPrimary
                                        )
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
                        Column {
                            Divider(
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                            NavigationBar(
                                modifier = Modifier.height(105.dp),
                                containerColor = Color.White,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ) {
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            Icons.Default.Home,
                                            contentDescription = null,
                                            tint = if (currentDestination?.hierarchy?.any { it.route == HomeDestination::class.qualifiedName } == true)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    //label = { Text("Inicio") },
                                    selected = currentDestination?.hierarchy?.any { it.route == HomeDestination::class.qualifiedName } == true,
                                    onClick = {
                                        navController.navigate(HomeDestination)
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = Color.Transparent
                                    )
                                )
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            Icons.Default.CalendarMonth,
                                            contentDescription = null,
                                            tint = if (currentDestination?.hierarchy?.any { it.route == CalendarDestination::class.qualifiedName } == true)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    //label = { Text("POS") },
                                    selected = currentDestination?.hierarchy?.any { it.route == CalendarDestination::class.qualifiedName } == true,
                                    onClick = {
                                        navController.navigate(CalendarDestination)
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = Color.Transparent
                                    )
                                )
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            Icons.Default.ShoppingCart,
                                            contentDescription = null,
                                            tint = if (currentDestination?.hierarchy?.any { it.route == PosDestination::class.qualifiedName } == true)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    //label = { Text("POS") },
                                    selected = currentDestination?.hierarchy?.any { it.route == PosDestination::class.qualifiedName } == true,
                                    onClick = {
                                        navController.navigate(PosDestination)
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = Color.Transparent
                                    )
                                )
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            Icons.Filled.AttachMoney,
                                            contentDescription = null,
                                            tint = if (currentDestination?.hierarchy?.any { it.route == SalesDestination::class.qualifiedName } == true)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    //label = { Text("Ventas") },
                                    selected = currentDestination?.hierarchy?.any { it.route == SalesDestination::class.qualifiedName } == true,
                                    onClick = { navController.navigate(SalesDestination) },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = Color.Transparent
                                    )
                                )
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            Icons.Default.Apps,
                                            contentDescription = null,
                                            tint = if (currentDestination?.hierarchy?.any { it.route == ConfigurationDestination::class.qualifiedName } == true)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    //label = { Text("Configuracion") },
                                    selected = currentDestination?.hierarchy?.any { it.route == ConfigurationDestination::class.qualifiedName } == true,
                                    onClick = {
                                        navController.navigate(ConfigurationDestination)
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = Color.Transparent
                                    )
                                )
                            }
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
                        fadeIn()
                    },
                    exitTransition = {
                        fadeOut()
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
                        ConfigurationScreen(
                            onLoggedOut = {
                                viewModel.setLoggedOut()
                                navController.navigate(LoginDestination)
                            }
                        ) {
                            navController.navigate(CashClosureDestination)
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
                    composable<CalendarDestination> {
                        CalendarScreen()
                    }
                    composable<CashClosureDestination> {
                        CashClosureScreen()
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
            route == ConfigurationDestination::class.qualifiedName ||
            route == CalendarDestination::class.qualifiedName
}
