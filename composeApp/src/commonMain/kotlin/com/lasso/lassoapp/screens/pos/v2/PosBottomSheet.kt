package com.lasso.lassoapp.screens.pos.v2

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.screens.pos.SelectedPosItem

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
    Card(
        modifier = modifier
            //.padding(bottom = 16.dp)
            .fillMaxWidth()
            .shadow(
                elevation = 50.dp,
                spotColor = Color(0x40000000),
                ambientColor = Color(0x40000000)
            )
            .border(
                width = 0.75394.dp,
                color = Color(0x1A353D3C),
                shape = RoundedCornerShape(size = 24.dp)
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(size = 24.dp)
            )
            .padding(horizontal = 0.dp, vertical = 0.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        PosCart(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            selectedItems = selectedItems,
            onClear = onClear,
            onLineClick = onLineClick,
            onRemove = onRemove,
        )
        PosCheckoutBar(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            totalPrice = totalPrice,
            onCheckout = onCheckout,
            enabled = enabled,
        )
    }
}
