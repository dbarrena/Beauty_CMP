package com.lasso.lassoapp.screens.sales.v2.payment_breakdown

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.model.SaleApiResponse
import com.lasso.lassoapp.screens.sales.v2.paymentBreakdown
import lassoapp.composeapp.generated.resources.Res
import lassoapp.composeapp.generated.resources.card_icon
import lassoapp.composeapp.generated.resources.cash_icon
import lassoapp.composeapp.generated.resources.phone_icon
import org.jetbrains.compose.resources.painterResource

@Composable
fun SalesPaymentBreakdownRow(
    sales: List<SaleApiResponse>,
    modifier: Modifier = Modifier,
    isLoading: Boolean,
) {
    val paymentBreakdown = sales.paymentBreakdown()
    Column(modifier = modifier) {
        Text(
            text = "Por método de pago",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(8.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            item {
                PaymentMiniCard(
                    label = "Efectivo",
                    amount = paymentBreakdown.cash,
                    isLoading = isLoading,
                    icon = {
                        Image(
                            painter = painterResource(Res.drawable.cash_icon),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit,
                        )
                    },
                    modifier = Modifier.width(128.dp),
                )
            }
            item {
                PaymentMiniCard(
                    label = "Tarjeta",
                    amount = paymentBreakdown.card,
                    icon = {
                        Image(
                            painter = painterResource(Res.drawable.card_icon),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit,
                        )
                    },
                    modifier = Modifier.width(128.dp),
                    isLoading = isLoading
                )
            }
            item {
                PaymentMiniCard(
                    label = "Transferencia",
                    amount = paymentBreakdown.transfer,
                    icon = {
                        Image(
                            painter = painterResource(Res.drawable.phone_icon),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit,
                        )
                    },
                    modifier = Modifier.width(128.dp),
                    isLoading = isLoading
                )
            }
        }
    }
}
