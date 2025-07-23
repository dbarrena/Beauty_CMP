package com.jetbrains.kmpapp.screens.pos

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jetbrains.kmpapp.model.BeautyItem

@Composable
fun PosBeautyItem(service: BeautyItem, onBeautyItemClicked: (BeautyItem) -> Unit){
    Card(
        modifier = Modifier.padding(12.dp),
        onClick = { onBeautyItemClicked(service) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
        ) {
            Text(service.name)
            Spacer(modifier = Modifier.weight(1f))
            Text("$${service.price}")
        }
    }
}