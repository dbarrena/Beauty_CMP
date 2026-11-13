package com.lasso.lassoapp.data.remote.endpoints

import com.lasso.lassoapp.data.remote.LASSO_API_URL
import com.lasso.lassoapp.data.remote.bodyOrThrow
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MasterAccess(@SerialName("is_master") val isMaster: Boolean)

@Serializable
data class SalesUsage(val used: Int, val limit: Int?, val remaining: Int?, @SerialName("can_create_sale") val canCreateSale: Boolean)

@Serializable
data class AdminPartner(
    val id: Int, val name: String, val email: String, val phone: String? = null,
    @SerialName("license_type") val licenseType: String? = null,
    @SerialName("license_expiry") val licenseExpiry: Long? = null,
    @SerialName("is_active") val isActive: Boolean,
    @SerialName("access_allowed") val accessAllowed: Boolean,
    @SerialName("sales_usage") val salesUsage: SalesUsage,
)

@Serializable
data class AdminPartnerPage(val page: Int, val total: Int, val partners: List<AdminPartner>)

// Required nullable fields ensure clearing phone/expiry sends JSON null even with encodeDefaults=false.
@Serializable
data class AdminPartnerInput(
    val name: String, val email: String, val phone: String?,
    @SerialName("license_type") val licenseType: String,
    @SerialName("license_expiry") val licenseExpiry: Long?,
    @SerialName("is_active") val isActive: Boolean,
)

@Serializable
data class AdminEmployee(
    val id: Int, val name: String, val email: String, val role: String, val disabled: Boolean,
    @SerialName("is_master") val isMaster: Boolean,
    @SerialName("can_see_sales") val canSeeSales: Boolean,
    @SerialName("is_displayed_on_calendar") val isDisplayedOnCalendar: Boolean,
)

@Serializable
data class AdminEmployeeInput(
    val name: String, val email: String, val role: String, val disabled: Boolean,
    @SerialName("can_see_sales") val canSeeSales: Boolean,
    @SerialName("is_displayed_on_calendar") val isDisplayedOnCalendar: Boolean,
    val password: String? = null,
)

interface AdminApi {
    suspend fun getMasterAccess(): MasterAccess
    suspend fun getAdminPartners(page: Int, search: String, plan: String?, active: Boolean?): AdminPartnerPage
    suspend fun getAdminPartner(id: Int): AdminPartner
    suspend fun saveAdminPartner(id: Int?, input: AdminPartnerInput): AdminPartner
    suspend fun getAdminEmployees(partnerId: Int): List<AdminEmployee>
    suspend fun saveAdminEmployee(partnerId: Int, id: Int?, input: AdminEmployeeInput): AdminEmployee
}

internal class KtorAdminApi(private val client: HttpClient) : AdminApi {
    private val base = LASSO_API_URL + "admin/"
    override suspend fun getMasterAccess(): MasterAccess = client.get(base + "me").bodyOrThrow()
    override suspend fun getAdminPartner(id: Int): AdminPartner = client.get(base + "partners/$id").bodyOrThrow()
    override suspend fun getAdminPartners(page: Int, search: String, plan: String?, active: Boolean?): AdminPartnerPage =
        client.get(base + "partners") {
            parameter("page", page)
            parameter("pageSize", 25)
            parameter("search", search)
            plan?.let { parameter("license_type", it) }
            active?.let { parameter("is_active", it) }
        }.bodyOrThrow()
    override suspend fun saveAdminPartner(id: Int?, input: AdminPartnerInput): AdminPartner =
        client.request(base + "partners" + (id?.let { "/$it" } ?: "")) {
            method = if (id == null) HttpMethod.Post else HttpMethod.Patch
            contentType(ContentType.Application.Json)
            setBody(input)
        }.bodyOrThrow()
    override suspend fun getAdminEmployees(partnerId: Int): List<AdminEmployee> =
        client.get(base + "partners/$partnerId/employees").bodyOrThrow()
    override suspend fun saveAdminEmployee(partnerId: Int, id: Int?, input: AdminEmployeeInput): AdminEmployee =
        client.request(base + "partners/$partnerId/employees" + (id?.let { "/$it" } ?: "")) {
            method = if (id == null) HttpMethod.Post else HttpMethod.Patch
            contentType(ContentType.Application.Json)
            setBody(input)
        }.bodyOrThrow()
}
