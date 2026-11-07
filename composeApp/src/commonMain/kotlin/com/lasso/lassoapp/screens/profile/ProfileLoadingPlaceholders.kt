package com.lasso.lassoapp.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.screens.utils.labelWidth
import com.lasso.lassoapp.screens.utils.rememberLoadingShimmer
import com.lasso.lassoapp.ui.theme.LassoPageBackground

@Composable
internal fun ProfileLoadingPlaceholders(state: ProfileState, modifier: Modifier = Modifier) {
    val brush = rememberLoadingShimmer()
    val density = LocalDensity.current
    val typography = MaterialTheme.typography
    val bodyHeight = with(density) { typography.bodyMedium.lineHeight.toDp() }
    val detailLabelHeight = with(density) { typography.labelMedium.lineHeight.toDp() }
    // Profile padding, avatar, section spacing, two detail rows, and action buttons.
    val profileHeight = 40.dp + 64.dp + 60.dp + (detailLabelHeight + bodyHeight + 32.dp) * 2 + 48.dp
    val buttonHeight = maxOf(48.dp, bodyHeight + 24.dp)
    val branchHeaderHeight = maxOf(
        48.dp,
        with(density) {
            typography.titleMedium.lineHeight.toDp() + typography.bodySmall.lineHeight.toDp()
        } + 4.dp,
    )
    val branchHeight = 32.dp + branchHeaderHeight + 16.dp + buttonHeight
    val isAdmin = state.employee?.role.equals("administrador", ignoreCase = true)
    val roleUnknown = state.employee == null

    Column(
        modifier.fillMaxSize().background(LassoPageBackground)
            .verticalScroll(rememberScrollState()).padding(16.dp).clearAndSetSemantics {},
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ProfileLoadingHeading(brush, "Perfil", "Información y configuración")
        ProfileLoadingShape(brush, Modifier.fillMaxWidth().height(profileHeight), cornerRadius = 16)
        if (roleUnknown || isAdmin || state.branches.isNotEmpty()) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ProfileLoadingHeading(
                    brush, "Sucursales", "Gestiona multiples sucursales.", Modifier.padding(top = 16.dp),
                )
                if (roleUnknown || isAdmin) {
                    val buttonWidth = labelWidth(
                        "Agregar sucursal", typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    ) + 58.dp // Horizontal padding, add icon, and spacing.
                    ProfileLoadingShape(brush, Modifier.width(buttonWidth).height(buttonHeight), cornerRadius = 14)
                }
                repeat(if (state.branches.isEmpty()) 2 else state.branches.size) {
                    ProfileLoadingShape(brush, Modifier.fillMaxWidth().height(branchHeight), cornerRadius = 16)
                }
            }
        }
    }
}

@Composable
private fun ProfileLoadingHeading(
    brush: Brush,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val titleStyle = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
    val subtitleStyle = MaterialTheme.typography.bodyMedium
    Column(modifier) {
        ProfileLoadingShape(
            brush,
            Modifier.width(labelWidth(title, titleStyle)).height(with(density) { titleStyle.lineHeight.toDp() }),
        )
        ProfileLoadingShape(
            brush,
            Modifier.width(labelWidth(subtitle, subtitleStyle)).height(with(density) { subtitleStyle.lineHeight.toDp() }),
        )
    }
}

@Composable
private fun ProfileLoadingShape(brush: Brush, modifier: Modifier, cornerRadius: Int = 4) {
    Box(modifier.background(brush, RoundedCornerShape(cornerRadius.dp)))
}
