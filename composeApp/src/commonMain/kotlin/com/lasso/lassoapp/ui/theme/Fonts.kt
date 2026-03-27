package com.lasso.lassoapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import lassoapp.composeapp.generated.resources.Inter_18pt_Bold
import lassoapp.composeapp.generated.resources.Inter_18pt_Light
import lassoapp.composeapp.generated.resources.Inter_18pt_Medium
import lassoapp.composeapp.generated.resources.Inter_18pt_Regular
import lassoapp.composeapp.generated.resources.Inter_18pt_SemiBold
import lassoapp.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font

@Composable
fun inter18Family(): FontFamily = FontFamily(
    Font(Res.font.Inter_18pt_Light, weight = FontWeight.Light),
    Font(Res.font.Inter_18pt_Regular, weight = FontWeight.Normal),
    Font(Res.font.Inter_18pt_Medium, weight = FontWeight.Medium),
    Font(Res.font.Inter_18pt_SemiBold, weight = FontWeight.SemiBold),
    Font(Res.font.Inter_18pt_Bold, weight = FontWeight.Bold),
)

@Composable
fun lassoTypography(): Typography {
    val inter = inter18Family()
    val base = Typography()
    return base.copy(
        displayLarge = base.displayLarge.copy(fontFamily = inter),
        displayMedium = base.displayMedium.copy(fontFamily = inter),
        displaySmall = base.displaySmall.copy(fontFamily = inter),
        headlineLarge = base.headlineLarge.copy(fontFamily = inter),
        headlineMedium = base.headlineMedium.copy(fontFamily = inter),
        headlineSmall = base.headlineSmall.copy(fontFamily = inter),
        titleLarge = base.titleLarge.copy(fontFamily = inter),
        titleMedium = base.titleMedium.copy(fontFamily = inter),
        titleSmall = base.titleSmall.copy(fontFamily = inter),
        bodyLarge = base.bodyLarge.copy(fontFamily = inter),
        bodyMedium = base.bodyMedium.copy(fontFamily = inter),
        bodySmall = base.bodySmall.copy(fontFamily = inter),
        labelLarge = base.labelLarge.copy(fontFamily = inter),
        labelMedium = base.labelMedium.copy(fontFamily = inter),
        labelSmall = base.labelSmall.copy(fontFamily = inter),
    )
}
