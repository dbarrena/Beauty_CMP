package com.lasso.lassoapp.data.remote.endpoints

import com.lasso.lassoapp.data.remote.LASSO_API_URL
import com.lasso.lassoapp.data.remote.ApiErrorResponse
import com.lasso.lassoapp.data.remote.bodyOrThrow
import com.lasso.lassoapp.model.Login
import com.lasso.lassoapp.model.LoginResponse
import com.lasso.lassoapp.model.MessageResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

interface AuthApi {
    suspend fun login(login: Login): LoginResponse
    suspend fun logout()
}

internal class KtorAuthApi(private val client: HttpClient) : AuthApi {
    override suspend fun login(login: Login): LoginResponse {
        val response = client.post(LASSO_API_URL + "auth/login") {
            contentType(ContentType.Application.Json)
            setBody(login)
        }
        if (response.status == HttpStatusCode.Unauthorized || response.status == HttpStatusCode.BadRequest) {
            val error = runCatching { response.body<ApiErrorResponse>().error }.getOrNull()
            return LoginResponse(error = error ?: "Correo o contraseña incorrectos.")
        }
        return response.bodyOrThrow()
    }

    override suspend fun logout() {
        client.post(LASSO_API_URL + "auth/logout").bodyOrThrow<MessageResponse>()
    }
}
