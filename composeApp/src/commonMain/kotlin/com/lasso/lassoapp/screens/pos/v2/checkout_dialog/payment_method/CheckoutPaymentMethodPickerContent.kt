package com.lasso.lassoapp.screens.pos.v2.checkout_dialog.payment_method

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.CheckoutPaymentMethod
import com.lasso.lassoapp.screens.pos.v2.toPosMoneyString
import lassoapp.composeapp.generated.resources.Res
import lassoapp.composeapp.generated.resources.checkout_chevron_right
import lassoapp.composeapp.generated.resources.checkout_chevron_right_teal
import lassoapp.composeapp.generated.resources.checkout_payment_acredito
import lassoapp.composeapp.generated.resources.checkout_payment_efectivo
import lassoapp.composeapp.generated.resources.checkout_payment_multiples
import lassoapp.composeapp.generated.resources.checkout_payment_tarjeta_debito
import lassoapp.composeapp.generated.resources.checkout_payment_transferencia

@Composable
internal fun CheckoutPaymentMethodPickerContent(
    totalPrice: Double,
    onClose: () -> Unit,
    onMethodClicked: (checkoutPaymentMethod: CheckoutPaymentMethod) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CheckoutPaymentMethodTokens.contentPadding)
                .padding(top = 8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                    //.height(108.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Registrar Venta",
                    color = CheckoutPaymentMethodTokens.titleColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 28.sp,
                    letterSpacing = (-0.45).sp,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Total a cobrar",
                    color = CheckoutPaymentMethodTokens.subtitleColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 20.sp,
                    letterSpacing = (-0.15).sp,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$${totalPrice.toPosMoneyString()}",
                    color = CheckoutPaymentMethodTokens.amountColor,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 48.sp,
                    letterSpacing = 0.35.sp,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(CheckoutPaymentMethodTokens.gapHeaderToRows))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(
                        CheckoutPaymentMethodTokens.gapBetweenRows,
                    ),
                ) {
                    CheckoutPaymentMethodRow(
                        label = "Efectivo",
                        style = PaymentMethodStyle(
                            rowStyle = PaymentMethodRowStyle(
                                rowBackground = CheckoutPaymentMethodColors.efectivoRow,
                                circleBackground = CheckoutPaymentMethodColors.efectivoCircle,
                                labelColor = CheckoutPaymentMethodTokens.defaultRowLabelColor,
                            ),
                            leadingIcon = Res.drawable.checkout_payment_efectivo,
                            trailingChevron = Res.drawable.checkout_chevron_right,
                        ),
                        onClick = { onMethodClicked(CheckoutPaymentMethod.Cash) },
                    )
                /*CheckoutPaymentMethodRow(
                    label = "A Crédito",
                    style = PaymentMethodStyle(
                        rowStyle = PaymentMethodRowStyle(
                            rowBackground = CheckoutPaymentMethodColors.acreditoRow,
                            circleBackground = CheckoutPaymentMethodColors.acreditoCircle,
                            labelColor = CheckoutPaymentMethodTokens.defaultRowLabelColor,
                        ),
                        leadingIcon = Res.drawable.checkout_payment_acredito,
                        trailingChevron = Res.drawable.checkout_chevron_right,
                    ),
                    onClick = { onMethodClicked(CheckoutPaymentMethod.Cash) },
                )*/
                /*CheckoutPaymentMethodRow(
                    label = "Tarjeta de Crédito",
                    style = PaymentMethodStyle(
                        rowStyle = PaymentMethodRowStyle(
                            rowBackground = CheckoutPaymentMethodColors.tarjetaCreditoRow,
                            circleBackground = CheckoutPaymentMethodColors.tarjetaCreditoCircle,
                            labelColor = CheckoutPaymentMethodTokens.defaultRowLabelColor,
                        ),
                        leadingIcon = Res.drawable.checkout_payment_tarjeta_credito,
                        trailingChevron = Res.drawable.checkout_chevron_right,
                    ),
                    onClick = noOp,
                )*/
                    CheckoutPaymentMethodRow(
                        label = "Tarjeta de Débito",
                        style = PaymentMethodStyle(
                            rowStyle = PaymentMethodRowStyle(
                                rowBackground = CheckoutPaymentMethodColors.tarjetaDebitoRow,
                                circleBackground = CheckoutPaymentMethodColors.tarjetaDebitoCircle,
                                labelColor = CheckoutPaymentMethodTokens.defaultRowLabelColor,
                            ),
                            leadingIcon = Res.drawable.checkout_payment_tarjeta_debito,
                            trailingChevron = Res.drawable.checkout_chevron_right,
                        ),
                        onClick = { onMethodClicked(CheckoutPaymentMethod.Card) },
                    )
                    CheckoutPaymentMethodRow(
                        label = "Transferencia",
                        style = PaymentMethodStyle(
                            rowStyle = PaymentMethodRowStyle(
                                rowBackground = CheckoutPaymentMethodColors.transferenciaRow,
                                circleBackground = CheckoutPaymentMethodColors.transferenciaCircle,
                                labelColor = CheckoutPaymentMethodTokens.defaultRowLabelColor,
                            ),
                            leadingIcon = Res.drawable.checkout_payment_transferencia,
                            trailingChevron = Res.drawable.checkout_chevron_right,
                        ),
                        onClick = { onMethodClicked(CheckoutPaymentMethod.Transfer) },
                    )
                    CheckoutPaymentMethodRow(
                        label = "Usar Múltiples Métodos",
                        style = PaymentMethodStyle(
                            rowStyle = PaymentMethodRowStyle(
                                rowBackground = CheckoutPaymentMethodColors.multiplesRow,
                                circleBackground = CheckoutPaymentMethodColors.multiplesCircle,
                                labelColor = CheckoutPaymentMethodTokens.multiplesRowLabelColor,
                                borderColor = CheckoutPaymentMethodTokens.multiplesRowLabelColor,
                                rowHeight = CheckoutPaymentMethodTokens.rowHeightMultiples,
                            ),
                            leadingIcon = Res.drawable.checkout_payment_multiples,
                            trailingChevron = Res.drawable.checkout_chevron_right_teal,
                        ),
                        onClick = { onMethodClicked(CheckoutPaymentMethod.Multiple) },
                    )
                }
            }
        }

        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cerrar",
                tint = CheckoutPaymentMethodTokens.closeIconTint,
            )
        }
    }
}
