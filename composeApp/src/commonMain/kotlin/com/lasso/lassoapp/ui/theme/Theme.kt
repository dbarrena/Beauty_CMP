package com.lasso.lassoapp.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

val LightLassoColorScheme = lightColorScheme(
    primary = LassoPrimary,
    onPrimary = LassoOnPrimary,
    primaryContainer = LassoPrimaryLight,
    onPrimaryContainer = LassoOnSurface,

    secondary = LassoSecondary,
    onSecondary = LassoOnSecondary,
    secondaryContainer = LassoSecondaryLight,
    onSecondaryContainer = LassoOnSurface,

    tertiary = LassoTertiary,
    onTertiary = LassoOnTertiary,
    tertiaryContainer = LassoTertiaryLight,
    onTertiaryContainer = LassoOnSurface,

    error = LassoError,
    onError = LassoOnError,

    background = LassoPageBackground,
    onBackground = LassoOnBackground,

    surface = LassoSurface,
    onSurface = LassoOnSurface,
    surfaceVariant = LassoSurfaceVariant,
    onSurfaceVariant = LassoOnSurfaceVariant,

    outline = LassoOutlineHairline,
    outlineVariant = LassoDivider,

    surfaceContainerLowest = LassoPageBackground,
    surfaceContainer = LassoSurfaceVariant,
    surfaceContainerHigh = LassoLightGray,
    surfaceContainerHighest = LassoDivider,
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

    surfaceContainerLowest = LassoDarkGray,
    surfaceContainer = LassoPrimaryDark,
    surfaceContainerHigh = LassoDarkGray,
    surfaceContainerHighest = LassoGray,
)
