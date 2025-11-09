package com.beauty.beautyapp.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

val LightBeautyColorScheme = lightColorScheme(
    primary = BeautyPrimary,
    onPrimary = BeautyOnPrimary,
    primaryContainer = BeautyPrimaryLight,
    onPrimaryContainer = BeautyDarkGray,

    secondary = BeautySecondary,
    onSecondary = BeautyOnSecondary,
    secondaryContainer = BeautySecondaryLight,
    onSecondaryContainer = BeautyDarkGray,

    tertiary = BeautyTertiary,
    onTertiary = BeautyOnTertiary,
    tertiaryContainer = BeautyTertiaryLight,
    onTertiaryContainer = BeautyDarkGray,

    error = BeautyError,
    onError = BeautyOnError,

    background = BeautyBackground,
    onBackground = BeautyOnBackground,

    surface = BeautySurface,
    onSurface = BeautyOnSurface,
    surfaceVariant = BeautySurfaceVariant,
    onSurfaceVariant = BeautyOnSurfaceVariant,

    outline = BeautyGray,
    outlineVariant = BeautyLightGray,

    // Additional surface colors for better UI hierarchy
    surfaceContainer = BeautySecondaryLight,
    surfaceContainerHigh = BeautyPrimaryLight,
    surfaceContainerHighest = BeautyLightGray
)

val DarkBeautyColorScheme = darkColorScheme(
    primary = BeautyPrimaryLight,
    onPrimary = BeautyDarkGray,
    primaryContainer = BeautyPrimaryDark,
    onPrimaryContainer = BeautyOnPrimary,

    secondary = BeautySecondaryLight,
    onSecondary = BeautyDarkGray,
    secondaryContainer = BeautySecondaryDark,
    onSecondaryContainer = BeautyOnPrimary,

    tertiary = BeautyTertiaryLight,
    onTertiary = BeautyDarkGray,
    tertiaryContainer = BeautyTertiaryDark,
    onTertiaryContainer = BeautyOnPrimary,

    error = BeautyError,
    onError = BeautyOnError,

    background = BeautyDarkGray,
    onBackground = BeautyOnPrimary,

    surface = BeautyDarkGray,
    onSurface = BeautyOnPrimary,
    surfaceVariant = BeautySurfaceVariant,
    onSurfaceVariant = BeautyOnSurfaceVariant,

    outline = BeautyLightGray,
    outlineVariant = BeautyGray,

    // Additional surface colors for dark mode
    surfaceContainer = BeautyPrimaryDark,
    surfaceContainerHigh = BeautyDarkGray,
    surfaceContainerHighest = BeautyGray
)