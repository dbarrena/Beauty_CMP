package com.beauty.beautyapp.data

import com.beauty.beautyapp.model.Service
import com.beauty.beautyapp.model.Product
import com.beauty.beautyapp.model.Sale
import com.beauty.beautyapp.model.SaleApiResponse
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
    suspend fun getSales(): List<SaleApiResponse>
    suspend fun registerProduct(product: Product): Product
    suspend fun registerService(product: Service): Service
    suspend fun registerSale(sale: Sale): Sale
}

class KtorBeautyApi(private val client: HttpClient) : BeautyApi {
    companion object {
        private const val API_URL =
            "http://192.168.68.105:3000/api/"
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

    override suspend fun getSales(): List<SaleApiResponse> {
        return try {
            println("KtorBeautyApi: getSales")
            client.get(API_URL + "sales/all").body()
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

    override suspend fun registerService(service: Service): Service {
        return try {
            println("KtorBeautyApi: registerService")
            client.post(API_URL + "services/new") {
                contentType(ContentType.Application.Json)
                setBody(service)
            }.body<Service>()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            throw e // or return a sensible default, but not emptyList()
        }
    }

    override suspend fun registerSale(sale: Sale): Sale {
        return try {
            println("KtorBeautyApi: registerSale")
            client.post(API_URL + "sales/new") {
                contentType(ContentType.Application.Json)
                setBody(sale)
            }.body<Sale>()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            throw e // or return a sensible default, but not emptyList()
        }
    }
}