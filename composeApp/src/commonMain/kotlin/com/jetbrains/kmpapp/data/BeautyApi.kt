package com.jetbrains.kmpapp.data

import com.jetbrains.kmpapp.model.Service
import com.jetbrains.kmpapp.model.Product
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.CancellationException

interface BeautyApi {
    suspend fun getServices(): List<Service>
    suspend fun getProducts(): List<Product>
    suspend fun registerProduct(product: Product): Product
}

class KtorBeautyApi(private val client: HttpClient) : BeautyApi {
    companion object {
        private const val API_URL =
            "http://192.168.68.106:3000/api/"
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

    override suspend fun getProducts(): List<Product> {
        return try {
            println("KtorBeautyApi: getProducts")
            client.get(API_URL + "products/all").body()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun registerProduct(product: Product): Product {
        return try {
            println("KtorBeautyApi: registerProduct")
            client.post(API_URL + "products/new") {
                contentType(ContentType.Application.Json)
                setBody(product)
            }.body<Product>()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            throw e // or return a sensible default, but not emptyList()
        }
    }
}