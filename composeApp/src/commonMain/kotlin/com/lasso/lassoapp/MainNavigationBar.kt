package com.lasso.lassoapp

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import lassoapp.composeapp.generated.resources.Res
import lassoapp.composeapp.generated.resources.agenda_icon
import lassoapp.composeapp.generated.resources.home_icon
import lassoapp.composeapp.generated.resources.sales_icon
import lassoapp.composeapp.generated.resources.shopping_cart_icon
import org.jetbrains.compose.resources.painterResource

enum class MainTab(val label: String) {
    Home("Inicio"),
    Calendar("Agenda"),
    Pos("Venta"),
    Sales("Ventas"),
    Configuration("Mas"),
}

@Composable
fun MainNavigationBar(selectedTab: MainTab, onSelect: (MainTab) -> Unit) {
    val labelStyle = MaterialTheme.typography.labelMedium
    val density = LocalDensity.current
    val resolver = LocalFontFamilyResolver.current
    val measurer = rememberTextMeasurer()
    val labelSize =
        remember(selectedTab, labelStyle, density, resolver, measurer) {
            measurer.measure(selectedTab.label, labelStyle, softWrap = false).size
        }
    val labelWidth = with(density) { labelSize.width.toDp() }
    val labelHeight = with(density) { labelSize.height.toDp() }
    val itemHeight = maxOf(80.dp, 22.dp + labelHeight + 24.dp)

    Column {
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
        NavigationBar(
            modifier = Modifier.heightIn(min = 105.dp),
            containerColor = Color.White,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ) {
            // Use the inset-adjusted width available to the actual items.
            BoxWithConstraints(Modifier.weight(1f)) {
                val selectedWidth = selectedTabWidth(maxWidth.value, labelWidth.value).dp
                Row {
                    MainTab.entries.forEach { tab ->
                        val selected = tab == selectedTab
                        // Each inner Row retains Material's item behavior while the outer Row
                        // reserves label space and at least 48 dp for the other touch targets.
                        Row(
                            modifier =
                                (if (selected) Modifier.width(selectedWidth)
                                    else Modifier.weight(1f))
                                    .heightIn(min = itemHeight)
                        ) {
                            NavigationBarItem(
                                selected = selected,
                                onClick = { onSelect(tab) },
                                icon = {
                                    val tint =
                                        if (selected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    MainTabIcon(tab, tint)
                                },
                                label = {
                                    Text(
                                        tab.label,
                                        style = labelStyle,
                                        color = MaterialTheme.colorScheme.primary,
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                },
                                colors =
                                    NavigationBarItemDefaults.colors(
                                        indicatorColor = Color.Transparent
                                    ),
                                alwaysShowLabel = false,
                            )
                        }
                    }
                }
            }
        }
    }
}

internal fun selectedTabWidth(available: Float, label: Float): Float {
    val equal = available / MainTab.entries.size
    return maxOf(equal, label + 8f)
        .coerceAtMost(maxOf(equal, available - 48f * (MainTab.entries.size - 1)))
}

@Composable
private fun MainTabIcon(tab: MainTab, tint: Color) {
    val resource =
        when (tab) {
            MainTab.Home -> Res.drawable.home_icon
            MainTab.Calendar -> Res.drawable.agenda_icon
            MainTab.Pos -> Res.drawable.shopping_cart_icon
            MainTab.Sales -> Res.drawable.sales_icon
            MainTab.Configuration -> null
        }
    if (resource == null)
        Icon(
            Icons.Default.Apps,
            contentDescription = tab.label,
            tint = tint,
            modifier = Modifier.size(22.dp),
        )
    else
        Icon(
            painterResource(resource),
            contentDescription = tab.label,
            tint = tint,
            modifier = Modifier.size(22.dp),
        )
}
