package com.lasso.lassoapp.screens.sales.v2

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.lasso.lassoapp.model.PaymentApiResponse
import com.lasso.lassoapp.model.SaleApiResponse
import com.lasso.lassoapp.ui.theme.LightLassoColorScheme
import com.lasso.lassoapp.ui.theme.lassoTypography
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun SalesScreenPreview() {
    val mockSales = listOf(
        SaleApiResponse(
            id = 1,
            total = "$150.00",
            partnerId = 1,
            createdAt = 1731515234000L,
            updatedAt = null,
            saleDetails = emptyList(),
            payments = listOf(
                PaymentApiResponse(id = 1, total = "$150.00", paymentType = "cash")
            )
        ),
        SaleApiResponse(
            id = 2,
            total = "$350.50",
            partnerId = 1,
            createdAt = 1731515234000L,
            updatedAt = null,
            saleDetails = emptyList(),
            payments = listOf(
                PaymentApiResponse(id = 2, total = "$350.50", paymentType = "card")
            )
        )
    )

    val mockState = SalesScreenStateV2(
        total = 500.50,
        sales = mockSales,
        isLoading = false,
        periodFilter = SalesPeriodFilter.Today
    )

    MaterialTheme(
        colorScheme = LightLassoColorScheme,
        typography = lassoTypography()
    ) {
        SalesScreenContentV2(
            state = mockState,
            onBack = {},
            onReloadSales = {},
            onClearSelectedSale = {},
            onLoadForPeriod = {},
            onApplyCustomDateRange = { _, _ -> },
            onSetSelectedSale = {},
            onDeleteSale = {}
        )
    }
}
