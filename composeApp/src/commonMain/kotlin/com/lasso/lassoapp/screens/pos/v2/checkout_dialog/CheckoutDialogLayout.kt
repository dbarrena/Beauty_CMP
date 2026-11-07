package com.lasso.lassoapp.screens.pos.v2.checkout_dialog

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.screens.pos.v2.toPosMoneyString
import com.lasso.lassoapp.screens.utils.AdaptiveScrollContent
import com.lasso.lassoapp.screens.utils.FittedAmountText

private val LocalCheckoutHeaderProgress = staticCompositionLocalOf<Float?> { null }

@Composable
internal fun CheckoutHeaderTransition(showBack: Boolean, content: @Composable () -> Unit) {
    val progress by animateFloatAsState(
        targetValue = if (showBack) 1f else 0f,
        animationSpec = tween(300),
        label = "Checkout title position",
    )
    CompositionLocalProvider(LocalCheckoutHeaderProgress provides progress, content = content)
}

@Composable
internal fun CheckoutHeader(
    total: Double,
    title: String = "Registrar Venta",
    summary: (@Composable () -> Unit)? = null,
) {
    val progress = LocalCheckoutHeaderProgress.current ?: 1f
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            title,
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 48.dp * (1f - progress))
                .heightIn(min = 48.dp - 20.dp * progress)
                .wrapContentHeight(Alignment.CenterVertically),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 28.sp,
            letterSpacing = (-0.45).sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Total a cobrar",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        FittedAmountText(
            text = "$${total.toPosMoneyString()}",
            color = MaterialTheme.colorScheme.onSurface,
            style =
                MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 48.sp,
                    lineHeight = 48.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = .35.sp,
                    textAlign = TextAlign.Center,
                ),
        )
        if (summary != null) {
            Spacer(Modifier.height(8.dp))
            summary()
        }
    }
}

@Composable
internal fun CheckoutDialogLayout(
    onClose: () -> Unit,
    header: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    actions: (@Composable () -> Unit)? = null,
    body: @Composable ColumnScope.() -> Unit,
) {
    val progress = LocalCheckoutHeaderProgress.current ?: if (onBack != null) 1f else 0f
    CompositionLocalProvider(LocalCheckoutHeaderProgress provides progress) {
        Column(modifier.fillMaxWidth()) {
            Box(Modifier.fillMaxWidth().padding(top = 24.dp)) {
                Box(
                    Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                        .padding(top = 48.dp * progress, bottom = 8.dp)
                ) {
                    header()
                }
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (onBack != null) {
                        TextButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Volver")
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = onClose) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            AdaptiveScrollContent(
                modifier = Modifier.weight(1f, fill = false),
                header = {},
                actions = actions?.let { action ->
                    {
                        Box(Modifier.padding(horizontal = 24.dp).padding(top = 16.dp, bottom = 24.dp)) {
                            action()
                        }
                    }
                },
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                        .padding(bottom = if (actions == null) 24.dp else 0.dp),
                    content = body,
                )
            }
        }
    }
}
