package com.lasso.lassoapp.screens.sales.v2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

/**
 * Figma-aligned sales history UI (summary, payment breakdown, period chips, rich cards).
 * Legacy fallback: `com.lasso.lassoapp.screens.sales.SalesScreen` + `SalesScreenViewModel`.
 */
@Composable
fun SalesScreenV2(onBack: () -> Unit = {}) {
    val viewModel = koinViewModel<SalesScreenViewModelV2>()
    val state by viewModel.state.collectAsState()

    SalesScreenContentV2(
        state = state,
        onBack = onBack,
        onReloadSales = viewModel::reloadSales,
        onClearSelectedSale = viewModel::clearSelectedSale,
        onLoadForPeriod = viewModel::loadForPeriod,
        onApplyCustomDateRange = viewModel::applyCustomDateRange,
        onSetSelectedSale = viewModel::setSelectedSale,
        onDeleteSale = viewModel::deleteSale,
    )
}
