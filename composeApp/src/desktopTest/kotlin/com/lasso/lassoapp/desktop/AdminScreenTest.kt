package com.lasso.lassoapp.desktop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.lasso.lassoapp.data.remote.endpoints.*
import com.lasso.lassoapp.screens.admin.*
import com.lasso.lassoapp.ui.theme.*
import org.junit.Test
import org.koin.compose.KoinIsolatedContext
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import java.io.File
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class AdminScreenTest {
    @Test fun partnerDetailsAndEmployeePermissionsFitPhoneAndDialogs() = runDesktopComposeUiTest(width = 480, height = 900) {
        val partner = AdminPartner(7, "Lasso Studio", "studio@example.test", licenseType = "premium", isActive = true,
            accessAllowed = true, salesUsage = SalesUsage(75, null, null, true))
        var saved: AdminEmployeeInput? = null
        var savedPartner: AdminPartnerInput? = null
        val api = object : AdminApi {
            override suspend fun getMasterAccess() = MasterAccess(true)
            override suspend fun getAdminPartner(id: Int) = partner
            override suspend fun getAdminPartners(page: Int, search: String, plan: String?, active: Boolean?) = AdminPartnerPage(1, 1, listOf(partner))
            override suspend fun saveAdminPartner(id: Int?, input: AdminPartnerInput): AdminPartner {
                savedPartner = input
                return partner
            }
            override suspend fun getAdminEmployees(partnerId: Int) = listOf(AdminEmployee(8, "Ana García", "ana@example.test", "empleado", false, false, false, true))
            override suspend fun saveAdminEmployee(partnerId: Int, id: Int?, input: AdminEmployeeInput): AdminEmployee {
                saved = input
                return AdminEmployee(8, input.name, input.email, input.role, input.disabled, false, input.canSeeSales, input.isDisplayedOnCalendar)
            }
        }
        val koin = koinApplication { modules(module { factory { AdminViewModel(api) } }) }
        val owner = object : ViewModelStoreOwner { override val viewModelStore = ViewModelStore() }
        try {
            setContent {
                KoinIsolatedContext(koin) {
                CompositionLocalProvider(LocalDensity provides Density(1f), LocalViewModelStoreOwner provides owner) {
                    MaterialTheme(colorScheme = LightLassoColorScheme, typography = lassoTypography()) {
                        Box(Modifier.requiredSize(480.dp, 900.dp)) { AdminScreen() }
                    }
                }
                }
            }
            waitUntil(timeoutMillis = 10_000) { onAllNodesWithText("Lasso Studio").fetchSemanticsNodes().isNotEmpty() }
            capture("admin-partners")
            onNodeWithText("Lasso Studio").performClick()
            onNodeWithText("75 / Sin límite").assertIsDisplayed()
            capture("admin-partner-details")
            onNodeWithText("Editar partner y membresía").performClick()
            onNodeWithText("Editar partner").assertIsDisplayed()
            onNodeWithText("Premium", substring = false).assertIsDisplayed()
            onNodeWithText("Cancelar", substring = false).assertIsDisplayed()
            onNodeWithText("Seleccionar fecha").performClick()
            onNodeWithContentDescription("Next Month").performClick()
            onNodeWithText("15", substring = false).performClick()
            capture("admin-membership-expiry-picker")
            onNodeWithText("OK").performClick()
            capture("admin-partner-dialog")
            onNodeWithText("Guardar", substring = false).performClick()
            onNodeWithText("Guardar cambios", substring = false).performClick()
            waitUntil(timeoutMillis = 10_000) { savedPartner != null }
            assertEquals("premium", savedPartner?.licenseType)
            val nextMonth = Clock.System.now().toLocalDateTime(adminTimeZone).date.plus(1, DateTimeUnit.MONTH)
            assertEquals(LocalDate(nextMonth.year, nextMonth.month, 15).atStartOfDayIn(adminTimeZone).toEpochMilliseconds(), savedPartner?.licenseExpiry)
            onNodeWithText("Editar partner y membresía").performClick()
            // Select and clear a date without changing the persisted membership type.
            onNodeWithText("Seleccionar fecha").performClick()
            onNodeWithText("OK").performClick()
            onNodeWithContentDescription("Quitar vencimiento (opcional)").performClick()
            savedPartner = null
            onNodeWithText("Guardar", substring = false).performClick()
            onNodeWithText("Guardar cambios", substring = false).performClick()
            waitUntil(timeoutMillis = 10_000) { savedPartner != null }
            assertEquals(null, savedPartner?.licenseExpiry)
            onNodeWithText("Ana García").performScrollTo().performClick()
            onNodeWithText("Empleado y permisos").assertIsDisplayed()
            capture("admin-employee-dialog")
            onNodeWithText("Cancelar", substring = false).assertIsDisplayed()
            onNodeWithText("Empleado", substring = false).performClick()
            onNodeWithText("Administrador", substring = false).performClick()
            onNodeWithText("Guardar", substring = false).performClick()
            onNodeWithText("Guardar cambios", substring = false).performClick()
            waitUntil(timeoutMillis = 10_000) { saved != null }
            assertEquals("administrador", saved?.role)
        } finally { owner.viewModelStore.clear(); koin.close() }
    }

    private fun ComposeUiTest.capture(name: String) {
        waitForIdle()
        val roots = onAllNodes(isRoot())
        val bitmap = roots[roots.fetchSemanticsNodes().lastIndex].captureToImage().asSkiaBitmap()
        val directory = File("build/admin-screenshots").apply { mkdirs() }
        org.jetbrains.skia.Image.makeFromBitmap(bitmap).encodeToData()?.use { File(directory, "$name.png").writeBytes(it.bytes) }
    }
}
