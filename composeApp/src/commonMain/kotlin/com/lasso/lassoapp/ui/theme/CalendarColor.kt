package com.lasso.lassoapp.ui.theme

import androidx.compose.ui.graphics.Color

fun calendarColorOrPrimary(hex: String?): Color =
    hex?.takeIf { it.matches(Regex("#[0-9a-fA-F]{6}")) }
        ?.drop(1)?.toLongOrNull(16)?.let { Color(0xFF000000L or it) } ?: LassoPrimary
