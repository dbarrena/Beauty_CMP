package com.lasso.lassoapp.data.remote.endpoints

import com.lasso.lassoapp.data.remote.LASSO_API_URL
import com.lasso.lassoapp.data.remote.bodyOrThrow
import com.lasso.lassoapp.data.remote.recoverWith
import com.lasso.lassoapp.model.Employee
import com.lasso.lassoapp.model.EmployeeRegistrationRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface EmployeesApi {
    suspend fun registerEmployee(employee: EmployeeRegistrationRequest): Employee
    suspend fun getEmployees(): List<Employee>
    suspend fun editEmployee(employee: Employee): Employee
}

@Serializable
private data class EmployeeEditRequest(
    val name: String,
    val email: String,
    val password: String? = null,
    val role: String,
    @SerialName("product_commission_percentage") val productCommissionPercentage: String?,
    @SerialName("service_commission_percentage") val serviceCommissionPercentage: String?,
)

internal class KtorEmployeesApi(private val client: HttpClient) : EmployeesApi {
    override suspend fun registerEmployee(employee: EmployeeRegistrationRequest): Employee =
        client.post(LASSO_API_URL + "employees/add") {
            contentType(ContentType.Application.Json)
            setBody(employee.copy(partnerId = null))
        }.bodyOrThrow()

    override suspend fun getEmployees(): List<Employee> = recoverWith(emptyList()) {
        client.get(LASSO_API_URL + "employees/all").bodyOrThrow()
    }

    override suspend fun editEmployee(employee: Employee): Employee =
        client.post(LASSO_API_URL + "employees/edit/${employee.id}") {
            contentType(ContentType.Application.Json)
            setBody(
                EmployeeEditRequest(
                    name = employee.name,
                    email = employee.email,
                    password = employee.password,
                    role = employee.role,
                    productCommissionPercentage = employee.productCommissionPercentage,
                    serviceCommissionPercentage = employee.serviceCommissionPercentage,
                ),
            )
        }.bodyOrThrow()
}
