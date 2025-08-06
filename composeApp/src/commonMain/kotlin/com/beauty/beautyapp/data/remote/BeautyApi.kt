package com.beauty.beautyapp.data.remote

import com.beauty.beautyapp.data.local.session.SessionRepository
import com.beauty.beautyapp.model.Employee
import com.beauty.beautyapp.model.Home
import com.beauty.beautyapp.model.Login
import com.beauty.beautyapp.model.LoginResponse
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
    suspend fun getEmployeeById(id: Int): Employee?
    suspend fun getHome(): Home?
    suspend fun login(login: Login): LoginResponse
}

class KtorBeautyApi(private val client: HttpClient, private val sessionRepository: SessionRepository) : BeautyApi {
    companion object {
        private const val API_URL =
            "http://157.230.63.57:3000/api/"
    }

    override suspend fun getServices(): List<Service> {
        return try {
            println("KtorBeautyApi: getServices")
            val partnerId = sessionRepository.getPartnerId() ?: 0
            client.get(API_URL + "services/all?partnerId=$partnerId").body()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()

            emptyList()
        }
    }

    override suspend fun getProducts(): List<Product> {
        return try {
            println("KtorBeautyApi: getProducts")
            val partnerId = sessionRepository.getPartnerId() ?: 0
            client.get(API_URL + "products/all?partnerId=$partnerId").body()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getSales(): List<SaleApiResponse> {
        return try {
            println("KtorBeautyApi: getSales")
            val partnerId = sessionRepository.getPartnerId() ?: 0
            client.get(API_URL + "sales/all?partnerId=$partnerId").body()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun registerProduct(product: Product): Product {
        return try {
            println("KtorBeautyApi: registerProduct")
            val partnerId = sessionRepository.getPartnerId() ?: 0

            client.post(API_URL + "products/new") {
                contentType(ContentType.Application.Json)
                setBody(product.copy(partnerId = partnerId))
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
            val partnerId = sessionRepository.getPartnerId() ?: 0

            client.post(API_URL + "services/new") {
                contentType(ContentType.Application.Json)
                setBody(service.copy(partnerId = partnerId))
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
            val partnerId = sessionRepository.getPartnerId() ?: 0

            client.post(API_URL + "sales/new") {
                contentType(ContentType.Application.Json)
                setBody(sale.copy(partnerId = partnerId))
            }.body<Sale>()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            throw e // or return a sensible default, but not emptyList()
        }
    }

    override suspend fun getEmployeeById(id: Int): Employee? {
        return try {
            println("KtorBeautyApi: getEmployeeById")
            client.get(API_URL + "employees/get/" + id).body()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            null
        }
    }

    override suspend fun getHome(): Home? {
        return try {
            println("KtorBeautyApi: getHome")
            val partnerId = sessionRepository.getPartnerId() ?: 0
            client.get(API_URL + "home?partnerId=$partnerId").body()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            null
        }
    }

    override suspend fun login(
        login: Login
    ): LoginResponse {
        return try {
            println("KtorBeautyApi: login")
            client.post(API_URL + "auth/login") {
                contentType(ContentType.Application.Json)
                setBody(login)
            }.body<LoginResponse>()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            throw e // or return a sensible default, but not emptyList()
        }
    }
}