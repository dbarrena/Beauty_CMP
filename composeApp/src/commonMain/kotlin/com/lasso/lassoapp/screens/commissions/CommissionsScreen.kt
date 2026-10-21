package com.lasso.lassoapp.screens.commissions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.lasso.lassoapp.utils.ComisionesPdfUtil
import com.lasso.lassoapp.utils.rememberComisionesPdfSharer
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CommissionsScreen() {
    val viewModel = koinViewModel<CommissionsViewModel>()
    val state by viewModel.state.collectAsState()
    val sharePdf = rememberComisionesPdfSharer()
    val scope = rememberCoroutineScope()
    var isExporting by remember { mutableStateOf(false) }
    var exportError by remember { mutableStateOf<String?>(null) }

    CommissionsContent(
        state = state,
        onSelectEmployee = viewModel::setSelectedEmployee,
        onLoadForPeriod = viewModel::loadForPeriod,
        onApplyCustomDateRange = viewModel::applyCustomDateRange,
        isExporting = isExporting,
        exportError = exportError,
        onSharePdf = {
            val calculation = state.commissions.singleOrNull { it.employee.id == state.selectedEmployee?.id }
            val start = state.reportPeriodStart
            val end = state.reportPeriodEnd
            if (!state.isLoading && !isExporting && calculation != null && start != null && end != null) {
                isExporting = true
                exportError = null
                scope.launch {
                    try {
                        val pdf = withContext(Dispatchers.Default) { ComisionesPdfUtil().generate(calculation, start, end) }
                        sharePdf(pdf)
                    } catch (error: CancellationException) {
                        throw error
                    } catch (error: Exception) {
                        exportError = "No se pudo compartir el PDF. Intenta de nuevo."
                    } finally {
                        isExporting = false
                    }
                }
            }
        },
    )
}
