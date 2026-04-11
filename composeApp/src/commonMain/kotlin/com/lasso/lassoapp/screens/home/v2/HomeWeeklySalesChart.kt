package com.lasso.lassoapp.screens.home.v2

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.screens.home.HomeDailySalesBar
import com.lasso.lassoapp.ui.theme.LassoTextMuted
import com.patrykandpatrick.vico.multiplatform.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.multiplatform.cartesian.data.columnSeries
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.multiplatform.common.Fill
import com.patrykandpatrick.vico.multiplatform.common.component.rememberLineComponent
import kotlin.math.roundToInt

@Composable
fun HomeWeeklySalesChart(
    lastSevenDays: List<HomeDailySalesBar>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Ventas últimos 7 días",
            style = MaterialTheme.typography.titleMedium,
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
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                ) {
                    lastSevenDays.forEach { day ->
                        Text(
                            text = day.valueFormatted,
                            style = MaterialTheme.typography.labelSmall,
                            color = LassoTextMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f),
                        )
                    }
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

                val chart =
                    rememberCartesianChart(
                        rememberColumnCartesianLayer(
                            columnProvider =
                                ColumnCartesianLayer.ColumnProvider.series(
                                    columns =
                                        listOf(
                                            rememberLineComponent(
                                                fill = Fill(MaterialTheme.colorScheme.primary),
                                                thickness = 18.dp,
                                            ),
                                        ),
                                ),
                        ),
                        bottomAxis =
                            HorizontalAxis.rememberBottom(
                                label = axisLabel,
                                itemPlacer = HorizontalAxis.ItemPlacer.segmented(),
                                valueFormatter = { _, x, _ ->
                                    val i = x.roundToInt()
                                    labels.getOrNull(i) ?: ""
                                },
                            ),
                    )

                CartesianChartHost(
                    chart = chart,
                    modelProducer = modelProducer,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                )
                }
            }
        }
    }
}
