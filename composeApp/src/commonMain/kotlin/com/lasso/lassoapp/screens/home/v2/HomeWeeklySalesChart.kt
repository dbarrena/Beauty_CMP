package com.lasso.lassoapp.screens.home.v2

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.screens.home.HomeDailySalesBar
import com.lasso.lassoapp.ui.theme.LassoPrimary
import com.lasso.lassoapp.ui.theme.LassoPrimaryLight
import com.lasso.lassoapp.ui.theme.LassoTextMuted
import com.patrykandpatrick.vico.multiplatform.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.multiplatform.cartesian.data.columnSeries
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.multiplatform.common.Fill
import com.patrykandpatrick.vico.multiplatform.common.Position
import com.patrykandpatrick.vico.multiplatform.common.component.TextComponent
import com.patrykandpatrick.vico.multiplatform.common.component.rememberLineComponent
import com.patrykandpatrick.vico.multiplatform.common.data.ExtraStore
import kotlin.math.roundToInt

/** Minimum width per day column so currency + axis labels are readable; chart scrolls when wider than the screen. */
private val WeeklyBarSlotWidth = 96.dp

/** Extra space above the tallest bar so Vico draws data labels above the column, not inside it. */
private val ChartYHeadroomFraction = 0.15

private fun formatMoneyWhole(amount: Int): String {
    val s =
        amount
            .toString()
            .reversed()
            .chunked(3)
            .joinToString(",")
            .reversed()
    return "$$s"
}

/**
 * Extends the automatic max Y with [headroomFraction] so the highest bar is shorter than the plot
 * area, leaving room for the data label above it.
 */
private class HeadroomYRangeProvider(
    private val headroomFraction: Double = ChartYHeadroomFraction,
) : CartesianLayerRangeProvider {
    private val auto = CartesianLayerRangeProvider.auto()

    override fun getMinX(minX: Double, maxX: Double, extraStore: ExtraStore): Double =
        auto.getMinX(minX, maxX, extraStore)

    override fun getMaxX(minX: Double, maxX: Double, extraStore: ExtraStore): Double =
        auto.getMaxX(minX, maxX, extraStore)

    override fun getMinY(minY: Double, maxY: Double, extraStore: ExtraStore): Double =
        auto.getMinY(minY, maxY, extraStore)

    override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore): Double {
        val base = auto.getMaxY(minY, maxY, extraStore)
        if (base <= 0.0) return base
        return base * (1.0 + headroomFraction)
    }
}

@Composable
fun HomeWeeklySalesChart(
    lastSevenDays: List<HomeDailySalesBar>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
        Text(
            text = "Ventas últimos 7 días",
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight(600),
            ),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (lastSevenDays.isEmpty()) {
                    Text(
                        text = "No hay datos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LassoTextMuted,
                    )
                } else {
                    val scrollState = rememberScrollState()
                    val barCount = lastSevenDays.size
                    val contentWidth =
                        remember(barCount) {
                            WeeklyBarSlotWidth * barCount
                        }

                    val yValues = remember(lastSevenDays) { lastSevenDays.map { it.yValue } }
                    val labels = remember(lastSevenDays) { lastSevenDays.map { it.shortDayLabel } }

                    val modelProducer = remember { CartesianChartModelProducer() }
                    LaunchedEffect(yValues) {
                        modelProducer.runTransaction {
                            columnSeries {
                                series(yValues)
                            }
                        }
                    }

                    val axisLabel =
                        rememberAxisLabelComponent(
                            style =
                                TextStyle(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                ),
                        )

                    val currencyFormatter =
                        remember {
                            CartesianValueFormatter { _, value, _ ->
                                formatMoneyWhole(value.roundToInt())
                            }
                        }

                    val labelTypography = MaterialTheme.typography.labelSmall
                    val dataLabel =
                        remember(labelTypography) {
                            TextComponent(
                                textStyle = labelTypography.copy(color = LassoTextMuted),
                            )
                        }

                    val rangeProvider =
                        remember {
                            HeadroomYRangeProvider(ChartYHeadroomFraction)
                        }

                    val barFill =
                        remember {
                            Fill(
                                Brush.verticalGradient(
                                    colors =
                                        listOf(
                                            LassoPrimaryLight,
                                            LassoPrimary,
                                        ),
                                ),
                            )
                        }

                    val barShape =
                        remember {
                            RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                        }

                    val chart =
                        rememberCartesianChart(
                            rememberColumnCartesianLayer(
                                columnProvider =
                                    ColumnCartesianLayer.ColumnProvider.series(
                                        columns =
                                            listOf(
                                                rememberLineComponent(
                                                    fill = barFill,
                                                    thickness = 22.dp,
                                                    shape = barShape,
                                                ),
                                            ),
                                    ),
                                dataLabel = dataLabel,
                                dataLabelPosition = Position.Vertical.Top,
                                dataLabelValueFormatter = currencyFormatter,
                                rangeProvider = rangeProvider,
                            ),
                            bottomAxis =
                                HorizontalAxis.rememberBottom(
                                    label = axisLabel,
                                    guideline = null,
                                    tick = null,
                                    itemPlacer = HorizontalAxis.ItemPlacer.segmented(),
                                    valueFormatter = { _, x, _ ->
                                        val i = x.roundToInt()
                                        labels.getOrNull(i) ?: ""
                                    },
                                ),
                        )

                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .horizontalScroll(scrollState),
                    ) {
                        CartesianChartHost(
                            chart = chart,
                            modelProducer = modelProducer,
                            modifier =
                                Modifier
                                    .width(contentWidth)
                                    .height(260.dp),
                        )
                    }
                }
            }
        }
    }
}
