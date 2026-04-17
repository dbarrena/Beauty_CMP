package com.lasso.lassoapp.screens.sales.v2.transaction_card

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.model.PaymentApiResponse
import com.lasso.lassoapp.model.SaleApiResponse
import com.lasso.lassoapp.model.SaleDetailApiResponse
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.payment_method.CheckoutPaymentMethodTokens
import com.lasso.lassoapp.screens.sales.v2.formatMoney
import com.lasso.lassoapp.screens.utils.toSaleCardDateTimeString
import com.lasso.lassoapp.ui.theme.LassoTextPlaceholder
import com.lasso.lassoapp.ui.theme.LassoTertiary
import lassoapp.composeapp.generated.resources.Res
import lassoapp.composeapp.generated.resources.card_icon
import lassoapp.composeapp.generated.resources.cash_icon
import lassoapp.composeapp.generated.resources.checkout_payment_efectivo
import lassoapp.composeapp.generated.resources.edit_icon
import lassoapp.composeapp.generated.resources.phone_icon
import lassoapp.composeapp.generated.resources.trash_can_icon
import org.jetbrains.compose.resources.painterResource

@Composable
fun SalesTransactionCard(
    sale: SaleApiResponse,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.AccessTime,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = LassoTextPlaceholder,
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = sale.createdAt.toSaleCardDateTimeString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = LassoTextPlaceholder,
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    sale.clientId?.let { id ->
                        Text(
                            text = "Cliente #$id",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(Modifier.height(4.dp))
                    }
                    sale.saleDetails.forEach { detail ->
                        val line = detailLineLabel(detail)
                        if (line.isNotBlank()) {
                            Text(
                                text = "• $line",
                                style = MaterialTheme.typography.bodySmall,
                                color = LassoTextPlaceholder,
                            )
                        }
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatMoney(sale.total),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                    )
                    sale.payments.forEach { payment ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when (payment.paymentType) {
                                    "cash" -> Icons.Outlined.AttachMoney
                                    "transfer" -> Icons.Outlined.Smartphone
                                    else -> Icons.Outlined.CreditCard
                                },
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = LassoTextPlaceholder,
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = paymentTypeLabel(payment),
                                style = MaterialTheme.typography.labelSmall,
                                color = LassoTextPlaceholder,
                                textAlign = TextAlign.End,
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedActionButton(
                    text = "Editar",
                    icon = {
                        Image(
                            painter = painterResource(Res.drawable.edit_icon),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            contentScale = ContentScale.Fit,
                        )
                    },
                    borderColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.primary,
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                )
                OutlinedActionButton(
                    text = "Eliminar",
                    icon = {
                        Image(
                            painter = painterResource(Res.drawable.trash_can_icon),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            contentScale = ContentScale.Fit,
                        )
                    },
                    borderColor = LassoTertiary,
                    contentColor = LassoTertiary,
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

private fun paymentTypeLabel(payment: PaymentApiResponse): String =
    when (payment.paymentType) {
        "cash" -> "Efectivo"
        "transfer" -> "Transferencia"
        else -> "Tarjeta"
    }

private fun detailLineLabel(detail: SaleDetailApiResponse): String {
    detail.product?.name?.let { return it }
    detail.service?.name?.let { return it }
    return ""
}
