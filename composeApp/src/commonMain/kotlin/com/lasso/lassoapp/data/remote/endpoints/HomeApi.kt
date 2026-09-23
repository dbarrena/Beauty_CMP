package com.lasso.lassoapp.data.remote.endpoints

import com.lasso.lassoapp.data.remote.LASSO_API_URL
import com.lasso.lassoapp.data.remote.bodyOrThrow
import com.lasso.lassoapp.data.remote.recoverWith
import com.lasso.lassoapp.model.Home
import io.ktor.client.HttpClient
import io.ktor.client.request.get

interface HomeApi {
    suspend fun getHome(
        startMonthEpoch: Long,
        endMonthEpoch: Long,
        startDayEpoch: Long,
        endDayEpoch: Long,
        startWeekEpoch: Long,
        endWeekEpoch: Long,
    ): Home?
}

internal class KtorHomeApi(private val client: HttpClient) : HomeApi {
    override suspend fun getHome(
        startMonthEpoch: Long,
        endMonthEpoch: Long,
        startDayEpoch: Long,
        endDayEpoch: Long,
        startWeekEpoch: Long,
        endWeekEpoch: Long,
    ): Home? = recoverWith(null) {
        client.get(
            LASSO_API_URL + "home" +
                "?startMonthEpoch=$startMonthEpoch" +
                "&endMonthEpoch=$endMonthEpoch" +
                "&startDayEpoch=$startDayEpoch" +
                "&endDayEpoch=$endDayEpoch" +
                "&startWeekEpoch=$startWeekEpoch" +
                "&endWeekEpoch=$endWeekEpoch",
        ).bodyOrThrow()
    }
}
