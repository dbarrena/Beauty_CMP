package com.beauty.beautyapp.screens.cash_closure.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.beauty.beautyapp.screens.utils.ElevatedInfoCard
import com.beauty.beautyapp.screens.utils.FullScreenLoading
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CashClosureScreen() {
    val viewModel = koinViewModel<CashClosureViewModel>()

    CashClosureContent(viewModel)
}

@Composable
fun CashClosureContent(viewModel: CashClosureViewModel) {
    val state = viewModel.state.collectAsState()

    if (state.value.isLoading) {
        FullScreenLoading()
    } else {
        state.value.openCashClosure?.let { cashClosure ->
            Column(
                modifier = Modifier.padding(8.dp).fillMaxSize()
            ) {
                cashClosure.openPayments.forEach {
                    val title = when (it.paymentType) {
                        "cash" -> "Efectivo"
                        "transfer" -> "Transferencia"
                        else -> "Tarjeta"
                    }

                    println(it.toString())

                    ElevatedInfoCard(
                        title = title,
                        description = it.total
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                CashClosureButton(state.value.isLoading) {
                    viewModel.createCashClosure()
                }
            }
        }
    }
}

@Composable
private fun CashClosureButton(isLoading: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        elevation = ButtonDefaults.buttonElevation(6.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.height(24.dp),
                strokeWidth = 2.dp
            )
        } else {
            Text(
                "Crear corte de caja",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}
