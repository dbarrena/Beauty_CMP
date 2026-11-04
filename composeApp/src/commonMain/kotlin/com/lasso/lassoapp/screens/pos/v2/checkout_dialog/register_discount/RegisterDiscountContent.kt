package com.lasso.lassoapp.screens.pos.v2.checkout_dialog.register_discount

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.CheckoutDialogLayout
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.CheckoutHeader
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.payment_amount.SplitPaymentRow
import com.lasso.lassoapp.screens.pos.v2.checkout_dialog.payment_method.CheckoutPaymentMethodColors
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
    var discountFieldValue by
        remember(discountAmount) {
            mutableStateOf(discountAmount?.takeIf { it > 0.0 }?.toPosMoneyString().orEmpty())
        }
    val discount = remember(discountFieldValue) { discountFieldValue.parseCurrency() }
    val isAboveTotal = discount > safeTotalPrice
    val appliedDiscount = discount.coerceIn(0.0, safeTotalPrice)
    val payableTotal = (safeTotalPrice - appliedDiscount).coerceAtLeast(0.0)

    CheckoutDialogLayout(
        onClose = onClose,
        onBack = onBack,
        modifier = modifier,
        header = { CheckoutHeader(payableTotal, "Aplicar Descuento") },
        actions = {
            Button(
                onClick = { onRegisterDiscount(appliedDiscount) },
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                shape = RoundedCornerShape(20.dp),
                enabled = discountFieldValue.isNotEmpty() && !isAboveTotal,
                colors =
                    ButtonDefaults.buttonColors(
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
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SplitPaymentRow(
                label = "Descuento",
                circleColor = CheckoutPaymentMethodColors.tarjetaCreditoCircle,
                icon = Res.drawable.sales_icon,
                value = discountFieldValue,
                onValueChange = { discountFieldValue = it },
            )
            if (isAboveTotal)
                Text(
                    "El descuento no puede ser mayor al total.",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                )
        }
    }
}
