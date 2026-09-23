package com.lasso.lassoapp.data.remote

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import io.ktor.utils.io.CancellationException
import kotlinx.serialization.Serializable

internal const val LASSO_API_URL = "https://cdn.dbxprts.com:3000/lasso/api/"

@Serializable
@PublishedApi
internal data class ApiErrorResponse(val error: String? = null, val message: String? = null)

class LassoApiException(message: String, val statusCode: Int) : Exception(message)

internal suspend inline fun <reified T> HttpResponse.bodyOrThrow(): T {
    if (status.isSuccess()) return body()
    val apiError = runCatching { body<ApiErrorResponse>() }.getOrNull()
    throw LassoApiException(
        message = apiError?.error ?: apiError?.message ?: "Error de servidor (${status.value})",
        statusCode = status.value,
    )
}

internal suspend inline fun <T> recoverWith(fallback: T, block: () -> T): T = try {
    block()
} catch (error: Exception) {
    if (error is CancellationException) throw error
    error.printStackTrace()
    fallback
}
