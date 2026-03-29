package com.lasso.lassoapp.screens.pos.v2.checkout

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
