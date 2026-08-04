package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Emerald500,
    onPrimary = Color.White,
    primaryContainer = Emerald800,
    onPrimaryContainer = Emerald100,
    secondary = Amber500,
    onSecondary = Color.Black,
    secondaryContainer = Amber700,
    onSecondaryContainer = Amber100,
    background = Slate900,
    onBackground = Color.White,
    surface = Slate800,
    onSurface = Color.White,
    surfaceVariant = Slate700,
    onSurfaceVariant = Slate200
)

private val LightColorScheme = lightColorScheme(
    primary = Emerald700,
    onPrimary = Color.White,
    primaryContainer = Emerald50,
    onPrimaryContainer = Emerald900,
    secondary = Amber600,
    onSecondary = Color.White,
    secondaryContainer = Amber50,
    onSecondaryContainer = Amber700,
    background = Slate100,
    onBackground = Slate900,
    surface = Color.White,
    onSurface = Slate800,
    surfaceVariant = Slate50,
    onSurfaceVariant = Slate600
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

