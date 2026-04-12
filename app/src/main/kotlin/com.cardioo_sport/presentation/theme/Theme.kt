package com.cardioo_sport.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = White,
    primaryContainer = GreenContainer,
    onPrimaryContainer = Black,
    secondary = GreenAccent,
    onSecondary = Black,
    background = White,
    onBackground = Black,
    surface = White,
    onSurface = Black,
)

private val DarkScheme = darkColorScheme(
    primary = GreenAccent,
    onPrimary = Black,
    primaryContainer = ColorTokens.DarkSurface,
    onPrimaryContainer = White,
    secondary = GreenPrimary,
    onSecondary = Black,
    background = ColorTokens.DarkBackground,
    onBackground = White,
    surface = ColorTokens.DarkSurface,
    onSurface = White,
)

@Composable
fun CardiooSportTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        typography = Typography,
        content = content,
    )
}

private object ColorTokens {
    val DarkBackground = Color(0xFF0F0F10)
    val DarkSurface = Color(0xFF17171A)
}

