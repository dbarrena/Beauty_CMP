package com.lasso.lassoapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.*
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.payment_amount.CheckoutSplitPaymentContent
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.payment_method.CheckoutPaymentMethodPickerContent
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.register_discount.RegisterDiscountContent
import com.lasso.lassoapp.screens.utils.*
import com.lasso.lassoapp.ui.theme.LightLassoColorScheme
import com.lasso.lassoapp.ui.theme.lassoTypography
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Permanent presentation fixtures: callbacks are local state; no Koin or live API is used. */
@RunWith(AndroidJUnit4::class)
class AccessibilityLayoutTest {
    @get:Rule val compose = createComposeRule()

    @Composable
    private fun Theme(scale: Float, content: @Composable () -> Unit) {
        val density = LocalDensity.current
        CompositionLocalProvider(LocalDensity provides Density(density.density, scale)) {
            MaterialTheme(colorScheme = LightLassoColorScheme, typography = lassoTypography()) {
                Box(Modifier.fillMaxSize()) { content() }
            }
        }
    }

    @Test
    fun navigationLabelsAndTouchTargetsAtAllTextSizes() {
        var scale by mutableStateOf(1f)
        var selected by mutableStateOf(MainTab.Home)
        compose.setContent {
            Theme(scale) {
                Box(Modifier.width(320.dp)) { MainNavigationBar(selected) { selected = it } }
            }
        }
        for (font in listOf(1f, 1.8f, 2f)) {
            compose.runOnIdle { scale = font }
            for (tab in MainTab.entries) {
                compose
                    .onNode(
                        (hasText(tab.label) or hasContentDescription(tab.label)) and
                            hasClickAction()
                    )
                    .performClick()
                compose.onNodeWithText(tab.label).assertIsDisplayed()
                compose
                    .onNode(
                        (hasText(tab.label) or hasContentDescription(tab.label)) and
                            hasClickAction()
                    )
                    .assertHeightIsAtLeast(48.dp)
                    .assertWidthIsAtLeast(48.dp)
            }
        }
    }

    @Test
    fun headerMovesWithoutLosingTextOrFocus() {
        var compact by mutableStateOf(false)
        compose.setContent {
            Theme(1.8f) {
                var query by remember { mutableStateOf("") }
                AdaptiveScrollContent(
                    modifier = Modifier.width(320.dp).height(if (compact) 160.dp else 500.dp),
                    header = {
                        Column {
                            Text("Punto de Venta")
                            TextField(query, { query = it }, Modifier.testTag("search"))
                        }
                    },
                    actions = { Text("Acción", Modifier.height(60.dp)) },
                ) {
                    repeat(20) { Text("Artículo $it") }
                }
            }
        }
        compose.onNodeWithTag("search").performClick().performTextInput("Labial")
        compose.runOnIdle { compact = true }
        compose.onNodeWithTag("search").assertTextContains("Labial").assertIsFocused()
        compose.runOnIdle { compact = false }
        compose.onNodeWithTag("search").assertTextContains("Labial").assertIsFocused()
    }

    @Test fun checkoutAtNormalTextSize() = checkoutMethodsBackDiscountAndOutsideDismissal(1f)

    @Test fun checkoutAtLargeTextSize() = checkoutMethodsBackDiscountAndOutsideDismissal(1.8f)

    @Test fun checkoutAtLargestTextSize() = checkoutMethodsBackDiscountAndOutsideDismissal(2f)

    // System font changes recreate the Activity. Each configuration gets a fresh native window.
    private fun checkoutMethodsBackDiscountAndOutsideDismissal(scale: Float) {
        var page by mutableStateOf("picker")
        var method by mutableStateOf<CheckoutPaymentMethod>(CheckoutPaymentMethod.Cash)
        var closed by mutableStateOf(false)
        var fakeSubmissions = 0
        compose.setContent {
            Theme(scale) {
                if (!closed)
                    LassoDialogFrame(onDismiss = { closed = true }) {
                        when (page) {
                            "picker" ->
                                CheckoutPaymentMethodPickerContent(
                                    14000.0,
                                    onClose = { closed = true },
                                    onMethodClicked = {
                                        method = it
                                        page = "payment"
                                    },
                                    onRegisterDiscountClicked = { page = "discount" },
                                )
                            "discount" ->
                                RegisterDiscountContent(
                                    14000.0,
                                    null,
                                    { page = "picker" },
                                    { closed = true },
                                    { page = "picker" },
                                )
                            else ->
                                CheckoutSplitPaymentContent(
                                    14000.0,
                                    { page = "picker" },
                                    { closed = true },
                                    { fakeSubmissions++ },
                                    {},
                                    checkoutPaymentMethod = method,
                                    state = CheckoutDialogViewModelV2.CheckoutDialogState(),
                                    showEmployeeSelector = false,
                                )
                        }
                    }
            }
        }
        for (label in
            listOf("Efectivo", "Tarjeta de Débito", "Transferencia", "Usar Múltiples Métodos")) {
            compose.onNodeWithText(label).performScrollTo().performClick()
            if (method != CheckoutPaymentMethod.Multiple) {
                compose.onNodeWithText("Restan ", substring = true).assertDoesNotExist()
                compose.onNodeWithText("Confirmar pago en ${method.display}").assertIsEnabled()
            }
            compose.onNodeWithText("Volver").performClick()
        }
        compose.onNodeWithText("Aplicar Descuento").performScrollTo().performClick()
        compose.onNodeWithText("Confirmar Descuento").assertIsNotEnabled()
        compose.onNodeWithText("Volver").performClick()
        compose.onNodeWithText("Efectivo").performScrollTo().performClick()
        compose.onNodeWithText("Total a cobrar").performTouchInput { click() }
        compose.runOnIdle { assertFalse(closed) }
        // Click the screen edge as well as the central backdrop: margins must also dismiss.
        compose.onAllNodes(isRoot()).onLast().performTouchInput { click(Offset(1f, 1f)) }
        compose.runOnIdle {
            assertTrue(closed)
            assertEquals(0, fakeSubmissions)
        }
    }

    @Test
    fun appointmentFieldsAndActionsRemainReachableAtTwoHundredPercent() {
        compose.setContent {
            Theme(2f) {
                com.lasso.lassoapp.screens.calendar.dialog.AppointmentDialog(
                    appointment = null,
                    appointmentEmployeeId = null,
                    clients = emptyList(),
                    employees = emptyList(),
                    services = emptyList(),
                    initialDate = kotlinx.datetime.LocalDate(2026, 10, 21),
                    isSaving = false,
                    isDeleting = false,
                    onDismiss = {},
                    onNewClient = {},
                    onResult = {},
                    onDelete = {},
                )
            }
        }
        compose.onNodeWithText("Guardar").assertIsDisplayed()
        compose.onNodeWithText("Servicio (opcional)").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Hora fin").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun compactCartPreservesLongPriceAndRemoveTarget() {
        val product =
            com.lasso.lassoapp.model.Product(
                name = "Servicio de prueba con nombre largo",
                price = "123456.78",
                commissionPercentage = null,
            )
        val line = com.lasso.lassoapp.screens.pos.SelectedPosItem(lassoItem = product)
        var removed = false
        compose.setContent {
            Theme(2f) {
                com.lasso.lassoapp.screens.pos.v2.bottom_sheet.PosBottomSheet(
                    listOf(line),
                    line.price,
                    {},
                    {},
                    { removed = true },
                    {},
                    true,
                    Modifier.width(320.dp).heightIn(max = 500.dp),
                )
            }
        }
        compose.onAllNodesWithText("$123,456.78").assertCountEquals(2)
        compose.onAllNodesWithText("$123,456.78").onFirst().assertIsDisplayed()
        compose.onAllNodesWithText("$123,456.78").onLast().assertIsDisplayed()
        compose.onNodeWithContentDescription("Eliminar").assertHeightIsAtLeast(48.dp).performClick()
        compose.runOnIdle { assertTrue(removed) }
    }

    @Test
    fun editorRejectsEmptyQuantityAndNegativePrice() {
        val product =
            com.lasso.lassoapp.model.Product(
                name = "Nombre completo del artículo",
                price = "250",
                commissionPercentage = null,
            )
        compose.setContent {
            Theme(1.8f) {
                com.lasso.lassoapp.screens.pos.edit_dialog.PosEditDialogScreen(
                    com.lasso.lassoapp.screens.pos.SelectedPosItem(lassoItem = product)
                ) {}
            }
        }
        val quantity = compose.onAllNodes(hasSetTextAction()).onFirst()
        quantity.performTextClearance()
        compose.onNodeWithText("Confirmar Cambios").assertIsNotEnabled()
        quantity.performTextInput("2")
        compose.onNodeWithText("Confirmar Cambios").assertIsEnabled()
        compose.onAllNodes(hasSetTextAction()).onLast().performTextReplacement("-1")
        compose.onNodeWithText("Confirmar Cambios").assertIsNotEnabled()
    }

    @Test
    fun existingSalePaymentsUseTheSameAdaptiveShell() {
        compose.setContent {
            Theme(2f) {
                com.lasso.lassoapp.screens.sales.detail.edit_dialog.EditSalePaymentsDialog(
                    payments = emptyList(),
                    total = 250.0,
                    isSaving = false,
                    error = null,
                    onDismiss = {},
                    onSave = {},
                )
            }
        }
        compose.onNodeWithText("Efectivo").performScrollTo().performClick()
        compose.onNodeWithText("Editar métodos de pago").assertIsDisplayed()
        compose.onNodeWithText("Confirmar pago en Efectivo").assertIsEnabled()
        compose.onNodeWithText("Volver").performClick()
        compose.onNodeWithText("Aplicar Descuento").assertDoesNotExist()
    }

    @Test
    fun posSearchKeepsFocusWhenHeaderMovesIntoCatalog() {
        var short by mutableStateOf(false)
        compose.setContent {
            Theme(1.8f) {
                var query by remember { mutableStateOf("") }
                val header: @Composable () -> Unit = {
                    Column {
                        com.lasso.lassoapp.screens.pos.v2.header.PosHeader({}, true)
                        com.lasso.lassoapp.screens.pos.v2.search_bar.PosSearchFilterBar(
                            query,
                            { query = it },
                            onCatalogFilterChange = {},
                        )
                    }
                }
                val grid = androidx.compose.foundation.lazy.grid.rememberLazyGridState()
                Box(Modifier.width(320.dp).height(if (short) 240.dp else 650.dp)) {
                    com.lasso.lassoapp.screens.pos.v2.catalog.PosCatalogGrid(
                        false,
                        emptyList(),
                        {},
                        Modifier.fillMaxSize(),
                        header = header,
                        state = grid,
                    )
                }
            }
        }
        compose.onNode(hasSetTextAction()).performClick().performTextInput("Labial")
        compose.runOnIdle { short = true }
        compose.onNode(hasSetTextAction()).assertTextContains("Labial").assertIsFocused()
        compose.runOnIdle { short = false }
        compose.onNode(hasSetTextAction()).assertTextContains("Labial").assertIsFocused()
        compose.onNode(hasSetTextAction()).performImeAction()
        compose.onNode(hasSetTextAction()).assertTextContains("Labial").assertIsNotFocused()
        compose.runOnIdle { short = true }
        compose.runOnIdle { short = false }
        compose.onNode(hasSetTextAction()).assertIsNotFocused()
    }

    @Test
    fun ventasTitleScrollsWithSummaryAndHistory() {
        compose.setContent {
            Theme(1.8f) {
                Box(Modifier.width(320.dp).height(600.dp)) {
                    com.lasso.lassoapp.screens.sales.v2.SalesScreenContentV2(
                        state = com.lasso.lassoapp.screens.sales.v2.SalesScreenStateV2(),
                        isAdmin = true,
                        onBack = {},
                        onReloadSales = {},
                        onClearSelectedSale = {},
                        onLoadForPeriod = {},
                        onApplyCustomDateRange = { _, _ -> },
                        onSetSelectedSale = {},
                        onDeleteSale = {},
                    )
                }
            }
        }
        val title = compose.onNodeWithText("Ventas", useUnmergedTree = true)
        title.assertIsDisplayed()
        val list =
            compose.onNode(
                hasScrollToIndexAction() and
                    SemanticsMatcher.keyIsDefined(
                        androidx.compose.ui.semantics.SemanticsProperties.VerticalScrollAxisRange
                    )
            )
        list.performScrollToIndex(3)
        title.assertIsNotDisplayed()
        list.performScrollToIndex(0)
        title.assertIsDisplayed()
    }

    @Test
    fun successKeepsTheCollectedAmountVisibleAtLargestTextSize() {
        var closed by mutableStateOf(false)
        compose.setContent {
            Theme(2f) {
                if (!closed)
                    LassoDialogFrame(onDismiss = { closed = true }) {
                        com.lasso.lassoapp.screens.pos.v2.checkout_dialog.success
                            .CheckoutSaleSuccessContent(
                                collectedAmount = 14000.0,
                                onClose = { closed = true },
                            )
                    }
            }
        }
        compose.onNodeWithText("$14,000.00 cobrados").assertIsDisplayed()
        compose.onNodeWithContentDescription("Cerrar").performClick()
        compose.runOnIdle { assertTrue(closed) }
    }
}
