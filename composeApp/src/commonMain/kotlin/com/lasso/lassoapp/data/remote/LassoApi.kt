package com.lasso.lassoapp.data.remote

import com.lasso.lassoapp.data.local.session.SessionRepository
import com.lasso.lassoapp.model.CashClosure
import com.lasso.lassoapp.model.CreateCashClosureRequest
import com.lasso.lassoapp.model.Employee
import com.lasso.lassoapp.model.Home
import com.lasso.lassoapp.model.Login
import com.lasso.lassoapp.model.LoginResponse
import com.lasso.lassoapp.model.Service
import com.lasso.lassoapp.model.Product
import com.lasso.lassoapp.model.Sale
import com.lasso.lassoapp.model.SaleApiResponse
import com.lasso.lassoapp.model.SaleDetailEditApiRequest
import com.lasso.lassoapp.model.SaleEditDateApiRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.CancellationException

interface LassoApi {
    suspend fun getServices(): List<Service>
    suspend fun getProducts(): List<Product>
    suspend fun getSales(): List<SaleApiResponse>
    suspend fun getThisMonthSales(): List<SaleApiResponse>
    suspend fun getSalesBetweenDates(start: Long, end: Long): List<SaleApiResponse>
    suspend fun registerProduct(product: Product): Product
    suspend fun registerService(product: Service): Service
    suspend fun registerSale(sale: Sale): Sale

    suspend fun getSale(id: Int): SaleApiResponse?
    suspend fun getEmployeeById(id: Int): Employee?
    suspend fun getHome(): Home?
    suspend fun login(login: Login): LoginResponse
    suspend fun getOpenCashClosure(): CashClosure?

    suspend fun deleteSale(saleId: Int): String?

    suspend fun deleteSaleDetail(saleDetailId: Int): String?

    suspend fun editSaleDetail(saleDetailEditApiRequest: SaleDetailEditApiRequest): String?
    suspend fun createCashClosure(): String?

    suspend fun editSaleDate(saleEditDateRequest: SaleEditDateApiRequest): String?
}

class KtorLassoApi(
    private val client: HttpClient,
    private val sessionRepository: SessionRepository
) : LassoApi {
    companion object {
        private const val API_URL =
            "https://cdn.dbxprts.com:3000/api/"
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

    override suspend fun getThisMonthSales(): List<SaleApiResponse> {
        return try {
            println("KtorBeautyApi: getServices")
            val partnerId = sessionRepository.getPartnerId() ?: 0
            client.get(API_URL + "sales/current-month?partnerId=$partnerId").body()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()

            emptyList()
        }
    }

    override suspend fun getSalesBetweenDates(
        start: Long,
        end: Long
    ): List<SaleApiResponse> {
        return try {
            println("KtorBeautyApi: getSales")
            val partnerId = sessionRepository.getPartnerId() ?: 0
            client.get(API_URL + "sales/sales-between?partnerId=$partnerId&startEpoch=$start&endEpoch=$end")
                .body()
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

    override suspend fun getSale(id: Int): SaleApiResponse? {
        return try {
            println("KtorBeautyApi: getSale $id")
            client.get(API_URL + "sales/get/$id").body()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            null
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

    override suspend fun getOpenCashClosure(): CashClosure? {
        return try {
            println("KtorBeautyApi: getCashClosure")
            val partnerId = sessionRepository.getPartnerId() ?: 0
            client.get(API_URL + "cash_closure/open?partnerId=$partnerId").body()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            null
        }
    }

    override suspend fun deleteSale(saleId: Int): String? {
        return try {
            println("KtorBeautyApi: deleteSaleDetail")
            client.delete(API_URL + "sales/delete/$saleId").body()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            null
        }
    }

    override suspend fun deleteSaleDetail(saleDetailId: Int): String? {
        return try {
            println("KtorBeautyApi: deleteSaleDetail")
            client.delete(API_URL + "sales/delete/detail/$saleDetailId").body()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            null
        }
    }

    override suspend fun editSaleDetail(saleDetailEditApiRequest: SaleDetailEditApiRequest): String? {
        return try {
            println("KtorBeautyApi: editSaleDetail")

            client.post(API_URL + "sales/edit/detail") {
                contentType(ContentType.Application.Json)
                setBody(saleDetailEditApiRequest)
            }.body()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun createCashClosure(): String? {
        return try {
            println("KtorBeautyApi: getCashClosure")
            val partnerId = sessionRepository.getPartnerId() ?: 0
            client.post(API_URL + "cash_closure/create") {
                contentType(ContentType.Application.Json)
                setBody(CreateCashClosureRequest(partnerId, ""))
            }.body()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            null
        }
    }

    override suspend fun editSaleDate(saleEditDateRequest: SaleEditDateApiRequest): String? {
        return try {
            println("KtorBeautyApi: editSaleDate")
            client.post(API_URL + "sales/edit/date/${saleEditDateRequest.saleId}") {
                contentType(ContentType.Application.Json)
                setBody(saleEditDateRequest)
            }.body()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            null
        }
    }
}