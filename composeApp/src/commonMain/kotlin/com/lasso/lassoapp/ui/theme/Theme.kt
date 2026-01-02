package com.lasso.lassoapp.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

val LightLassoColorScheme = lightColorScheme(
    primary = LassoPrimary,
    onPrimary = LassoOnPrimary,
    primaryContainer = LassoPrimaryLight,
    onPrimaryContainer = LassoDarkGray,

    secondary = LassoSecondary,
    onSecondary = LassoOnSecondary,
    secondaryContainer = LassoSecondaryLight,
    onSecondaryContainer = LassoDarkGray,

    tertiary = LassoTertiary,
    onTertiary = LassoOnTertiary,
    tertiaryContainer = LassoTertiaryLight,
    onTertiaryContainer = LassoDarkGray,

    error = LassoError,
    onError = LassoOnError,

    background = LassoBackground,
    onBackground = LassoOnBackground,

    surface = LassoSurface,
    onSurface = LassoOnSurface,
    surfaceVariant = LassoSurfaceVariant,
    onSurfaceVariant = LassoOnSurfaceVariant,

    outline = LassoGray,
    outlineVariant = LassoLightGray,

    // Additional surface colors for better UI hierarchy
    surfaceContainer = LassoSecondaryLight,
    surfaceContainerHigh = LassoPrimaryLight,
    surfaceContainerHighest = LassoLightGray
)

val DarkLassoColorScheme = darkColorScheme(
    primary = LassoPrimaryLight,
    onPrimary = LassoDarkGray,
    primaryContainer = LassoPrimaryDark,
    onPrimaryContainer = LassoOnPrimary,

    secondary = LassoSecondaryLight,
    onSecondary = LassoDarkGray,
    secondaryContainer = LassoSecondaryDark,
    onSecondaryContainer = LassoOnPrimary,

    tertiary = LassoTertiaryLight,
    onTertiary = LassoDarkGray,
    tertiaryContainer = LassoTertiaryDark,
    onTertiaryContainer = LassoOnPrimary,

    error = LassoError,
    onError = LassoOnError,

    background = LassoDarkGray,
    onBackground = LassoOnPrimary,

    surface = LassoDarkGray,
    onSurface = LassoOnPrimary,
    surfaceVariant = LassoSurfaceVariant,
    onSurfaceVariant = LassoOnSurfaceVariant,

    outline = LassoLightGray,
    outlineVariant = LassoGray,

    // Additional surface colors for dark mode
    surfaceContainer = LassoPrimaryDark,
    surfaceContainerHigh = LassoDarkGray,
    surfaceContainerHighest = LassoGray
)