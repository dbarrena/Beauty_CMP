package com.lasso.lassoapp.screens.pos.v2.checkout_dialog.payment_method

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun CheckoutPaymentMethodRow(
    label: String,
    style: PaymentMethodStyle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(CheckoutPaymentMethodTokens.rowCornerRadius)
    val interactionSource = remember { MutableInteractionSource() }
    val rowModifier = modifier
        .fillMaxWidth()
        .height(style.rowStyle.rowHeight)
        .clip(shape)
        .then(
            if (style.rowStyle.borderColor != null) {
                Modifier.border(
                    width = style.rowStyle.borderWidth,
                    color = style.rowStyle.borderColor,
                    shape = shape,
                )
            } else {
                Modifier
            },
        )
        .background(style.rowStyle.rowBackground)
        .clickable(
            interactionSource = interactionSource,
            indication = ripple(),
            onClick = onClick,
        )
        .padding(horizontal = CheckoutPaymentMethodTokens.rowHorizontalPadding)

    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(CheckoutPaymentMethodTokens.iconCircleSize)
                    .clip(CircleShape)
                    .background(style.rowStyle.circleBackground),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(style.leadingIcon),
                    contentDescription = null,
                    modifier = Modifier.size(CheckoutPaymentMethodTokens.iconImageSize),
                    contentScale = ContentScale.Fit,
                )
            }
            Text(
                text = label,
                color = style.rowStyle.labelColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 24.sp,
                letterSpacing = (-0.31).sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false),
            )
        }
        Image(
            painter = painterResource(style.trailingChevron),
            contentDescription = null,
            modifier = Modifier.size(CheckoutPaymentMethodTokens.chevronSize),
            contentScale = ContentScale.Fit,
        )
    }
}

internal data class PaymentMethodStyle(
    val rowStyle: PaymentMethodRowStyle,
    val leadingIcon: DrawableResource,
    val trailingChevron: DrawableResource,
)
