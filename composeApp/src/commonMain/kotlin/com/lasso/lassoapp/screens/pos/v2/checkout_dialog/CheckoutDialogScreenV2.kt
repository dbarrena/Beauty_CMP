package com.lasso.lassoapp.screens.pos.v2.checkout_dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lasso.lassoapp.screens.pos.SelectedPosItem
import com.lasso.lassoapp.screens.pos.v2.toPosMoneyString
import com.lasso.lassoapp.ui.theme.LassoOutlineHairline
import lassoapp.composeapp.generated.resources.Res
import lassoapp.composeapp.generated.resources.checkout_chevron_right
import lassoapp.composeapp.generated.resources.checkout_chevron_right_teal
import lassoapp.composeapp.generated.resources.checkout_payment_acredito
import lassoapp.composeapp.generated.resources.checkout_payment_efectivo
import lassoapp.composeapp.generated.resources.checkout_payment_multiples
import lassoapp.composeapp.generated.resources.checkout_payment_tarjeta_debito
import lassoapp.composeapp.generated.resources.checkout_payment_transferencia

/**
 * Figma "Registrar Venta" payment method picker ([node 1:1477](https://www.figma.com/design/yVgyoT7vGZRaLXyF3p6TK7/Lasso-App?node-id=1-1477)).
 * Row actions are intentionally no-ops until follow-up checkout steps exist.
 */
@Composable
fun CheckoutDialogScreenV2(
    totalPrice: Double,
    @Suppress("UNUSED_PARAMETER")
    items: List<SelectedPosItem>,
    onDismiss: (Boolean) -> Unit,
) {
    Dialog(onDismissRequest = { onDismiss(false) }) {
        Card(
            modifier = Modifier
                .widthIn(max = 400.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(CheckoutPaymentMethodTokens.cardCornerRadius),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp,
                pressedElevation = 10.dp,
                focusedElevation = 10.dp,
                hoveredElevation = 10.dp,
                draggedElevation = 10.dp,
                disabledElevation = 0.dp,
            ),
            border = BorderStroke(1.dp, LassoOutlineHairline),
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(CheckoutPaymentMethodTokens.contentPadding)
                        .padding(top = 8.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(108.dp),
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

                    val noOp: () -> Unit = { }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(
                            CheckoutPaymentMethodTokens.gapBetweenRows,
                        ),
                    ) {
                        CheckoutPaymentMethodRow(
                            label = "Efectivo",
                            style = PaymentMethodStyle(
                                rowStyle = PaymentMethodRowStyle(
                                    rowBackground = ColorEfectivoRow,
                                    circleBackground = ColorEfectivoCircle,
                                    labelColor = CheckoutPaymentMethodTokens.defaultRowLabelColor,
                                ),
                                leadingIcon = Res.drawable.checkout_payment_efectivo,
                                trailingChevron = Res.drawable.checkout_chevron_right,
                            ),
                            onClick = noOp,
                        )
                        CheckoutPaymentMethodRow(
                            label = "A Crédito",
                            style = PaymentMethodStyle(
                                rowStyle = PaymentMethodRowStyle(
                                    rowBackground = ColorAcreditoRow,
                                    circleBackground = ColorAcreditoCircle,
                                    labelColor = CheckoutPaymentMethodTokens.defaultRowLabelColor,
                                ),
                                leadingIcon = Res.drawable.checkout_payment_acredito,
                                trailingChevron = Res.drawable.checkout_chevron_right,
                            ),
                            onClick = noOp,
                        )
                        /*CheckoutPaymentMethodRow(
                            label = "Tarjeta de Crédito",
                            style = PaymentMethodStyle(
                                rowStyle = PaymentMethodRowStyle(
                                    rowBackground = ColorTarjetaCreditoRow,
                                    circleBackground = ColorTarjetaCreditoCircle,
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
                                    rowBackground = ColorTarjetaDebitoRow,
                                    circleBackground = ColorTarjetaDebitoCircle,
                                    labelColor = CheckoutPaymentMethodTokens.defaultRowLabelColor,
                                ),
                                leadingIcon = Res.drawable.checkout_payment_tarjeta_debito,
                                trailingChevron = Res.drawable.checkout_chevron_right,
                            ),
                            onClick = noOp,
                        )
                        CheckoutPaymentMethodRow(
                            label = "Transferencia",
                            style = PaymentMethodStyle(
                                rowStyle = PaymentMethodRowStyle(
                                    rowBackground = ColorTransferenciaRow,
                                    circleBackground = ColorTransferenciaCircle,
                                    labelColor = CheckoutPaymentMethodTokens.defaultRowLabelColor,
                                ),
                                leadingIcon = Res.drawable.checkout_payment_transferencia,
                                trailingChevron = Res.drawable.checkout_chevron_right,
                            ),
                            onClick = noOp,
                        )
                        CheckoutPaymentMethodRow(
                            label = "Usar Múltiples Métodos",
                            style = PaymentMethodStyle(
                                rowStyle = PaymentMethodRowStyle(
                                    rowBackground = ColorMultiplesRow,
                                    circleBackground = ColorMultiplesCircle,
                                    labelColor = CheckoutPaymentMethodTokens.multiplesRowLabelColor,
                                    borderColor = CheckoutPaymentMethodTokens.multiplesRowLabelColor,
                                    rowHeight = CheckoutPaymentMethodTokens.rowHeightMultiples,
                                ),
                                leadingIcon = Res.drawable.checkout_payment_multiples,
                                trailingChevron = Res.drawable.checkout_chevron_right_teal,
                            ),
                            onClick = noOp,
                        )
                    }
                }

                IconButton(
                    onClick = { onDismiss(false) },
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
    }
}

private val ColorEfectivoRow = androidx.compose.ui.graphics.Color(0xFFE3F8F4)
private val ColorEfectivoCircle = androidx.compose.ui.graphics.Color(0xFFB3E5FC)
private val ColorAcreditoRow = androidx.compose.ui.graphics.Color(0xFFE8F5E9)
private val ColorAcreditoCircle = androidx.compose.ui.graphics.Color(0xFFA5D6A7)
private val ColorTarjetaCreditoRow = androidx.compose.ui.graphics.Color(0xFFFFF8E1)
private val ColorTarjetaCreditoCircle = androidx.compose.ui.graphics.Color(0xFFFFE082)
private val ColorTarjetaDebitoRow = androidx.compose.ui.graphics.Color(0xFFF3E5F5)
private val ColorTarjetaDebitoCircle = androidx.compose.ui.graphics.Color(0xFFCE93D8)
private val ColorTransferenciaRow = androidx.compose.ui.graphics.Color(0xFFFFE0B2)
private val ColorTransferenciaCircle = androidx.compose.ui.graphics.Color(0xFFFFB74D)
private val ColorMultiplesRow = androidx.compose.ui.graphics.Color(0xFFE0F7FA)
private val ColorMultiplesCircle = androidx.compose.ui.graphics.Color(0xFF80DEEA)
