package com.lasso.lassoapp.screens.pos.v2.checkout_dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lasso.lassoapp.screens.pos.v2.toPosMoneyString
import com.lasso.lassoapp.screens.utils.DialogContentScaffold
import com.lasso.lassoapp.screens.utils.FittedAmountText

@Composable
internal fun CheckoutHeader(
    total: Double,
    title: String = "Registrar Venta",
    summary: (@Composable () -> Unit)? = null,
) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            title,
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
) = DialogContentScaffold(onClose, header, modifier, onBack, actions = actions, body = body)
