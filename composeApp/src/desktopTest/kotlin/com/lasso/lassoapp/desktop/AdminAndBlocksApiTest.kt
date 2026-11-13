package com.lasso.lassoapp.desktop

import com.lasso.lassoapp.data.remote.KtorLassoApi
import com.lasso.lassoapp.data.remote.LassoApiException
import com.lasso.lassoapp.data.remote.endpoints.*
import com.lasso.lassoapp.model.*
import com.sun.net.httpserver.HttpServer
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.plugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import org.junit.Test
import java.net.InetSocketAddress
import java.util.Collections
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.test.*

/** Run the real API delegates against the committed server's HTTP contract. */
class AdminAndBlocksApiTest {
    private data class Request(val method: String, val path: String, val query: String?, val body: String)
    @Test fun adminAndNamedBlocksUseCorrectRoutesMethodsPayloadsAndErrors() = runBlocking {
        val partner = AdminPartner(7, "Salón", "salon@example.test", licenseType = "premium", isActive = true,
            accessAllowed = true, salesUsage = SalesUsage(120, null, null, true))
        val employee = AdminEmployee(8, "Ana", "ana@example.test", "empleado", false, false, false, true)
        val block = CalendarBlock(50, 8, 100, 200, title = "Comida")
        val requests = Collections.synchronizedList(mutableListOf<Request>())
        val deny = AtomicBoolean(false)
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/lasso/api/") { exchange ->
            val request = Request(exchange.requestMethod, exchange.requestURI.path,
                exchange.requestURI.rawQuery, exchange.requestBody.bufferedReader().use { it.readText() })
            requests += request
            val response = when {
                deny.get() -> """{"error":"Acceso exclusivo para el administrador de la plataforma"}"""
                request.path.endsWith("/admin/me") -> Json.encodeToString(MasterAccess(true))
                request.path.endsWith("/admin/partners") && request.method == "GET" -> Json.encodeToString(AdminPartnerPage(2, 26, listOf(partner)))
                request.path.contains("/employees") && request.method == "GET" -> Json.encodeToString(listOf(employee))
                request.path.contains("/employees") -> Json.encodeToString(employee)
                request.path.contains("/admin/partners") -> Json.encodeToString(partner)
                request.path.endsWith("/appointments/calendar") -> Json.encodeToString(AppointmentCalendarResponse(0, 300,
                    listOf(EmployeeAppointmentSchedule(8, "Ana", blockedHours = listOf(block)))))
                request.method == "DELETE" -> """{"message":"Eliminado"}"""
                else -> Json.encodeToString(block)
            }
            val bytes = response.toByteArray(Charsets.UTF_8)
            exchange.responseHeaders.set("Content-Type", "application/json")
            exchange.sendResponseHeaders(if (deny.get()) 403 else if (request.method == "POST") 201 else 200, bytes.size.toLong())
            exchange.responseBody.use { it.write(bytes) }
        }
        server.start()
        val client = HttpClient(OkHttp) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
        // Redirect all requests locally while retaining the production routes and API delegates.
        client.plugin(HttpSend).intercept { request ->
            request.url.protocol = URLProtocol.HTTP
            request.url.host = "127.0.0.1"
            request.url.port = server.address.port
            execute(request)
        }
        try {
            val api = KtorLassoApi(client)
            assertTrue(api.getMasterAccess().isMaster)
            assertEquals(partner, api.getAdminPartners(2, "Salón", "premium", false).partners.single())
            assertTrue(requests.last().query!!.contains("license_type=premium"))
            assertTrue(requests.last().query!!.contains("is_active=false"))
            assertEquals(partner, api.getAdminPartner(7))
            assertEquals("/lasso/api/admin/partners/7", requests.last().path)
            val input = AdminPartnerInput(partner.name, partner.email, null, "premium", null, true)
            api.saveAdminPartner(null, input)
            assertEquals("POST", requests.last().method)
            api.saveAdminPartner(7, input)
            assertEquals("PATCH", requests.last().method)
            val partnerBody = Json.parseToJsonElement(requests.last().body).jsonObject
            assertEquals(JsonNull, partnerBody["license_expiry"])
            assertEquals(JsonPrimitive("premium"), partnerBody["license_type"])
            assertEquals(listOf(employee), api.getAdminEmployees(7))
            val employeeInput = AdminEmployeeInput("Ana", employee.email, "empleado", false, false, true)
            api.saveAdminEmployee(7, null, employeeInput.copy(password = "password123"))
            assertEquals("POST", requests.last().method)
            assertEquals("/lasso/api/admin/partners/7/employees", requests.last().path)
            api.saveAdminEmployee(7, 8, employeeInput)
            assertEquals("PATCH", requests.last().method)
            assertEquals("/lasso/api/admin/partners/7/employees/8", requests.last().path)
            assertFalse(Json.parseToJsonElement(requests.last().body).jsonObject.containsKey("password"))
            assertEquals("Comida", api.getAppointmentCalendar(0, 300).employees.single().blockedHours.single().title)
            api.createCalendarBlock(CalendarBlockWriteRequest(8, 100, 200, title = "Comida"))
            assertEquals("POST", requests.last().method)
            assertEquals(JsonPrimitive("Comida"), Json.parseToJsonElement(requests.last().body).jsonObject["title"])
            api.editCalendarBlock(50, CalendarBlockWriteRequest(8, 100, 200, title = null))
            assertEquals("PATCH", requests.last().method)
            assertEquals("/lasso/api/appointments/blocked-hours/50", requests.last().path)
            assertEquals(JsonNull, Json.parseToJsonElement(requests.last().body).jsonObject["title"])
            api.deleteCalendarBlock(50)
            assertEquals("DELETE", requests.last().method)
            deny.set(true)
            val error = assertFailsWith<LassoApiException> { api.getMasterAccess() }
            assertEquals(403, error.statusCode)
        } finally { client.close(); server.stop(0) }
    }
}
