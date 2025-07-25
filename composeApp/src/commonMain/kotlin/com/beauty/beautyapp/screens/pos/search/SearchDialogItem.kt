package com.beauty.beautyapp.screens.pos.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.beauty.beautyapp.model.BeautyItem
import com.beauty.beautyapp.model.Product

@Composable
fun SearchDialogItem(beautyItem: BeautyItem, onClick: (beautyItem: BeautyItem) -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = {
            onClick(beautyItem)
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = beautyItem.name,
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "$${beautyItem.price}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            if (beautyItem is Product) {
                Text(
                    text = "Categoria: ${beautyItem.category}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}