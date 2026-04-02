package com.lasso.lassoapp.screens.pos.v2.checkout_dialog.payment_method

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.ui.theme.LassoPrimary
import com.lasso.lassoapp.ui.theme.LassoTextMuted
import com.lasso.lassoapp.ui.theme.LassoTextPrimary

internal object CheckoutPaymentMethodTokens {
    val titleColor: Color = LassoTextPrimary
    val subtitleColor: Color = LassoTextMuted
    val amountColor: Color = LassoTextPrimary
    val defaultRowLabelColor: Color = LassoTextPrimary
    val multiplesRowLabelColor: Color = LassoPrimary
    val closeIconTint: Color = LassoTextMuted

    val cardCornerRadius: Dp = 24.dp
    val rowCornerRadius: Dp = 16.dp
    val rowHeightDefault: Dp = 80.dp
    val rowHeightMultiples: Dp = 83.dp
    val iconCircleSize: Dp = 48.dp
    val iconImageSize: Dp = 24.dp
    val chevronSize: Dp = 20.dp
    val rowHorizontalPadding: Dp = 16.dp
    val multiplesBorderWidth: Dp = 1.5.dp
    val contentPadding: Dp = 24.dp
    val gapHeaderToRows: Dp = 24.dp
    val gapBetweenRows: Dp = 12.dp
}

internal data class PaymentMethodRowStyle(
    val rowBackground: Color,
    val circleBackground: Color,
    val labelColor: Color,
    val borderColor: Color? = null,
    val borderWidth: Dp = CheckoutPaymentMethodTokens.multiplesBorderWidth,
    val rowHeight: Dp = CheckoutPaymentMethodTokens.rowHeightDefault,
)

/** Figma row/circle fills shared by picker rows and split-payment icon circles. */
internal object CheckoutPaymentMethodColors {
    val efectivoRow = Color(0xFFE3F8F4)
    val efectivoCircle = Color(0xFFB3E5FC)
    val acreditoRow = Color(0xFFE8F5E9)
    val acreditoCircle = Color(0xFFA5D6A7)
    val tarjetaCreditoRow = Color(0xFFFFF8E1)
    val tarjetaCreditoCircle = Color(0xFFFFE082)
    val tarjetaDebitoRow = Color(0xFFF3E5F5)
    val tarjetaDebitoCircle = Color(0xFFCE93D8)
    val transferenciaRow = Color(0xFFFFE0B2)
    val transferenciaCircle = Color(0xFFFFB74D)
    val multiplesRow = Color(0xFFE0F7FA)
    val multiplesCircle = Color(0xFF80DEEA)
}
