package com.lasso.lassoapp.data.remote

import com.lasso.lassoapp.data.local.session.SessionRepository
import com.lasso.lassoapp.model.CashClosure
import com.lasso.lassoapp.model.AppointmentCalendarResponse
import com.lasso.lassoapp.model.AppointmentWriteRequest
import com.lasso.lassoapp.model.Client
import com.lasso.lassoapp.model.ClientWriteRequest
import com.lasso.lassoapp.model.MessageResponse
import com.lasso.lassoapp.model.SavedAppointment
import com.lasso.lassoapp.model.normalized
import com.lasso.lassoapp.model.CashClosureRecordsResponse
import com.lasso.lassoapp.model.CommissionCalculationResponse
import com.lasso.lassoapp.model.CreateCashClosureRequest
import com.lasso.lassoapp.model.Employee
import com.lasso.lassoapp.model.EmployeeRegistrationRequest
import com.lasso.lassoapp.model.Home
import com.lasso.lassoapp.model.Login
import com.lasso.lassoapp.model.LoginResponse
import com.lasso.lassoapp.model.Service
import com.lasso.lassoapp.model.Product
import com.lasso.lassoapp.model.ProductCategory
import com.lasso.lassoapp.model.Sale
import com.lasso.lassoapp.model.SaleApiResponse
import com.lasso.lassoapp.model.SaleDetailEditApiRequest
import com.lasso.lassoapp.model.SaleEditDateApiRequest
import com.lasso.lassoapp.model.SalesByProductCategoryApiResponse
import com.lasso.lassoapp.model.TopSellersResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.utils.io.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Serializable
private data class ApiErrorResponse(val error: String? = null, val message: String? = null)

class LassoApiException(
    message: String,
    val statusCode: Int,
) : Exception(message)

private suspend inline fun <reified T> HttpResponse.bodyOrThrow(): T {
    if (status.isSuccess()) return body()
    val apiError = runCatching { body<ApiErrorResponse>() }.getOrNull()
    throw LassoApiException(
        message = apiError?.error ?: apiError?.message ?: "Error de servidor (${status.value})",
        statusCode = status.value,
    )
}

interface LassoApi {
    suspend fun getClients(): List<Client>
    suspend fun registerClient(request: ClientWriteRequest): Client
    suspend fun editClient(id: Int, request: ClientWriteRequest): Client
    suspend fun getAppointmentCalendar(startEpoch: Long, endEpoch: Long): AppointmentCalendarResponse
    suspend fun createAppointment(request: AppointmentWriteRequest): SavedAppointment
    suspend fun editAppointment(id: Int, request: AppointmentWriteRequest): SavedAppointment
    suspend fun deleteAppointment(id: Int): MessageResponse
    suspend fun getServices(): List<Service>
    suspend fun getProducts(): List<Product>
    suspend fun getSalesBetweenDates(start: Long, end: Long): List<SaleApiResponse>
    suspend fun registerProduct(product: Product): Product
    suspend fun registerService(service: Service): Service
    suspend fun registerSale(sale: Sale): Sale
    suspend fun registerEmployee(employee: EmployeeRegistrationRequest): Employee

    suspend fun getSale(id: Int): SaleApiResponse?
    suspend fun getEmployeeById(id: Int): Employee?
    suspend fun getEmployees(): List<Employee>
    suspend fun getHome(
        startMonthEpoch: Long,
        endMonthEpoch: Long,
        startDayEpoch: Long,
        endDayEpoch: Long,
        startWeekEpoch: Long,
        endWeekEpoch: Long
    ): Home?

    suspend fun getHomeTopSellers(): TopSellersResponse?
    suspend fun login(login: Login): LoginResponse
    suspend fun logout()
    suspend fun getOpenCashClosure(): CashClosure?
    suspend fun createCashClosure(): String?
    suspend fun getCashClosureRecords(): List<CashClosureRecordsResponse>

    suspend fun getProductCategories(): List<ProductCategory>
    suspend fun registerProductCategory(productCategory: ProductCategory): ProductCategory
    suspend fun getSalesByProductCategory(
        start: Long,
        end: Long,
        categoryId: Int
    ): SalesByProductCategoryApiResponse?

    suspend fun calculateCommissions(
        employeeId: Int,
        start: Long,
        end: Long
    ): List<CommissionCalculationResponse>

    suspend fun editSaleDate(saleEditDateRequest: SaleEditDateApiRequest): String?
    suspend fun editSaleDetail(saleDetailEditApiRequest: SaleDetailEditApiRequest): String?
    suspend fun editService(service: Service): Service
    suspend fun editProduct(product: Product): Product
    suspend fun editEmployee(employee: Employee): String?

    suspend fun deleteSale(saleId: Int): String?

    suspend fun deleteSaleDetail(saleDetailId: Int): String?
    suspend fun disableService(service: Service): String?
    suspend fun diableProduct(product: Product): String?
}


class KtorLassoApi(
    private val client: HttpClient,
    private val sessionRepository: SessionRepository
) : LassoApi {
    companion object {
        private const val LEGACY_API_URL =
            "https://cdn.dbxprts.com:3000/api/"
        private const val LASSO_API_URL = "https://cdn.dbxprts.com:3000/lasso/api/"
        private const val SECURE_SALES_URL = LASSO_API_URL + "sales/"
    }

    private suspend fun requirePartnerId(): Int = sessionRepository.getPartnerId()
        ?: throw LassoApiException("No hay una sesión activa", 401)

    override suspend fun getClients(): List<Client> {
        val partnerId = requirePartnerId()
        return client.get(LEGACY_API_URL + "clients/all?partnerId=$partnerId").bodyOrThrow()
    }

    override suspend fun registerClient(request: ClientWriteRequest): Client {
        val partnerId = requirePartnerId()
        return client.post(LEGACY_API_URL + "clients/new") {
            contentType(ContentType.Application.Json)
            setBody(request.normalized().copy(partnerId = partnerId))
        }.bodyOrThrow()
    }

    override suspend fun editClient(id: Int, request: ClientWriteRequest): Client {
        return client.post(LEGACY_API_URL + "clients/edit") {
            contentType(ContentType.Application.Json)
            setBody(request.normalized().copy(id = id))
        }.bodyOrThrow()
    }

    override suspend fun getAppointmentCalendar(
        startEpoch: Long,
        endEpoch: Long,
    ): AppointmentCalendarResponse {
        val partnerId = requirePartnerId()
        return client.get(
            LEGACY_API_URL + "appointments/calendar?partnerId=$partnerId&startEpoch=$startEpoch&endEpoch=$endEpoch"
        ).bodyOrThrow()
    }

    override suspend fun createAppointment(request: AppointmentWriteRequest): SavedAppointment {
        val partnerId = requirePartnerId()
        return client.post(LEGACY_API_URL + "appointments/new") {
            contentType(ContentType.Application.Json)
            setBody(request.normalized().copy(partnerId = partnerId))
        }.bodyOrThrow()
    }

    override suspend fun editAppointment(
        id: Int,
        request: AppointmentWriteRequest,
    ): SavedAppointment {
        val partnerId = requirePartnerId()
        return client.post(LEGACY_API_URL + "appointments/edit/$id") {
            contentType(ContentType.Application.Json)
            setBody(request.normalized().copy(partnerId = partnerId))
        }.bodyOrThrow()
    }

    override suspend fun deleteAppointment(id: Int): MessageResponse {
        val partnerId = requirePartnerId()
        return client.delete(LEGACY_API_URL + "appointments/delete/$id?partnerId=$partnerId").bodyOrThrow()
    }

    override suspend fun getServices(): List<Service> {
        return try {
            println("KtorBeautyApi: getServices")
            val partnerId = sessionRepository.getPartnerId() ?: 0
            client.get(LEGACY_API_URL + "services/all?partnerId=$partnerId").body()
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
            client.get(LEGACY_API_URL + "products/all?partnerId=$partnerId").body()
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
            client.get(SECURE_SALES_URL + "sales-between?startEpoch=$start&endEpoch=$end")
                .bodyOrThrow()
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

            client.post(LEGACY_API_URL + "products/new") {
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

            client.post(LEGACY_API_URL + "services/new") {
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
            client.post(SECURE_SALES_URL + "new") {
                contentType(ContentType.Application.Json)
                setBody(sale.copy(partnerId = null))
            }.bodyOrThrow<Sale>()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            throw e // or return a sensible default, but not emptyList()
        }
    }

    override suspend fun registerEmployee(employee: EmployeeRegistrationRequest): Employee {
        return try {
            println("KtorBeautyApi: registerEmployee")
            val partnerId = sessionRepository.getPartnerId() ?: 0

            client.post(LEGACY_API_URL + "employees/add") {
                contentType(ContentType.Application.Json)
                setBody(employee.copy(partnerId = partnerId))
            }.body<Employee>()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun getSale(id: Int): SaleApiResponse? {
        return try {
            println("KtorBeautyApi: getSale $id")
            client.get(SECURE_SALES_URL + "get/$id").bodyOrThrow()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            null
        }
    }

    override suspend fun getEmployeeById(id: Int): Employee? {
        return try {
            println("KtorBeautyApi: getEmployeeById")
            client.get(LEGACY_API_URL + "employees/get/" + id).body()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            null
        }
    }

    override suspend fun getEmployees(): List<Employee> {
        return try {
            println("KtorBeautyApi: getEmployees")
            val partnerId = sessionRepository.getPartnerId() ?: 0
            client.get(LEGACY_API_URL + "employees/all?partnerId=$partnerId").body()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getHome(
        startMonthEpoch: Long,
        endMonthEpoch: Long,
        startDayEpoch: Long,
        endDayEpoch: Long,
        startWeekEpoch: Long,
        endWeekEpoch: Long
    ): Home? {
        return try {
            println("KtorBeautyApi: getHome")
            val partnerId = sessionRepository.getPartnerId() ?: 0
            client.get(LEGACY_API_URL + "home?partnerId=$partnerId&startMonthEpoch=$startMonthEpoch&endMonthEpoch=$endMonthEpoch&startDayEpoch=$startDayEpoch&endDayEpoch=$endDayEpoch&startWeekEpoch=$startWeekEpoch&endWeekEpoch=$endWeekEpoch")
                .body()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            null
        }
    }

    override suspend fun getHomeTopSellers(): TopSellersResponse? {
        return try {
            println("KtorBeautyApi: getHomeTopSellers")
            val partnerId = sessionRepository.getPartnerId() ?: 0
            client.get(LEGACY_API_URL + "home/top-sellers?partnerId=$partnerId").body()
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
            client.post(LASSO_API_URL + "auth/login") {
                contentType(ContentType.Application.Json)
                setBody(login)
            }.bodyOrThrow<LoginResponse>()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            throw e // or return a sensible default, but not emptyList()
        }
    }

    override suspend fun logout() {
        client.post(LASSO_API_URL + "auth/logout").bodyOrThrow<MessageResponse>()
    }

    override suspend fun getOpenCashClosure(): CashClosure? {
        return try {
            println("KtorBeautyApi: getCashClosure")
            val partnerId = sessionRepository.getPartnerId() ?: 0
            client.get(LEGACY_API_URL + "cash_closure/open?partnerId=$partnerId").body()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            null
        }
    }

    override suspend fun deleteSale(saleId: Int): String? {
        return try {
            println("KtorBeautyApi: deleteSale")
            client.delete(SECURE_SALES_URL + "delete/$saleId").bodyOrThrow<MessageResponse>().message
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            null
        }
    }

    override suspend fun deleteSaleDetail(saleDetailId: Int): String? {
        return try {
            println("KtorBeautyApi: deleteSaleDetail")
            client.delete(SECURE_SALES_URL + "delete/detail/$saleDetailId").bodyOrThrow<MessageResponse>().message
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            null
        }
    }

    override suspend fun disableService(service: Service): String? {
        return try {
            println("KtorBeautyApi: disableService")

            client.post(LEGACY_API_URL + "services/disable") {
                contentType(ContentType.Application.Json)
                setBody(service)
            }.body()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun diableProduct(product: Product): String? {
        return try {
            println("KtorBeautyApi: disableProducts")

            client.post(LEGACY_API_URL + "products/disable") {
                contentType(ContentType.Application.Json)
                setBody(product)
            }.body()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun editSaleDetail(saleDetailEditApiRequest: SaleDetailEditApiRequest): String? {
        return try {
            println("KtorBeautyApi: editSaleDetail")

            client.post(SECURE_SALES_URL + "edit/detail") {
                contentType(ContentType.Application.Json)
                setBody(saleDetailEditApiRequest)
            }.bodyOrThrow<MessageResponse>().message
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun editService(service: Service): Service {
        return try {
            println("KtorBeautyApi: editService")
            val partnerId = sessionRepository.getPartnerId() ?: 0

            client.post(LEGACY_API_URL + "services/edit") {
                contentType(ContentType.Application.Json)
                setBody(service)
            }.body<Service>()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            throw e // or return a sensible default, but not emptyList()
        }
    }

    override suspend fun editProduct(product: Product): Product {
        return try {
            println("KtorBeautyApi: editProduct")
            val partnerId = sessionRepository.getPartnerId() ?: 0

            client.post(LEGACY_API_URL + "products/edit") {
                contentType(ContentType.Application.Json)
                setBody(product)
            }.body<Product>()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            throw e // or return a sensible default, but not emptyList()
        }
    }

    override suspend fun editEmployee(employee: Employee): String? {
        return try {
            println("KtorBeautyApi: editEmployee")
            client.post(LEGACY_API_URL + "employees/edit/${employee.id}") {
                contentType(ContentType.Application.Json)
                setBody(employee)
            }.body()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            null
        }
    }

    override suspend fun createCashClosure(): String? {
        return try {
            println("KtorBeautyApi: getCashClosure")
            val partnerId = sessionRepository.getPartnerId() ?: 0
            client.post(LEGACY_API_URL + "cash_closure/create") {
                contentType(ContentType.Application.Json)
                setBody(CreateCashClosureRequest(partnerId, ""))
            }.body()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            null
        }
    }

    override suspend fun getCashClosureRecords(): List<CashClosureRecordsResponse> {
        return try {
            println("KtorBeautyApi: getCashClosure")
            val partnerId = sessionRepository.getPartnerId() ?: 0
            client.get(LEGACY_API_URL + "cash_closure/all?partnerId=$partnerId").body()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            listOf()
        }
    }

    override suspend fun editSaleDate(saleEditDateRequest: SaleEditDateApiRequest): String? {
        return try {
            println("KtorBeautyApi: editSaleDate")
            client.post(SECURE_SALES_URL + "edit/${saleEditDateRequest.saleId}") {
                contentType(ContentType.Application.Json)
                setBody(saleEditDateRequest)
            }.bodyOrThrow<MessageResponse>().message
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            null
        }
    }

    override suspend fun getProductCategories(): List<ProductCategory> {
        return try {
            println("KtorBeautyApi: getProductCategories")
            val partnerId = sessionRepository.getPartnerId() ?: 0
            client.get(LEGACY_API_URL + "product_categories/all?partnerId=$partnerId").body()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            listOf()
        }
    }

    override suspend fun registerProductCategory(productCategory: ProductCategory): ProductCategory {
        return try {
            println("KtorBeautyApi: registerProductCategory")
            val partnerId = sessionRepository.getPartnerId() ?: 0

            client.post(LEGACY_API_URL + "product_categories/new") {
                contentType(ContentType.Application.Json)
                setBody(productCategory.copy(partnerId = partnerId))
            }.body<ProductCategory>()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            throw e // or return a sensible default, but not emptyList()
        }
    }

    override suspend fun getSalesByProductCategory(
        start: Long,
        end: Long,
        categoryId: Int
    ): SalesByProductCategoryApiResponse? {
        return try {
            println("KtorBeautyApi: getSalesByProductCategory")
            val partnerId = sessionRepository.getPartnerId() ?: 0
            client.get(
                LEGACY_API_URL + "reports/products-by-category?" +
                        "partnerId=$partnerId" +
                        "&startEpoch=$start" +
                        "&endEpoch=$end" +
                        "&categoryId=$categoryId"
            )
                .body()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            null
        }
    }

    override suspend fun calculateCommissions(
        employeeId: Int,
        start: Long,
        end: Long
    ): List<CommissionCalculationResponse> {
        return try {
            println("KtorBeautyApi: calculateCommissions")
            val partnerId = sessionRepository.getPartnerId() ?: 0
            client.get(
                LEGACY_API_URL + "commissions/calculate?" +
                        "partnerId=$partnerId" +
                        "&employeeId=$employeeId" +
                        "&startEpoch=$start" +
                        "&endEpoch=$end"
            ).body()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            emptyList()
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun currentMonthRange(): Pair<Long, Long> {
        val timeZone = TimeZone.currentSystemDefault()
        val currentDate = Instant.fromEpochMilliseconds(Clock.System.now().toEpochMilliseconds())
            .toLocalDateTime(timeZone)
            .date
        val startDate = LocalDate(currentDate.year, currentDate.month, 1)
        return startDate.atStartOfDayIn(timeZone).toEpochMilliseconds() to
            startDate.plus(1, DateTimeUnit.MONTH).atStartOfDayIn(timeZone).toEpochMilliseconds()
    }
}
