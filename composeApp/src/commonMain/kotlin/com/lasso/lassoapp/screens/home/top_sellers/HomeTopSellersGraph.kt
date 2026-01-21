package com.lasso.lassoapp.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.model.TopItem
import com.lasso.lassoapp.model.TopSellersResponse
import kotlin.math.roundToInt

// Vico (Compose Multiplatform)
import com.patrykandpatrick.vico.multiplatform.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.multiplatform.common.Fill
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.multiplatform.cartesian.data.columnSeries
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.multiplatform.common.component.rememberLineComponent

@Composable
private fun TopSalesColumnChart(
    title: String,
    items: List<TopItem>,
    modifier: Modifier = Modifier,
    maxItems: Int = 10,           // keep 10 if you want; you can lower to 6–8 for “Top N”
    labelMaxChars: Int = 12,      // shorten labels for the axis
    barSlotWidthDp: Int = 84,     // wider = more readable labels; also increases scroll width
) {
    Column(modifier = modifier.padding(16.dp)) {
        // Optional title
        // Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))

        if (items.isEmpty()) {
            Text("No hay datos", style = MaterialTheme.typography.bodyMedium)
            return@Column
        }

        val trimmed = remember(items, maxItems) { items.take(maxItems) }
        val labels = remember(trimmed, labelMaxChars) {
            trimmed.map { it.name.trim() }.map { name ->
                if (name.length > labelMaxChars) name.take(labelMaxChars) + "…" else name
            }
        }

        println("DIEGO labels $labels")

        val yValues = remember(trimmed) { trimmed.map { it.totalSalesAsDouble().toFloat() } }

        val modelProducer = remember { CartesianChartModelProducer() }

        LaunchedEffect(yValues) {
            modelProducer.runTransaction {
                columnSeries {
                    series(yValues)
                }
            }
        }

        // Make the chart wider than the screen so labels have room, then scroll.
        val minWidth = 420.dp
        val contentWidth = remember(labels.size, barSlotWidthDp) {
            (labels.size * barSlotWidthDp).dp.coerceAtLeast(minWidth)
        }

        val axisLabel = rememberAxisLabelComponent(
            style = TextStyle(
                color = MaterialTheme.colorScheme.onSurface, // 👈 visible in both themes
                fontSize = MaterialTheme.typography.labelSmall.fontSize
            )
        )

        val chart = rememberCartesianChart(
            rememberColumnCartesianLayer(
                columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                    columns = listOf(
                        rememberLineComponent(
                            fill = Fill(MaterialTheme.colorScheme.secondary),
                            thickness = 16.dp
                        )
                    )
                )
            ),
            startAxis = VerticalAxis.rememberStart(
                label = axisLabel,
                valueFormatter = { _, value, _ ->
                    formatCurrency(value)
                }
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                label = axisLabel,
                itemPlacer = HorizontalAxis.ItemPlacer.segmented(),
                //labelRotationDegrees = 45f,
                valueFormatter = { _, x, _ ->
                    val i = x.roundToInt()
                    labels.getOrNull(i) ?: ""
                }
            ),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            CartesianChartHost(
                chart = chart,
                modelProducer = modelProducer,
                modifier = Modifier
                    .width(contentWidth)
                    .height(320.dp),
            )
        }

        // Optional: hint to user that it scrolls.
        // Spacer(Modifier.height(8.dp))
        // Text("Scroll horizontally to see all labels", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun TopServicesChart(
    response: TopSellersResponse,
    modifier: Modifier = Modifier,
) {
    TopSalesColumnChart(
        title = "Top services by total sales",
        items = response.topServices,
        modifier = modifier,
    )
}

@Composable
fun TopProductsChart(
    response: TopSellersResponse,
    modifier: Modifier = Modifier,
) {
    TopSalesColumnChart(
        title = "Top products by total sales",
        items = response.topProducts,
        modifier = modifier,
    )
}

private fun formatCurrency(value: Double): String {
    // Round to whole dollars for axis readability
    val rounded = value.toInt()

    return "$" + rounded
        .toString()
        .reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()
}

