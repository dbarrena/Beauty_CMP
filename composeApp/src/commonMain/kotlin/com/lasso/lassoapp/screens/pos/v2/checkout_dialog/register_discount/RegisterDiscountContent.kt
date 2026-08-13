package com.lasso.lassoapp.screens.pos.v2.checkout_dialog.register_discount

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.payment_amount.SplitPaymentRow
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.payment_method.CheckoutPaymentMethodColors
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.payment_method.CheckoutPaymentMethodTokens
import com.lasso.lassoapp.screens.pos.v2.toPosMoneyString
import com.lasso.lassoapp.ui.theme.LassoPrimary
import com.lasso.lassoapp.utils.parseCurrency
import lassoapp.composeapp.generated.resources.Res
import lassoapp.composeapp.generated.resources.sales_icon

@Composable
internal fun RegisterDiscountContent(
    totalPrice: Double,
    discountAmount: Double?,
    onBack: () -> Unit,
    onClose: () -> Unit,
    onRegisterDiscount: (Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    val safeTotalPrice = totalPrice.coerceAtLeast(0.0)
    var discountFieldValue by remember(discountAmount) {
        mutableStateOf(
            discountAmount
                ?.takeIf { it > 0.0 }
                ?.toPosMoneyString()
                .orEmpty(),
        )
    }
    val discount = remember(discountFieldValue) { discountFieldValue.parseCurrency() }
    val isAboveTotal = discount > safeTotalPrice
    val appliedDiscount = discount.coerceIn(0.0, safeTotalPrice)
    val payableTotal = (safeTotalPrice - appliedDiscount).coerceAtLeast(0.0)

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
                    text = "Aplicar Descuento",
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
                    text = "$${payableTotal.toPosMoneyString()}",
                    color = CheckoutPaymentMethodTokens.amountColor,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 48.sp,
                    letterSpacing = 0.35.sp,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SplitPaymentRow(
                        label = "Descuento",
                        circleColor = CheckoutPaymentMethodColors.tarjetaCreditoCircle,
                        icon = Res.drawable.sales_icon,
                        value = discountFieldValue,
                        onValueChange = { discountFieldValue = it },
                    )

                    if (isAboveTotal) {
                        Text(
                            text = "El descuento no puede ser mayor al total.",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Button(
                onClick = { onRegisterDiscount(appliedDiscount) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(20.dp),
                enabled = discountFieldValue.isNotEmpty() && !isAboveTotal,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LassoPrimary,
                    contentColor = Color.White,
                ),
            ) {
                Text(
                    text = "Confirmar Descuento",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.31).sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
