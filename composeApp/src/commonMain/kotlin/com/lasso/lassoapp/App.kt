package com.lasso.lassoapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lasso.lassoapp.screens.calendar.CalendarScreen
import com.lasso.lassoapp.screens.commissions.CommissionsScreen
import com.lasso.lassoapp.screens.cash_closure.create.CashClosureScreen
import com.lasso.lassoapp.screens.cash_closure.records.CashClosureRecordsScreen
import com.lasso.lassoapp.screens.clients.ClientsScreen
import com.lasso.lassoapp.screens.configuration.ConfigurationScreen
import com.lasso.lassoapp.screens.configuration.ConfigurationScreenRoutes
import com.lasso.lassoapp.screens.home.v2.HomeScreenV2
import com.lasso.lassoapp.screens.login.LoginScreen
import com.lasso.lassoapp.screens.register.RegisterScreen
import com.lasso.lassoapp.screens.pos.v2.PosScreenV2
import com.lasso.lassoapp.screens.product_catalog.ProductCatalogScreen
import com.lasso.lassoapp.screens.reports.sales_by_product_category.SalesByProductCategoryScreen
import com.lasso.lassoapp.screens.sales.v2.SalesScreenV2
import com.lasso.lassoapp.ui.theme.LightLassoColorScheme
import com.lasso.lassoapp.ui.theme.lassoTypography
import kotlinx.serialization.Serializable
import lassoapp.composeapp.generated.resources.Res
import lassoapp.composeapp.generated.resources.bell_icon
import lassoapp.composeapp.generated.resources.lasso_icon_full_cropped_title_only
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.painterResource
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
object LoginDestination

@Serializable
object RegisterDestination

@Serializable
object CalendarDestination

@Serializable
object CashClosureDestination

@Serializable
object CashClosureRecordsDestination

@Serializable
object ProductCatalogDestination

@Serializable
object SalesByProductCategoriesDestination

@Serializable
object EmployeesDestination

@Serializable
object ClientsDestination

@Serializable
object CommissionsDestination

@OptIn(ExperimentalMaterial3Api::class, InternalResourceApi::class)
@Composable
fun App() {
    MaterialTheme(
        colorScheme = LightLassoColorScheme,
        typography = lassoTypography(),
    ) {
        val navController: NavHostController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        val viewModel = koinViewModel<AppViewModel>()
        val state = viewModel.state.collectAsState()

        val showBottomNav = isRootDestination(currentDestination)
        val showMainTopBar = isRootDestination(currentDestination)

        val onBackNavigation: () -> Unit = { navController.popBackStack() }

        val titleText: String = when {
            currentDestination?.hasRoute<HomeDestination>() == true -> "Inicio"
            currentDestination?.hasRoute<PosDestination>() == true -> "Punto de Venta"
            currentDestination?.hasRoute<SalesDestination>() == true -> "Ventas"
            currentDestination?.hasRoute<ConfigurationDestination>() == true -> "Herramientas"
            currentDestination?.hasRoute<DetailDestination>() == true -> "Detalle"
            currentDestination?.hasRoute<CalendarDestination>() == true -> "Calendario de citas"
            currentDestination?.hasRoute<CashClosureDestination>() == true -> "Corte de caja"
            currentDestination?.hasRoute<CashClosureRecordsDestination>() == true -> "Registro cortes de caja"
            currentDestination?.hasRoute<ProductCatalogDestination>() == true -> "Productos y Servicios"
            currentDestination?.hasRoute<SalesByProductCategoriesDestination>() == true -> "Ventas por Categoria"
            currentDestination?.hasRoute<EmployeesDestination>() == true -> "Empleados"
            currentDestination?.hasRoute<ClientsDestination>() == true -> "Clientes"
            currentDestination?.hasRoute<CommissionsDestination>() == true -> "Comisiones"
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
                                    Box(modifier = Modifier.padding(0.dp)) {
                                        Image(
                                            painter = painterResource(Res.drawable.lasso_icon_full_cropped_title_only),
                                            contentDescription = titleText.ifBlank { "LassoApp" },
                                            modifier = Modifier.height(20.dp),
                                        )
                                    }
                                    //}
                                },
                                navigationIcon = {
                                    if (!showMainTopBar) {
                                        IconButton(
                                            onClick = onBackNavigation
                                        ) {
                                            Icon(
                                                Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = "Back",
                                                tint = MaterialTheme.colorScheme.primary,

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
                                            modifier = Modifier.size(20.dp),
                                            painter = painterResource(Res.drawable.bell_icon),
                                            contentDescription = "Profile",
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    titleContentColor = MaterialTheme.colorScheme.onSurface
                                ),
                            )
                        }
                    }
                },
                bottomBar = {
                    if (showBottomNav && isLoggedIn) {
                        val selectedTab = when {
                            currentDestination?.hasRoute<CalendarDestination>() == true -> MainTab.Calendar
                            currentDestination?.hasRoute<PosDestination>() == true -> MainTab.Pos
                            currentDestination?.hasRoute<SalesDestination>() == true -> MainTab.Sales
                            currentDestination?.hasRoute<ConfigurationDestination>() == true -> MainTab.Configuration
                            else -> MainTab.Home
                        }
                        MainNavigationBar(selectedTab) { tab ->
                            when (tab) {
                                MainTab.Home -> navController.navigate(HomeDestination)
                                MainTab.Calendar -> navController.navigate(CalendarDestination)
                                MainTab.Pos -> navController.navigate(PosDestination)
                                MainTab.Sales -> navController.navigate(SalesDestination)
                                MainTab.Configuration -> navController.navigate(ConfigurationDestination)
                            }
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                    modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding),
                    enterTransition = {
                        fadeIn()
                    },
                    exitTransition = {
                        fadeOut()
                    }
                ) {
                    composable<HomeDestination> {
                        HomeScreenV2(
                            onNavigateToPos = { navController.navigate(PosDestination) },
                            onNavigateToCalendar = { navController.navigate(CalendarDestination) },
                            onNavigateToSales = { navController.navigate(SalesDestination) },
                            onNavigateToCashClosure = { navController.navigate(CashClosureDestination) },
                            onNavigateToCommissions = { navController.navigate(CommissionsDestination) },
                        )
                    }
                    composable<PosDestination> {
                        PosScreenV2()
                    }
                    composable<SalesDestination> {
                        SalesScreenV2(
                            isAdmin = state.value.isAdmin,
                            onBack = {
                                navController.navigate(HomeDestination) {
                                    launchSingleTop = true
                                }
                            },
                        )
                    }
                    composable<ConfigurationDestination> {
                        ConfigurationScreen(
                            onLoggedOut = {
                                viewModel.setLoggedOut()
                                navController.navigate(LoginDestination)
                            }
                        ) { destination ->
                            when (destination) {
                                ConfigurationScreenRoutes.CASH_CLOSURE -> {
                                    navController.navigate(CashClosureDestination)
                                }

                                ConfigurationScreenRoutes.CASH_CLOSURE_RECORDS -> {
                                    navController.navigate(CashClosureRecordsDestination)
                                }

                                ConfigurationScreenRoutes.SALES_BY_PRODUCT_CATEGORIES -> {
                                    navController.navigate(SalesByProductCategoriesDestination)
                                }

                                ConfigurationScreenRoutes.EMPLOYEES -> {
                                    navController.navigate(EmployeesDestination)
                                }

                                ConfigurationScreenRoutes.CLIENTS -> {
                                    navController.navigate(ClientsDestination)
                                }

                                ConfigurationScreenRoutes.COMMISSIONS -> {
                                    navController.navigate(CommissionsDestination)
                                }

                                ConfigurationScreenRoutes.PRODUCT_CATALOG -> {
                                    navController.navigate(ProductCatalogDestination)
                                }
                            }
                        }
                    }
                    composable<LoginDestination> {
                        LoginScreen(onRegister = {
                            navController.navigate(RegisterDestination) { launchSingleTop = true }
                        }) {
                            viewModel.setLoggedIn()
                            navController.navigate(HomeDestination)
                        }
                    }
                    composable<RegisterDestination> {
                        RegisterScreen(onLogin = { navController.popBackStack() })
                    }
                    composable<CalendarDestination> {
                        CalendarScreen()
                    }
                    composable<CashClosureDestination> {
                        CashClosureScreen()
                    }
                    composable<CashClosureRecordsDestination> {
                        CashClosureRecordsScreen()
                    }
                    composable<ProductCatalogDestination> {
                        ProductCatalogScreen()
                    }
                    composable<SalesByProductCategoriesDestination> {
                        SalesByProductCategoryScreen()
                    }
                    composable<EmployeesDestination> {
                        com.lasso.lassoapp.screens.employees.EmployeesScreen()
                    }
                    composable<ClientsDestination> {
                        ClientsScreen()
                    }
                    composable<CommissionsDestination> {
                        CommissionsScreen()
                    }
                }
            }
        }
    }
}

fun isRootDestination(destination: NavDestination?): Boolean {
    return destination?.hasRoute<HomeDestination>() == true ||
            destination?.hasRoute<PosDestination>() == true ||
            destination?.hasRoute<SalesDestination>() == true ||
            destination?.hasRoute<ConfigurationDestination>() == true ||
            destination?.hasRoute<CalendarDestination>() == true
}
