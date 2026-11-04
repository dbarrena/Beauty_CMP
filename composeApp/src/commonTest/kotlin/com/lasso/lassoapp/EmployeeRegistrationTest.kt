package com.lasso.lassoapp

import com.lasso.lassoapp.model.EmployeeRegistrationRequest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class EmployeeRegistrationTest {
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
