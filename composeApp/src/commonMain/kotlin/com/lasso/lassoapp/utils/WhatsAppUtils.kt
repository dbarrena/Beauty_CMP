package com.lasso.lassoapp.utils

import io.ktor.http.encodeURLParameter

private const val MEXICO_COUNTRY_CODE = "52"
private const val MEXICO_NATIONAL_NUMBER_LENGTH = 10
private const val MIN_INTERNATIONAL_NUMBER_LENGTH = 8
private const val MAX_INTERNATIONAL_NUMBER_LENGTH = 15

private val PHONE_FORMATTING_CHARACTERS = setOf(' ', '\t', '\n', '\r', '(', ')', '-', '+')

fun normalizeWhatsAppPhone(phone: String): String? {
    val trimmedPhone = phone.trim()
    if (trimmedPhone.isEmpty() || trimmedPhone.any { it !in '0'..'9' && it !in PHONE_FORMATTING_CHARACTERS }) {
        return null
    }

    val digits = trimmedPhone.filter { it in '0'..'9' }
    val internationalNumber = if (digits.length == MEXICO_NATIONAL_NUMBER_LENGTH) {
        MEXICO_COUNTRY_CODE + digits
    } else {
        digits
    }

    return internationalNumber.takeIf {
        it.length in MIN_INTERNATIONAL_NUMBER_LENGTH..MAX_INTERNATIONAL_NUMBER_LENGTH && it.first() != '0'
    }
}

fun buildWhatsAppUrl(phone: String, message: String): String? {
    val normalizedPhone = normalizeWhatsAppPhone(phone) ?: return null
    return "https://wa.me/$normalizedPhone?text=${message.encodeURLParameter()}"
}
