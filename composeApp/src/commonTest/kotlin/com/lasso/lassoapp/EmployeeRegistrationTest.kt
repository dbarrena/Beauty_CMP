package com.lasso.lassoapp

import com.lasso.lassoapp.model.EmployeeRegistrationRequest
import com.lasso.lassoapp.model.Employee
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.JsonNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class EmployeeRegistrationTest {
    @Test
    fun defaultConfigurationIsIncludedInRegistrationJson() {
        val request = EmployeeRegistrationRequest("Ana", "ana@example.com", "123456", "empleado")
        val body = Json.parseToJsonElement(Json.encodeToString(request)).jsonObject

        assertEquals(JsonNull, body["calendar_color"])
        assertEquals("true", body["is_displayed_on_calendar"]?.jsonPrimitive?.content)
        assertEquals("false", body["can_see_sales"]?.jsonPrimitive?.content)
    }

    @Test
    fun customConfigurationIsIncludedInRegistrationJson() {
        val request = EmployeeRegistrationRequest(
            "Ana", "ana@example.com", "123456", "empleado",
            calendarColor = "#00D1AD", isDisplayedOnCalendar = false, canSeeSales = true
        )
        val body = Json.parseToJsonElement(Json.encodeToString(request)).jsonObject

        assertEquals("#00D1AD", body["calendar_color"]?.jsonPrimitive?.content)
        assertEquals("false", body["is_displayed_on_calendar"]?.jsonPrimitive?.content)
        assertEquals("true", body["can_see_sales"]?.jsonPrimitive?.content)
    }

    @Test
    fun employeeConfigurationIsDecodedWithBackwardCompatibleDefaults() {
        val legacy = """{"id":1,"name":"Ana","email":"ana@example.com","role":"empleado","partner_id":7,"updated_at":null}"""
        val employee = Json.decodeFromString<Employee>(legacy)
        assertEquals(null, employee.calendarColor)
        assertEquals(true, employee.isDisplayedOnCalendar)
        assertEquals(false, employee.canSeeSales)

        val configured = Json.decodeFromString<Employee>(legacy.dropLast(1) +
            ""","calendar_color":"#FF7A5F","is_displayed_on_calendar":false,"can_see_sales":true}""")
        assertEquals("#FF7A5F", configured.calendarColor)
        assertEquals(false, configured.isDisplayedOnCalendar)
        assertEquals(true, configured.canSeeSales)
    }

    @Test
    fun defaultCommissionsAreIncludedInRegistrationJson() {
        val request = EmployeeRegistrationRequest("Ana", "ana@example.com", "123456", "empleado")
        val body = Json.parseToJsonElement(Json.encodeToString(request.copy(partnerId = null))).jsonObject

        assertEquals("0", body["product_commission_percentage"]?.jsonPrimitive?.content)
        assertEquals("0", body["service_commission_percentage"]?.jsonPrimitive?.content)
        assertFalse("partner_id" in body)
    }

    @Test
    fun customCommissionsArePreservedInRegistrationJson() {
        val request = EmployeeRegistrationRequest(
            "Ana", "ana@example.com", "123456", "empleado",
            productCommissionPercentage = "10", serviceCommissionPercentage = "12.5"
        )
        val body = Json.parseToJsonElement(Json.encodeToString(request)).jsonObject

        assertEquals("10", body["product_commission_percentage"]?.jsonPrimitive?.content)
        assertEquals("12.5", body["service_commission_percentage"]?.jsonPrimitive?.content)
    }
}
