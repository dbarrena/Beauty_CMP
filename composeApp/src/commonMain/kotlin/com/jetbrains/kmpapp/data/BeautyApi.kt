package com.jetbrains.kmpapp.data

import com.jetbrains.kmpapp.model.Service
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.utils.io.CancellationException

interface BeautyApi {
    suspend fun getServices(): List<Service>
}

class KtorBeautyApi(private val client: HttpClient) : BeautyApi {
    companion object {
        private const val API_URL =
            "http://192.168.68.109:3000/api/"
    }

    override suspend fun getServices(): List<Service> {
        return try {
            println("KtorBeautyApi: getServices")
            client.get(API_URL + "services/all").body()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()

            emptyList()
        }
    }
}