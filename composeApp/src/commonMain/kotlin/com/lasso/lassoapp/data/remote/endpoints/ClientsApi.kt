package com.lasso.lassoapp.data.remote.endpoints

import com.lasso.lassoapp.data.remote.LASSO_API_URL
import com.lasso.lassoapp.data.remote.bodyOrThrow
import com.lasso.lassoapp.model.Client
import com.lasso.lassoapp.model.ClientWriteRequest
import com.lasso.lassoapp.model.normalized
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

interface ClientsApi {
    suspend fun getClients(): List<Client>
    suspend fun registerClient(request: ClientWriteRequest): Client
    suspend fun editClient(id: Int, request: ClientWriteRequest): Client
}

internal class KtorClientsApi(private val client: HttpClient) : ClientsApi {
    override suspend fun getClients(): List<Client> =
        client.get(LASSO_API_URL + "clients/all").bodyOrThrow()

    override suspend fun registerClient(request: ClientWriteRequest): Client =
        client.post(LASSO_API_URL + "clients/new") {
            contentType(ContentType.Application.Json)
            setBody(request.normalized().copy(partnerId = null))
        }.bodyOrThrow()

    override suspend fun editClient(id: Int, request: ClientWriteRequest): Client =
        client.post(LASSO_API_URL + "clients/edit") {
            contentType(ContentType.Application.Json)
            setBody(request.normalized().copy(id = id, partnerId = null))
        }.bodyOrThrow()
}
