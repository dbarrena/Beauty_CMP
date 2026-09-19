package com.lasso.lassoapp.data.remote.endpoints

import com.lasso.lassoapp.data.remote.LASSO_API_URL
import com.lasso.lassoapp.data.remote.bodyOrThrow
import com.lasso.lassoapp.data.remote.recoverWith
import com.lasso.lassoapp.model.CommissionCalculationResponse
import com.lasso.lassoapp.model.SalesByProductCategoryApiResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get

interface ReportsApi {
    suspend fun getSalesByProductCategory(
        start: Long,
        end: Long,
        categoryId: Int,
    ): SalesByProductCategoryApiResponse?

    suspend fun calculateCommissions(
        employeeId: Int,
        start: Long,
        end: Long,
    ): List<CommissionCalculationResponse>
}

internal class KtorReportsApi(private val client: HttpClient) : ReportsApi {
    override suspend fun getSalesByProductCategory(
        start: Long,
        end: Long,
        categoryId: Int,
    ): SalesByProductCategoryApiResponse? = recoverWith(null) {
        client.get(
            LASSO_API_URL + "reports/products-by-category" +
                "?startEpoch=$start&endEpoch=$end&categoryId=$categoryId",
        ).bodyOrThrow()
    }

    override suspend fun calculateCommissions(
        employeeId: Int,
        start: Long,
        end: Long,
    ): List<CommissionCalculationResponse> = recoverWith(emptyList()) {
        client.get(
            LASSO_API_URL + "commissions/calculate" +
                "?employeeId=$employeeId&startEpoch=$start&endEpoch=$end",
        ).bodyOrThrow()
    }
}
