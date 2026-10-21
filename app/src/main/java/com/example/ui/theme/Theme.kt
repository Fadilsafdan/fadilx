package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FadixColorScheme = lightColorScheme(
    primary = FadixNavy,
    onPrimary = Color.White,
    primaryContainer = FadixNavySurface,
    onPrimaryContainer = Color.White,
    secondary = FadixYellow,
    onSecondary = FadixNavyDark,
    secondaryContainer = FadixYellowLight,
    onSecondaryContainer = FadixNavyDark,
    tertiary = FadixYellowDark,
    background = FadixBackground,
    onBackground = FadixTextPrimary,
    surface = FadixSurface,
    onSurface = FadixTextPrimary,
    surfaceVariant = FadixSurfaceVariant,
    onSurfaceVariant = FadixTextSecondary,
    outline = FadixBorder,
    error = FadixError,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FadixColorScheme,
        typography = Typography,
        content = content
    )
}
