package com.lasso.lassoapp.screens.pos.v2

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.screens.pos.SelectedPosItem

/** Top corners only so the sheet sits flush with the bottom edge like a standard bottom sheet. */
private val PosBottomSheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)

@Composable
fun PosBottomSheet(
    selectedItems: List<SelectedPosItem>,
    totalPrice: Double,
    onClear: () -> Unit,
    onLineClick: (SelectedPosItem) -> Unit,
    onRemove: (SelectedPosItem) -> Unit,
    onCheckout: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color(0x1A353D3C),
                shape = PosBottomSheetShape,
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = PosBottomSheetShape,
            ),
        shape = PosBottomSheetShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
        ) {
            PosCart(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                selectedItems = selectedItems,
                onClear = onClear,
                onLineClick = onLineClick,
                onRemove = onRemove,
            )

            HorizontalDivider()

            PosCheckoutBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                totalPrice = totalPrice,
                onCheckout = onCheckout,
                enabled = enabled,
            )
        }
    }
}
