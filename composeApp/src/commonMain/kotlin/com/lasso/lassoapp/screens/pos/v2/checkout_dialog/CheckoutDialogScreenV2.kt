package com.lasso.lassoapp.screens.pos.v2.checkout_dialog

import androidx.compose.runtime.Composable
import com.lasso.lassoapp.screens.pos.SelectedPosItem

/**
 * Entry point for POS v2 checkout. Delegates to [CheckoutFlowDialog] (picker → split payment → success).
 */
@Composable
fun CheckoutDialogScreenV2(
    totalPrice: Double,
    items: List<SelectedPosItem>,
    onDismiss: (Boolean) -> Unit,
) {
    CheckoutFlowDialog(
        totalPrice = totalPrice,
        items = items,
        onDismiss = onDismiss,
    )
}
