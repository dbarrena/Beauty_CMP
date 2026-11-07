package com.lasso.lassoapp.screens.home.v2

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.screens.utils.AdaptiveEqualPair
import com.lasso.lassoapp.screens.utils.labelWidth
import com.lasso.lassoapp.screens.utils.rememberLoadingShimmer

@Composable
internal fun HomeLoadingPlaceholders(isAdmin: Boolean, modifier: Modifier = Modifier) {
    val brush = rememberLoadingShimmer()
    val density = LocalDensity.current
    val titleHeight = with(density) { MaterialTheme.typography.headlineMedium.lineHeight.toDp() }
    val sectionTitleStyle = MaterialTheme.typography.titleMedium.copy(
        fontSize = 18.sp,
        fontWeight = FontWeight(600),
    )
    val sectionTitleHeight = with(density) { sectionTitleStyle.lineHeight.toDp() }
    val actionLabelStyle = MaterialTheme.typography.labelSmall.copy(
        fontSize = 12.sp,
        fontWeight = FontWeight(500),
    )
    val actionHeight = with(density) { actionLabelStyle.lineHeight.toDp() } + 48.dp

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 24.dp)
            .clearAndSetSemantics {},
    ) {
        HomeLoadingShape(brush, Modifier.fillMaxWidth(.65f).height(titleHeight))
        Spacer(Modifier.height(20.dp))
        if (isAdmin) {
            val minCardWidth = maxOf(
                120.dp,
                labelWidth("Transacciones", MaterialTheme.typography.bodyMedium) + 32.dp,
            )
            // Icon, label spacing, text lines, and padding match the metric cards.
            val metricHeight = with(density) {
                MaterialTheme.typography.bodyMedium.lineHeight.toDp() +
                    MaterialTheme.typography.headlineMedium.lineHeight.toDp()
            } + 100.dp
            Column(
                Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                repeat(2) {
                    AdaptiveEqualPair(
                        minFieldWidth = minCardWidth,
                        first = { HomeLoadingShape(brush, it.height(metricHeight), card = true) },
                        second = { HomeLoadingShape(brush, it.height(metricHeight), card = true) },
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
        }
        Column(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
            HomeLoadingShape(
                brush,
                Modifier.width(labelWidth("Acciones rápidas", sectionTitleStyle)).height(sectionTitleHeight),
            )
            Spacer(Modifier.height(12.dp))
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                val labels = listOf("Nueva venta", "Ventas", "Agendar") +
                    if (isAdmin) listOf("Comisiones") else emptyList()
                labels.forEach { label ->
                    val cellWidth = maxOf(75.dp, labelWidth(label.split(' '), actionLabelStyle)) + 8.dp
                    HomeLoadingShape(brush, Modifier.width(cellWidth).height(actionHeight), card = true)
                }
            }
        }
    }
}

@Composable
private fun HomeLoadingShape(brush: Brush, modifier: Modifier, card: Boolean = false) {
    Box(modifier.background(brush, RoundedCornerShape(if (card) 12.dp else 4.dp)))
}
