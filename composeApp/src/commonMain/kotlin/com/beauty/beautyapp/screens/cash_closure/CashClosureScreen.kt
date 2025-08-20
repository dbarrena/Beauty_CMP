package com.beauty.beautyapp.screens.cash_closure

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
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
            }
        }
    }
}