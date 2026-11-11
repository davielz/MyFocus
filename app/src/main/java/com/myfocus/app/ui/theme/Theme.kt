package com.myfocus.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = FocusBluePrimary,
    secondary = FocusBlueSecondary,
    background = FocusDarkBackground,
    surface = FocusDarkSurface,
    surfaceVariant = FocusDarkCard,
    onPrimary = FocusDarkBackground,
    onBackground = FocusTextPrimary,
    onSurface = FocusTextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = FocusBluePrimary,
    secondary = FocusBlueSecondary,
    background = FocusTextPrimary,
    surface = FocusTextPrimary,
    surfaceVariant = FocusDarkCard,
    onPrimary = FocusDarkBackground,
    onBackground = FocusDarkBackground,
    onSurface = FocusDarkBackground
)

@Composable
fun MyFocusTheme(
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

