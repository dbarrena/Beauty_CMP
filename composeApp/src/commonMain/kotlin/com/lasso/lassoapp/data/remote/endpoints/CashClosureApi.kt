package com.lasso.lassoapp.data.remote.endpoints

import com.lasso.lassoapp.data.remote.LASSO_API_URL
import com.lasso.lassoapp.data.remote.bodyOrThrow
import com.lasso.lassoapp.data.remote.recoverWith
import com.lasso.lassoapp.model.CashClosure
import com.lasso.lassoapp.model.CashClosureRecordsResponse
import com.lasso.lassoapp.model.CreateCashClosureRequest
import com.lasso.lassoapp.model.MessageResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

interface CashClosureApi {
    suspend fun getOpenCashClosure(): CashClosure?
    suspend fun createCashClosure(): String?
    suspend fun getCashClosureRecords(): List<CashClosureRecordsResponse>
}

internal class KtorCashClosureApi(private val client: HttpClient) : CashClosureApi {
    override suspend fun getOpenCashClosure(): CashClosure? = recoverWith(null) {
        client.get(LASSO_API_URL + "cash_closure/open").bodyOrThrow()
    }

    override suspend fun createCashClosure(): String? = recoverWith(null) {
        client.post(LASSO_API_URL + "cash_closure/create") {
            contentType(ContentType.Application.Json)
            setBody(CreateCashClosureRequest())
        }.bodyOrThrow<MessageResponse>().message
    }

    override suspend fun getCashClosureRecords(): List<CashClosureRecordsResponse> =
        recoverWith(emptyList()) {
            client.get(LASSO_API_URL + "cash_closure/all").bodyOrThrow()
        }
}
