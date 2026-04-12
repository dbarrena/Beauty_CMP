package com.lasso.lassoapp.screens.pos.v2.checkout_dialog.payment_amount

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.CheckoutDialogViewModelV2
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.CheckoutPaymentMethod
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.payment_method.CheckoutPaymentMethodColors
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.payment_method.CheckoutPaymentMethodTokens
import com.lasso.lassoapp.screens.pos.v2.toPosMoneyString
import com.lasso.lassoapp.ui.theme.LassoPrimary
import com.lasso.lassoapp.ui.theme.LassoSecondary
import com.lasso.lassoapp.ui.theme.LassoTextMuted
import com.lasso.lassoapp.ui.theme.LassoTextPlaceholder
import com.lasso.lassoapp.ui.theme.LassoTextPrimary
import lassoapp.composeapp.generated.resources.Res
import lassoapp.composeapp.generated.resources.checkout_payment_efectivo
import lassoapp.composeapp.generated.resources.checkout_payment_tarjeta_credito
import lassoapp.composeapp.generated.resources.checkout_payment_tarjeta_debito
import lassoapp.composeapp.generated.resources.checkout_payment_transferencia
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlin.text.iterator

@Composable
internal fun CheckoutSplitPaymentContent(
    totalPrice: Double,
    onBack: () -> Unit,
    onClose: () -> Unit,
    onRegisterSale: (payments: List<CheckoutDialogViewModelV2.PosPayment>) -> Unit,
    modifier: Modifier = Modifier,
    checkoutPaymentMethod: CheckoutPaymentMethod,
    isRegisteringSale: Boolean = false,
    registerSaleError: String? = null,
) {
    var efectivo by remember { mutableStateOf("") }
    var tarjetaDebito by remember { mutableStateOf("") }
    var transferencia by remember { mutableStateOf("") }

    val sumEntered = remember(efectivo, tarjetaDebito, transferencia) {
        listOf(efectivo, tarjetaDebito, transferencia).sumOf { parseMoney(it) }
    }
    val remaining = (totalPrice - sumEntered).coerceAtLeast(0.0)

    LaunchedEffect(checkoutPaymentMethod, totalPrice) {
        when (checkoutPaymentMethod) {
            CheckoutPaymentMethod.Cash -> {
                efectivo = totalPrice.toPosMoneyString()
            }
            CheckoutPaymentMethod.Card -> {
                tarjetaDebito = totalPrice.toPosMoneyString()
            }
            CheckoutPaymentMethod.Transfer -> {
                transferencia = totalPrice.toPosMoneyString()
            }
            else -> {}
        }
    }

    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onBack, modifier = Modifier.padding(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = LassoPrimary,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = "Volver",
                        color = LassoPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = (-0.31).sp,
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CheckoutPaymentMethodTokens.contentPadding),
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
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

                if (remaining != 0.0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "Restan ",
                            color = LassoTextMuted,
                            fontSize = 18.sp,
                            letterSpacing = (-0.44).sp,
                        )
                        Text(
                            text = "$${remaining.toPosMoneyString()}",
                            color = LassoSecondary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.44).sp,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    when (checkoutPaymentMethod) {
                        CheckoutPaymentMethod.Cash -> {
                            SplitPaymentRow(
                                label = "Efectivo",
                                circleColor = CheckoutPaymentMethodColors.efectivoCircle,
                                icon = Res.drawable.checkout_payment_efectivo,
                                value = efectivo,
                                onValueChange = { efectivo = it },
                            )
                        }

                        CheckoutPaymentMethod.Card -> {
                            SplitPaymentRow(
                                label = "Tarjeta de Débito",
                                circleColor = CheckoutPaymentMethodColors.tarjetaDebitoCircle,
                                icon = Res.drawable.checkout_payment_tarjeta_debito,
                                value = tarjetaDebito,
                                onValueChange = { tarjetaDebito = it },
                            )
                        }

                        CheckoutPaymentMethod.Transfer -> {
                            SplitPaymentRow(
                                label = "Transferencia",
                                circleColor = CheckoutPaymentMethodColors.transferenciaCircle,
                                icon = Res.drawable.checkout_payment_transferencia,
                                value = transferencia,
                                onValueChange = { transferencia = it },
                            )
                        }

                        CheckoutPaymentMethod.Multiple -> {
                            SplitPaymentRow(
                                label = "Efectivo",
                                circleColor = CheckoutPaymentMethodColors.efectivoCircle,
                                icon = Res.drawable.checkout_payment_efectivo,
                                value = efectivo,
                                onValueChange = { efectivo = it },
                            )

                            SplitPaymentRow(
                                label = "Tarjeta de Débito",
                                circleColor = CheckoutPaymentMethodColors.tarjetaCreditoCircle,
                                icon = Res.drawable.checkout_payment_tarjeta_credito,
                                value = tarjetaDebito,
                                onValueChange = { tarjetaDebito = it },
                            )

                            SplitPaymentRow(
                                label = "Transferencia",
                                circleColor = CheckoutPaymentMethodColors.transferenciaCircle,
                                icon = Res.drawable.checkout_payment_transferencia,
                                value = transferencia,
                                onValueChange = { transferencia = it },
                            )
                        }
                    }

                    /*SplitPaymentRow(
                        label = "Tarjeta de Débito",
                        circleColor = CheckoutPaymentMethodColors.tarjetaDebitoCircle,
                        icon = Res.drawable.checkout_payment_tarjeta_debito,
                        value = tarjetaDebito,
                        onValueChange = { tarjetaDebito = it },
                    )*/

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val payments = mutableListOf<CheckoutDialogViewModelV2.PosPayment>()


                            if (efectivo.isNotEmpty()) {
                                val total = parseMoney(efectivo)

                                payments.add(
                                    CheckoutDialogViewModelV2.PosPayment(
                                        paymentType = CheckoutPaymentMethod.Cash,
                                        total = total
                                    )
                                )
                            }

                            if (tarjetaDebito.isNotEmpty()) {
                                val total = parseMoney(tarjetaDebito)

                                payments.add(
                                    CheckoutDialogViewModelV2.PosPayment(
                                        paymentType = CheckoutPaymentMethod.Card,
                                        total = total
                                    )
                                )
                            }

                            if (transferencia.isNotEmpty()) {
                                val total = parseMoney(transferencia)

                                payments.add(
                                    CheckoutDialogViewModelV2.PosPayment(
                                        paymentType = CheckoutPaymentMethod.Transfer,
                                        total = total
                                    )
                                )
                            }

                            onRegisterSale(payments)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(20.dp),
                        enabled = remaining == 0.0 && !isRegisteringSale,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LassoPrimary,
                            contentColor = Color.White,
                        ),
                    ) {
                        Text(
                            text = if (isRegisteringSale) {
                                "Registrando…"
                            } else {
                                "Confirmar pago en ${checkoutPaymentMethod.display}"
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.31).sp,
                        )
                    }
                    registerSaleError?.let { message ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SplitPaymentRow(
    label: String,
    circleColor: Color,
    icon: DrawableResource,
    value: String,
    onValueChange: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(CheckoutPaymentMethodTokens.iconCircleSize)
                .clip(CircleShape)
                .background(circleColor),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(CheckoutPaymentMethodTokens.iconImageSize),
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = label,
                color = LassoTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 20.sp,
                letterSpacing = (-0.15).sp,
            )
            CheckoutSplitMoneyField(
                value = value,
                onValueChange = onValueChange,
            )
        }
    }
}

@Composable
private fun CheckoutSplitMoneyField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(20.dp)
    val textColor = if (value.isEmpty()) LassoTextPlaceholder else LassoTextPrimary
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(shape)
            .background(Color(0xFFF8F8FA))
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "$",
                color = LassoTextMuted,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
            BasicTextField(
                value = value,
                onValueChange = { onValueChange(sanitizeMoneyInput(it)) },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = textColor,
                    letterSpacing = (-0.31).sp,
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done,
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

private fun sanitizeMoneyInput(input: String): String {
    if (input.isEmpty()) return ""
    val sb = StringBuilder()
    var dotSeen = false
    for (c in input) {
        when {
            c.isDigit() -> sb.append(c)
            c == '.' && !dotSeen -> {
                sb.append('.')
                dotSeen = true
            }
        }
    }
    val parts = sb.toString().split('.')
    if (parts.size == 2 && parts[1].length > 2) {
        return parts[0] + "." + parts[1].take(2)
    }
    return sb.toString()
}

private fun parseMoney(s: String): Double =
    s.replace(",", "").toDoubleOrNull() ?: 0.0
