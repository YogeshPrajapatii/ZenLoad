package com.example.zenload.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AccentPurple,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Color.White,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkGlassSurface // We'll use this for Glass cards
)

private val LightColorScheme = lightColorScheme(
    primary = AccentBlue,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = Color.White,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    surfaceVariant = LightGlassSurface // We'll use this for Glass cards
)

@Composable
fun ZenLoadTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit
) {
    // We are forcing our custom colors by ignoring dynamicColor
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // This changes the top status bar color to match your app
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme, typography = Typography, content = content
    )
}