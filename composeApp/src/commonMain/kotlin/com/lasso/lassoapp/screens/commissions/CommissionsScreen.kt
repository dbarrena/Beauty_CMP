package com.lasso.lassoapp.screens.commissions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CommissionsScreen() {
    val viewModel = koinViewModel<CommissionsViewModel>()
    val state by viewModel.state.collectAsState()

    CommissionsContent(
        state = state,
        onSelectEmployee = viewModel::setSelectedEmployee,
        onLoadForPeriod = viewModel::loadForPeriod,
        onApplyCustomDateRange = viewModel::applyCustomDateRange
    )
}
