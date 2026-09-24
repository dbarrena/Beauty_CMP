package com.lasso.lassoapp.data.remote.endpoints

import com.lasso.lassoapp.data.remote.LASSO_API_URL
import com.lasso.lassoapp.data.remote.bodyOrThrow
import com.lasso.lassoapp.data.remote.recoverWith
import com.lasso.lassoapp.model.MessageResponse
import com.lasso.lassoapp.model.Sale
import com.lasso.lassoapp.model.SaleApiResponse
import com.lasso.lassoapp.model.SaleDetailEditApiRequest
import com.lasso.lassoapp.model.SaleEditApiRequest
import com.lasso.lassoapp.model.SaleEditDateApiRequest
import com.lasso.lassoapp.model.SalePaymentsEditApiRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

private const val SALES_URL = LASSO_API_URL + "sales/"

interface SalesApi {
    suspend fun getSalesBetweenDates(start: Long, end: Long): List<SaleApiResponse>
    suspend fun registerSale(sale: Sale): SaleApiResponse
    suspend fun getSale(id: Int): SaleApiResponse?
    suspend fun editSale(saleId: Int, request: SaleEditApiRequest): String
    suspend fun editSalePayments(saleId: Int, request: SalePaymentsEditApiRequest): String
    suspend fun editSaleDate(saleEditDateRequest: SaleEditDateApiRequest): String?
    suspend fun editSaleDetail(saleDetailEditApiRequest: SaleDetailEditApiRequest): String?
    suspend fun deleteSale(saleId: Int): String?
    suspend fun deleteSaleDetail(saleDetailId: Int): String?
}

internal class KtorSalesApi(private val client: HttpClient) : SalesApi {
    override suspend fun getSalesBetweenDates(start: Long, end: Long): List<SaleApiResponse> =
        recoverWith(emptyList()) {
            client.get(SALES_URL + "sales-between?startEpoch=$start&endEpoch=$end").bodyOrThrow()
        }

    override suspend fun registerSale(sale: Sale): SaleApiResponse =
        client.post(SALES_URL + "new") {
            contentType(ContentType.Application.Json)
            setBody(sale.copy(partnerId = null))
        }.bodyOrThrow()

    override suspend fun getSale(id: Int): SaleApiResponse? = recoverWith(null) {
        client.get(SALES_URL + "get/$id").bodyOrThrow()
    }

    override suspend fun editSale(saleId: Int, request: SaleEditApiRequest): String =
        client.post(SALES_URL + "edit/$saleId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.bodyOrThrow<MessageResponse>().message

    override suspend fun editSalePayments(saleId: Int, request: SalePaymentsEditApiRequest): String =
        client.post(SALES_URL + "edit/$saleId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.bodyOrThrow<MessageResponse>().message

    override suspend fun editSaleDate(saleEditDateRequest: SaleEditDateApiRequest): String? =
        recoverWith(null) {
            client.post(SALES_URL + "edit/${saleEditDateRequest.saleId}") {
                contentType(ContentType.Application.Json)
                setBody(saleEditDateRequest)
            }.bodyOrThrow<MessageResponse>().message
        }

    override suspend fun editSaleDetail(
        saleDetailEditApiRequest: SaleDetailEditApiRequest,
    ): String? = client.post(SALES_URL + "edit/detail") {
        contentType(ContentType.Application.Json)
        setBody(saleDetailEditApiRequest)
    }.bodyOrThrow<MessageResponse>().message

    override suspend fun deleteSale(saleId: Int): String? = recoverWith(null) {
        client.delete(SALES_URL + "delete/$saleId").bodyOrThrow<MessageResponse>().message
    }

    override suspend fun deleteSaleDetail(saleDetailId: Int): String? = recoverWith(null) {
        client.delete(SALES_URL + "delete/detail/$saleDetailId")
            .bodyOrThrow<MessageResponse>().message
    }
}
