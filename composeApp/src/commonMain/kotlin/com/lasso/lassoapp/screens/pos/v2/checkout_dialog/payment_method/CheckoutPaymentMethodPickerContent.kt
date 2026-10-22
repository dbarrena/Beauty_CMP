package com.lasso.lassoapp.screens.pos.v2.checkout_dialog.payment_method

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.CheckoutDialogLayout
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.CheckoutHeader
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.CheckoutPaymentMethod
import com.lasso.lassoapp.screens.pos.v2.toPosMoneyString
import com.lasso.lassoapp.ui.theme.LassoSecondary
import com.lasso.lassoapp.ui.theme.LassoTextMuted
import lassoapp.composeapp.generated.resources.Res
import lassoapp.composeapp.generated.resources.checkout_chevron_right
import lassoapp.composeapp.generated.resources.checkout_chevron_right_teal
import lassoapp.composeapp.generated.resources.checkout_payment_acredito
import lassoapp.composeapp.generated.resources.checkout_payment_efectivo
import lassoapp.composeapp.generated.resources.checkout_payment_multiples
import lassoapp.composeapp.generated.resources.checkout_payment_tarjeta_debito
import lassoapp.composeapp.generated.resources.checkout_payment_transferencia

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CheckoutPaymentMethodPickerContent(
    totalPrice: Double,
    discountAmount: Double? = null,
    title: String = "Registrar Venta",
    showDiscountAction: Boolean = true,
    onClose: () -> Unit,
    onMethodClicked: (checkoutPaymentMethod: CheckoutPaymentMethod) -> Unit,
    onRegisterDiscountClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val appliedDiscount = discountAmount?.coerceIn(0.0, totalPrice.coerceAtLeast(0.0)) ?: 0.0
    val payableTotal = (totalPrice - appliedDiscount).coerceAtLeast(0.0)

    CheckoutDialogLayout(
        onClose = onClose,
        modifier = modifier,
        header = {
            CheckoutHeader(
                payableTotal,
                title,
                summary =
                    if (appliedDiscount > 0.0)
                        ({
                            FlowRow(horizontalArrangement = Arrangement.Center) {
                                Text("Descuento ", color = LassoTextMuted, fontSize = 18.sp)
                                Text(
                                    "$${appliedDiscount.toPosMoneyString()}",
                                    color = LassoSecondary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        })
                    else null,
            )
        },
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(CheckoutPaymentMethodTokens.gapBetweenRows)
        ) {
            CheckoutPaymentMethodRow(
                label = "Efectivo",
                style =
                    PaymentMethodStyle(
                        rowStyle =
                            PaymentMethodRowStyle(
                                rowBackground = CheckoutPaymentMethodColors.efectivoRow,
                                circleBackground = CheckoutPaymentMethodColors.efectivoCircle,
                                labelColor = CheckoutPaymentMethodTokens.defaultRowLabelColor,
                            ),
                        leadingIcon = Res.drawable.checkout_payment_efectivo,
                        trailingChevron = Res.drawable.checkout_chevron_right,
                    ),
                onClick = { onMethodClicked(CheckoutPaymentMethod.Cash) },
            )
            CheckoutPaymentMethodRow(
                label = "Tarjeta de Débito",
                style =
                    PaymentMethodStyle(
                        rowStyle =
                            PaymentMethodRowStyle(
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
                style =
                    PaymentMethodStyle(
                        rowStyle =
                            PaymentMethodRowStyle(
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
                style =
                    PaymentMethodStyle(
                        rowStyle =
                            PaymentMethodRowStyle(
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
            if (showDiscountAction)
                CheckoutPaymentMethodRow(
                    label = "Aplicar Descuento",
                    style =
                        PaymentMethodStyle(
                            rowStyle =
                                PaymentMethodRowStyle(
                                    rowBackground = CheckoutPaymentMethodColors.acreditoRow,
                                    circleBackground = CheckoutPaymentMethodColors.acreditoCircle,
                                    labelColor = CheckoutPaymentMethodTokens.defaultRowLabelColor,
                                ),
                            leadingIcon = Res.drawable.checkout_payment_acredito,
                            trailingChevron = Res.drawable.checkout_chevron_right,
                        ),
                    onClick = onRegisterDiscountClicked,
                )
        }
    }
}
