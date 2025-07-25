package com.beauty.beautyapp.screens.pos

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.beauty.beautyapp.model.BeautyItem

@Composable
fun PosBeautyItem(service: BeautyItem, onBeautyItemClicked: (BeautyItem) -> Unit) {
    Card(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5) // light gray or any color you like
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = { onBeautyItemClicked(service) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
        ) {
            Text(service.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$${service.price}",
                //color = Color.Dar,
                fontWeight = FontWeight.Bold
            )
        }
    }
}